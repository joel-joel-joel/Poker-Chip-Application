package com.joelcode.pokerchipsapplication.service;

import com.joelcode.pokerchipsapplication.dto.other.ChipDistribution;
import com.joelcode.pokerchipsapplication.dto.other.HourlyTransactionActivity;
import com.joelcode.pokerchipsapplication.dto.other.PlayerChipStatistics;
import com.joelcode.pokerchipsapplication.entities.ChipTransaction;
import com.joelcode.pokerchipsapplication.entities.Room;
import com.joelcode.pokerchipsapplication.entities.RoomPlayer;
import com.joelcode.pokerchipsapplication.repositories.ChipTransactionRepo;
import com.joelcode.pokerchipsapplication.repositories.RoomPlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class ChipTransactionService {
    @Autowired
    private ChipTransactionRepo chipTransactionRepo;

    @Autowired
    private RoomPlayerService roomPlayerService;

    private SimpMessagingTemplate simpMessagingTemplate;


    // ==== TRANSACTION CREATION ====
    public ChipTransaction recordTransfer(RoomPlayer fromPlayer, RoomPlayer toPlayer,
                                         Integer amount, ChipTransaction.transactionType type) {
        ChipTransaction transaction = new ChipTransaction(fromPlayer, toPlayer, fromPlayer.getRoom(), amount);
        transaction.setTransactionType(type);

        return chipTransactionRepo.save(transaction);
    }

    public ChipTransaction recordCall (RoomPlayer fromPlayer, RoomPlayer toPlayer, Integer amount) {
        return recordTransfer(fromPlayer, toPlayer, amount, ChipTransaction.transactionType.CALL);
    }

    public ChipTransaction recordRaise (RoomPlayer fromPlayer, RoomPlayer toPlayer, Integer amount) {
        return recordTransfer(fromPlayer, toPlayer, amount, ChipTransaction.transactionType.RAISE);
    }

    public ChipTransaction recordBuyin (RoomPlayer player, Integer amount) {
        ChipTransaction transaction = new ChipTransaction(null, player, player.getRoom(), amount);
        transaction.setTransactionType(ChipTransaction.transactionType.BUYIN);

        return chipTransactionRepo.save(transaction);
    }

    // ==== COMPREHENSIVE CHIP TRANSFER WITH AUDIT ====

    public void transferChips(UUID fromPlayerId, UUID toPlayerId,int amount, ChipTransaction.transactionType type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        roomPlayerService.transferPlayerChips(toPlayerId, fromPlayerId, amount);

        RoomPlayer fromPlayer = roomPlayerService.findByUserID(fromPlayerId);
        RoomPlayer toPlayer = roomPlayerService.findByUserID(toPlayerId);

        ChipTransaction transaction = new ChipTransaction(fromPlayer, toPlayer, fromPlayer.getRoom(), amount);

        ChipTransferEvent event = new ChipTransferEvent(
                fromPlayer.getUser().getUsername(),
                toPlayer.getUser().getUsername(),
                amount,
                transaction.getDescription(), // Uses your enum's describe() method!
                fromPlayer.getChipBalance(),
                toPlayer.getChipBalance()
        );

        simpMessagingTemplate.convertAndSend(
                "/topic/room/" + fromPlayer.getRoom().getCode() + "/transfers",
                event
        );
    }

    public void performCall(UUID fromPlayerId, UUID toPlayerId, int amount) {
        transferChips(fromPlayerId, toPlayerId, amount, ChipTransaction.transactionType.CALL);
    }

    public void performRaise(UUID fromPlayerId, UUID toPlayerId, int amount) {
        transferChips(fromPlayerId, toPlayerId, amount, ChipTransaction.transactionType.RAISE);
    }

    public void performBuyin(UUID playerId, int amount) {
        RoomPlayer player = roomPlayerService.findByUserID(playerId);

        player.setChipBalance(player.getChipBalance() + amount);
        roomPlayerService.updatePlayerChips(playerId, player.getChipBalance());

        ChipTransaction transaction = recordBuyin(player, amount);

        BuyInEvent event = new BuyInEvent(
                player.getUser().getUsername(),
                amount,
                transaction.getDescription(),
                player.getChipBalance()
        );

        simpMessagingTemplate.convertAndSend(
                "/topic/room/" + player.getRoom().getCode() + "/buyin",
                event
        );
    }

    // ==== TRANSACTION HISTORY QUERIES ====

    public List<ChipTransaction> getPlayerTransactions(UUID playerId) {
        return chipTransactionRepo.findTransactionsByPlayer(playerId);
    }

    public List<ChipTransaction> getRoomTransactions(Room room) {
        return chipTransactionRepo.findByRoomOrderByCreatedAtDesc(room);
    }

    public List<ChipTransaction> getTransactionByType(ChipTransaction.transactionType type, Room room){
        return chipTransactionRepo.findByTransactionTypeAndRoomOrderByCreatedAtDesc(type, room);
    }

    public List<ChipTransaction> getRecentTransactions (UUID roomId, long hours){
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return chipTransactionRepo.findRecentTransactions(roomId, since);
    }

    // ==== PAGINATED QUERIES ====

    public List<ChipTransaction> getRoomTransactionHistory(Room room) {
        return chipTransactionRepo.findByRoomOrderByCreatedAtDesc(room);
    }

    public Page<ChipTransaction> getRoomTransactionHistoryPaginated(Room room, Pageable pageable) {
        return chipTransactionRepo.findByRoomOrderByCreatedAtDesc(room, pageable);
    }

    public List<ChipTransaction> getLargestTransactions(UUID roomId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return chipTransactionRepo.getLargestTransactions(roomId, pageable);
    }

    // ==== TRANSACTION TYPE SPECIFIC QUERIES ====

    public List<ChipTransaction> getTransactionCalls(Room room) {
        return getTransactionByType(ChipTransaction.transactionType.CALL, room);
    }

    public List<ChipTransaction> getTransactionRaises(Room room) {
        return getTransactionByType(ChipTransaction.transactionType.RAISE, room);
    }

    public List<ChipTransaction> getTransactionBuysins(Room room) {
        return getTransactionByType(ChipTransaction.transactionType.BUYIN, room);
    }

    // ==== PLAYER STATISTICS ====

    public PlayerChipStatistics getPlayerStatistics(UUID playerId) {
        Long totalSent = chipTransactionRepo.getTotalChipsTransferredByPlayer(playerId);
        Long totalReceived = chipTransactionRepo.getTotalChipsReceivedByPlayer(playerId);

        List<ChipTransaction> transactions = getPlayerTransactions(playerId);

        return new PlayerChipStatistics(
                totalSent != null ? totalSent : 0L,
                totalReceived != null ? totalReceived : 0L,
                (totalReceived != null ? totalReceived : 0L) - (totalSent != null ? totalSent : 0L),
                transactions.size()
        );
    }

    // ==== ROOM ANALYTICS ====

    public List<HourlyTransactionActivity> getRoomTransactionActivity(UUID roomId){
        List<Object[]> results = chipTransactionRepo.getTransactionActivityByHour(roomId);

        return results.stream()
                .map(result -> new HourlyTransactionActivity(
                        ((Number) result[1]).intValue(),
                        ((Number) result[0]).longValue()
                ))
                .collect(Collectors.toList());
    }

    public Integer getTotalBuyinsForRoom(Room room){
        List<ChipTransaction> buyIns = getTransactionBuysins(room);
        return buyIns.stream()
                .mapToInt(ChipTransaction::getChipsAmount)
                .sum();
    }

    public List<PlayerTransactionSummary> getMostActivePlayers(Room room, int limit) {
        List<ChipTransaction> transactions = getRoomTransactionHistory(room);

        // Group by player and count
        return transactions.stream()
                .flatMap(t -> {
                    List<RoomPlayer> players = new ArrayList<>();
                    if (t.getFromPlayer() != null) players.add(t.getFromPlayer());
                    if (t.getToPlayer() != null) players.add(t.getToPlayer());
                    return players.stream();
                })
                .collect(Collectors.groupingBy(
                        RoomPlayer::getId,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(limit)
                .map(entry -> {
                    RoomPlayer player = roomPlayerService.findByUserID(entry.getKey());
                    return new PlayerTransactionSummary(
                            player.getUser().getUsername(),
                            entry.getValue()
                    );
                })
                .collect(Collectors.toList());
    }

    // ==== BULK OPERATIONS ====

    public List<ChipTransaction> recordBulkTransfer(UUID fromPlayerId,
                                                    List<ChipDistribution> distributions,
                                                    ChipTransaction.transactionType type) {
        RoomPlayer fromPlayer = roomPlayerService.findByUserID(fromPlayerId);

        // Calculate total
        int totalAmount = distributions.stream()
                .mapToInt(ChipDistribution::getAmount)
                .sum();

        if (fromPlayer.getChipBalance() < totalAmount) {
            throw new RuntimeException("Not enough chips for bulk transfer");
        }

        List<ChipTransaction> transactions = new ArrayList<>();

        for (ChipDistribution dist : distributions) {
            roomPlayerService.transferPlayerChips(fromPlayerId, dist.getToPlayerID(), dist.getAmount());

            RoomPlayer toPlayer = roomPlayerService.findByUserID(dist.getToPlayerID());
            ChipTransaction transaction = recordTransfer(fromPlayer, toPlayer, dist.getAmount(), type);
            transactions.add(transaction);
        }

        return transactions;
    }
}

// ========================================
// SUPPORTING CLASSES FOR STATISTICS
// ========================================

class TransactionTypeStatistics {
    private long callCount;
    private long raiseCount;
    private long buyInCount;
    private int totalCallAmount;
    private int totalRaiseAmount;
    private int totalBuyInAmount;

    public TransactionTypeStatistics(long callCount, long raiseCount, long buyInCount,
                                     int totalCallAmount, int totalRaiseAmount, int totalBuyInAmount) {
        this.callCount = callCount;
        this.raiseCount = raiseCount;
        this.buyInCount = buyInCount;
        this.totalCallAmount = totalCallAmount;
        this.totalRaiseAmount = totalRaiseAmount;
        this.totalBuyInAmount = totalBuyInAmount;
    }

    // Getters
    public long getCallCount() { return callCount; }
    public long getRaiseCount() { return raiseCount; }
    public long getBuyInCount() { return buyInCount; }
    public int getTotalCallAmount() { return totalCallAmount; }
    public int getTotalRaiseAmount() { return totalRaiseAmount; }
    public int getTotalBuyInAmount() { return totalBuyInAmount; }
}

class PlayerTransactionSummary {
    private String username;
    private long transactionCount;

    public PlayerTransactionSummary(String username, long transactionCount) {
        this.username = username;
        this.transactionCount = transactionCount;
    }

    // Getters
    public String getUsername() { return username; }
    public long getTransactionCount() { return transactionCount; }
}

class BuyInEvent {
    private String playerUsername;
    private Integer amount;
    private String description;
    private Integer newBalance;
    private LocalDateTime timestamp;

    public BuyInEvent(String playerUsername, Integer amount, String description, Integer newBalance) {
        this.playerUsername = playerUsername;
        this.amount = amount;
        this.description = description;
        this.newBalance = newBalance;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getPlayerUsername() { return playerUsername; }
    public Integer getAmount() { return amount; }
    public String getDescription() { return description; }
    public Integer getNewBalance() { return newBalance; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

class ChipTransferEvent {
    private String fromPlayer;
    private String toPlayer;
    private Integer amount;
    private String description;
    private Integer fromPlayerBalance;
    private Integer toPlayerBalance;
    private LocalDateTime timestamp;

    public ChipTransferEvent(String fromPlayer, String toPlayer, Integer amount,
                             String description, Integer fromPlayerBalance, Integer toPlayerBalance) {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.amount = amount;
        this.description = description;
        this.fromPlayerBalance = fromPlayerBalance;
        this.toPlayerBalance = toPlayerBalance;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getFromPlayer() { return fromPlayer; }
    public String getToPlayer() { return toPlayer; }
    public Integer getAmount() { return amount; }
    public String getDescription() { return description; }
    public Integer getFromPlayerBalance() { return fromPlayerBalance; }
    public Integer getToPlayerBalance() { return toPlayerBalance; }
    public LocalDateTime getTimestamp() { return timestamp; }


}
