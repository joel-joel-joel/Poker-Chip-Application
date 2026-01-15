package com.joelcode.pokerchipsapplication.repositories;

import com.joelcode.pokerchipsapplication.entities.ChipTransaction;
import com.joelcode.pokerchipsapplication.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChipTransactionRepo extends JpaRepository<ChipTransaction, UUID> {
    // ==== TRANSACTION HISTORY ====

    // Get all transactions in a room (chronological order)
    List<ChipTransaction> findByRoomOrderByCreatedAtDesc(Room room);

    // Get all transactions involving a specific player
    @Query("SELECT ct FROM ChipTransaction ct " +
            "WHERE ct.fromPlayer.id = :playerId OR ct.toPlayer.id = :playerId " +
            "ORDER BY ct.happenedAt DESC")
    List<ChipTransaction> findTransactionsByPlayer(@Param("playerId") UUID playerId);

    // Get transactions by type (transfer, ante, blinds, etc.)
    List<ChipTransaction> findByTransactionTypeAndRoomOrderByCreatedAtDesc(
            ChipTransaction.transactionType type, Room room);

    // Get recent transactions (last N hours)
    @Query("SELECT ct FROM ChipTransaction ct " +
            "WHERE ct.room.id = :roomId AND ct.happenedAt >= :since " +
            "ORDER BY ct.happenedAt DESC")
    List<ChipTransaction> findRecentTransactions(@Param("roomId") UUID roomId,
                                                 @Param("since") LocalDateTime since);

    // ==== PAGINATION FOR LARGE TRANSACTION LISTS ====

    // Paginated room transaction history
    Page<ChipTransaction> findByRoomOrderByCreatedAtDesc(Room room, Pageable pageable);

    // ==== STATISTICS AND REPORTING ====

    // Total chips transferred by a player
    @Query("SELECT COALESCE(SUM(ct.chipsAmount), 0) FROM ChipTransaction ct " +
            "WHERE ct.fromPlayer.id = :playerId AND ct.transactionType = 'TRANSFER'")
    Long getTotalChipsTransferredByPlayer(@Param("playerId") UUID playerId);

    // Total chips received by a player
    @Query("SELECT COALESCE(SUM(ct.chipsAmount), 0) FROM ChipTransaction ct " +
            "WHERE ct.toPlayer.id = :playerId AND ct.transactionType = 'TRANSFER'")
    Long getTotalChipsReceivedByPlayer(@Param("playerId") UUID playerId);

    // Most active transaction period (for analytics)
    @Query("SELECT COUNT(ct), HOUR(ct.happenedAt) as hour FROM ChipTransaction ct " +
            "WHERE ct.room.id = :roomId " +
            "GROUP BY HOUR(ct.happenedAt) " +
            "ORDER BY COUNT(ct) DESC")
    List<Object[]> getTransactionActivityByHour(@Param("roomId") UUID roomId);

    // Largest transactions in a room
    @Query("SELECT ct FROM ChipTransaction ct " +
            "WHERE ct.room.id = :roomId " +
            "ORDER BY ct.chipsAmount DESC")
    List<ChipTransaction> getLargestTransactions(@Param("roomId") UUID roomId, Pageable pageable);
}
