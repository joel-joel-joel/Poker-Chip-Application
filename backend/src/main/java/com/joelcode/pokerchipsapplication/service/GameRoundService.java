package com.joelcode.pokerchipsapplication.service;

import com.joelcode.pokerchipsapplication.entities.*;
import com.joelcode.pokerchipsapplication.repositories.*;
import com.joelcode.pokerchipsapplication.exceptions.custom.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class GameRoundService {

    private final GameRoundRepo gameRoundRepo;
    private final RoomRepo roomRepo;
    private final RoomPlayerRepo roomPlayerRepo;
    private final ChipTransactionService chipTransactionService;

    public GameRoundService(GameRoundRepo gameRoundRepo, RoomRepo roomRepo,
                           RoomPlayerRepo roomPlayerRepo, ChipTransactionService chipTransactionService) {
        this.gameRoundRepo = gameRoundRepo;
        this.roomRepo = roomRepo;
        this.roomPlayerRepo = roomPlayerRepo;
        this.chipTransactionService = chipTransactionService;
    }

    public GameRound startNewRound(String roomCode) {
        Room room = roomRepo.findByCode(roomCode)
            .orElseThrow(() -> new RoomNotFoundException("Room not found: " + roomCode));

        // End any active round first
        gameRoundRepo.findByRoomIdAndStatus(room.getId(), GameRound.RoundStatus.ACTIVE)
            .ifPresent(r -> {
                r.setStatus(GameRound.RoundStatus.COMPLETE);
                r.setEndedAt(LocalDateTime.now());
                gameRoundRepo.save(r);
            });

        // Reset all players for new round
        resetPlayersForNewRound(room.getId());

        // Create new round
        Integer maxRoundNum = gameRoundRepo.getMaxRoundNumber(room.getId());
        int nextRoundNum = (maxRoundNum == null) ? 1 : maxRoundNum + 1;

        GameRound round = new GameRound();
        round.setRoom(room);
        round.setRoundNumber(nextRoundNum);
        round.setPotAmount(0);
        round.setCurrentBet(0);
        round.setStatus(GameRound.RoundStatus.ACTIVE);

        return gameRoundRepo.save(round);
    }

    public GameRound getCurrentRound(UUID roomId) {
        return gameRoundRepo.findByRoomIdAndStatus(roomId, GameRound.RoundStatus.ACTIVE)
            .orElse(null);
    }

    public void resetPlayersForNewRound(UUID roomId) {
        List<RoomPlayer> players = roomPlayerRepo.findAllByRoom_Id(roomId);
        for (RoomPlayer player : players) {
            player.setCurrentHandBet(0);
            player.setAllIn(false);
            if (player.getStatus() == RoomPlayer.PlayerStatus.FOLDED ||
                player.getStatus() == RoomPlayer.PlayerStatus.ALL_IN) {
                if (player.getChipBalance() > 0) {
                    player.setStatus(RoomPlayer.PlayerStatus.ACTIVE);
                } else {
                    player.setStatus(RoomPlayer.PlayerStatus.INACTIVE);
                }
            }
            roomPlayerRepo.save(player);
        }
    }

    public GameRound getRoundById(UUID roundId) {
        return gameRoundRepo.findById(roundId)
            .orElseThrow(() -> new InvalidPokerActionException("Round not found: " + roundId));
    }

    // Helper methods for poker actions
    private GameRound getActiveRound(UUID roundId) {
        GameRound round = gameRoundRepo.findById(roundId)
            .orElseThrow(() -> new InvalidPokerActionException("Round not found"));
        if (round.getStatus() != GameRound.RoundStatus.ACTIVE) {
            throw new InvalidPokerActionException("Round is not active");
        }
        return round;
    }

    private RoomPlayer getActivePlayer(UUID playerId) {
        RoomPlayer player = roomPlayerRepo.findById(playerId)
            .orElseThrow(() -> new PlayerNotInRoomException("Player not found"));
        if (player.getStatus() == RoomPlayer.PlayerStatus.FOLDED) {
            throw new InvalidPokerActionException("Player has folded");
        }
        if (player.getStatus() == RoomPlayer.PlayerStatus.INACTIVE) {
            throw new InvalidPokerActionException("Player is inactive");
        }
        return player;
    }

    // Poker action methods will be added next
    public void placeBet(UUID roundId, UUID playerId, int amount) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer player = getActivePlayer(playerId);

        if (round.getCurrentBet() > 0) {
            throw new InvalidPokerActionException("Cannot bet - there is already a bet. Use raise instead.");
        }
        if (amount <= 0) {
            throw new InvalidPokerActionException("Bet amount must be positive");
        }
        if (amount > player.getChipBalance()) {
            throw new InvalidPokerActionException("Insufficient chips for bet");
        }

        player.setChipBalance(player.getChipBalance() - amount);
        player.setCurrentHandBet(amount);
        round.setPotAmount(round.getPotAmount() + amount);
        round.setCurrentBet(amount);

        roomPlayerRepo.save(player);
        gameRoundRepo.save(round);
        chipTransactionService.recordBetToPot(round, player, amount, ChipTransaction.transactionType.BET);
    }

    public void call(UUID roundId, UUID playerId) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer player = getActivePlayer(playerId);

        int callAmount = round.getCurrentBet() - player.getCurrentHandBet();
        if (callAmount <= 0) {
            throw new InvalidPokerActionException("No bet to call");
        }
        if (callAmount > player.getChipBalance()) {
            throw new InvalidPokerActionException("Insufficient chips - use all-in instead");
        }

        player.setChipBalance(player.getChipBalance() - callAmount);
        player.setCurrentHandBet(round.getCurrentBet());
        round.setPotAmount(round.getPotAmount() + callAmount);

        roomPlayerRepo.save(player);
        gameRoundRepo.save(round);
        chipTransactionService.recordBetToPot(round, player, callAmount, ChipTransaction.transactionType.CALL);
    }

    public void raise(UUID roundId, UUID playerId, int raiseToAmount) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer player = getActivePlayer(playerId);

        if (raiseToAmount <= round.getCurrentBet()) {
            throw new InvalidPokerActionException("Raise must be higher than current bet of " + round.getCurrentBet());
        }

        int totalNeeded = raiseToAmount - player.getCurrentHandBet();
        if (totalNeeded > player.getChipBalance()) {
            throw new InvalidPokerActionException("Insufficient chips for raise");
        }

        player.setChipBalance(player.getChipBalance() - totalNeeded);
        player.setCurrentHandBet(raiseToAmount);
        round.setPotAmount(round.getPotAmount() + totalNeeded);
        round.setCurrentBet(raiseToAmount);

        roomPlayerRepo.save(player);
        gameRoundRepo.save(round);
        chipTransactionService.recordBetToPot(round, player, totalNeeded, ChipTransaction.transactionType.RAISE);
    }

    public void check(UUID roundId, UUID playerId) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer player = getActivePlayer(playerId);

        int callAmount = round.getCurrentBet() - player.getCurrentHandBet();
        if (callAmount > 0) {
            throw new InvalidPokerActionException("Cannot check - must call " + callAmount + " or fold");
        }

        chipTransactionService.recordCheck(round, player);
    }

    public void fold(UUID roundId, UUID playerId) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer player = getActivePlayer(playerId);

        player.setStatus(RoomPlayer.PlayerStatus.FOLDED);
        roomPlayerRepo.save(player);
        chipTransactionService.recordFold(round, player);
    }

    public void allIn(UUID roundId, UUID playerId) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer player = getActivePlayer(playerId);

        int allInAmount = player.getChipBalance();
        if (allInAmount <= 0) {
            throw new InvalidPokerActionException("No chips to go all-in with");
        }

        player.setCurrentHandBet(player.getCurrentHandBet() + allInAmount);
        round.setPotAmount(round.getPotAmount() + allInAmount);

        if (player.getCurrentHandBet() > round.getCurrentBet()) {
            round.setCurrentBet(player.getCurrentHandBet());
        }

        player.setChipBalance(0);
        player.setAllIn(true);
        player.setStatus(RoomPlayer.PlayerStatus.ALL_IN);

        roomPlayerRepo.save(player);
        gameRoundRepo.save(round);
        chipTransactionService.recordBetToPot(round, player, allInAmount, ChipTransaction.transactionType.ALL_IN);
    }

    public void endRound(UUID roundId, UUID winnerId) {
        GameRound round = getActiveRound(roundId);
        RoomPlayer winner = roomPlayerRepo.findById(winnerId)
            .orElseThrow(() -> new PlayerNotInRoomException("Winner not found"));

        int potAmount = round.getPotAmount();
        winner.setChipBalance(winner.getChipBalance() + potAmount);

        round.setWinner(winner);
        round.setStatus(GameRound.RoundStatus.COMPLETE);
        round.setEndedAt(LocalDateTime.now());
        round.setPotAmount(0);

        roomPlayerRepo.save(winner);
        gameRoundRepo.save(round);
        chipTransactionService.recordWin(round, winner, potAmount);
    }
}
