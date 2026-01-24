package com.joelcode.pokerchipsapplication.repositories;


import com.joelcode.pokerchipsapplication.entities.Room;
import com.joelcode.pokerchipsapplication.entities.RoomPlayer;
import com.joelcode.pokerchipsapplication.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomPlayerRepo extends JpaRepository<RoomPlayer, UUID> {
    Optional<RoomPlayer> findByUserAndRoom(User user, Room room);

    Optional<RoomPlayer> findByUserIdAndRoomId(UUID id, UUID room);

    List<RoomPlayer> findAllByRoom(Room room);

    List<RoomPlayer> findAllByRoom_Id(UUID roomId);

    List<RoomPlayer> findByUser(User user);
    
    boolean existsByUserAndRoom(User user, Room room);


    // Get players by chip count (leaderboard)
    @Query("SELECT rp FROM RoomPlayer rp WHERE rp.room.code = :roomCode ORDER BY rp.chipBalance DESC")
    List<RoomPlayer> findPlayersInRoomOrderByChips(@Param("roomCode") String roomCode);


    // Find players with minimum chips (still in game)
    @Query("SELECT rp FROM RoomPlayer rp " +
            "WHERE rp.room.id = :roomId AND rp.chipBalance >= :minChips")
    List<RoomPlayer> findPlayersWithMinimumChips(@Param("roomId") UUID roomId,
                                                 @Param("minChips") Integer minChips);

    // Find players with zero chips (eliminated)
    @Query("SELECT rp FROM RoomPlayer rp " +
            "WHERE rp.room.id = :roomId AND rp.chipBalance = 0")
    List<RoomPlayer> findEliminatedPlayers(@Param("roomId") UUID roomId);

    // Get total chips in a room (for validation)
    @Query("SELECT COALESCE(SUM(rp.chipBalance), 0) FROM RoomPlayer rp " +
            "WHERE rp.room.id = :roomId")
    Long getTotalChipsInRoom(@Param("roomId") UUID roomId);

    // Find top N players by chips in a room
    @Query("SELECT rp FROM RoomPlayer rp WHERE rp.room.code = :roomCode ORDER BY rp.chipBalance DESC")
    List<RoomPlayer> findTopPlayersByChips(@Param("roomCode") String roomCode,
                                           org.springframework.data.domain.Pageable pageable);

    // ==== STATISTICS QUERIES ====

    // Average chips per player in room
    @Query("SELECT AVG(rp.chipBalance) FROM RoomPlayer rp WHERE rp.room.id = :roomId")
    Double getAverageChipsInRoom(@Param("roomId") UUID roomId);

    // Player count in room
    @Query("SELECT COUNT(rp) FROM RoomPlayer rp WHERE rp.room.id = :roomId")
    Long getPlayerCountInRoom(@Param("roomId") UUID roomId);

    // Find rooms where user has most chips
    @Query("SELECT rp FROM RoomPlayer rp " +
            "WHERE rp.user.id = :userId " +
            "ORDER BY rp.chipBalance DESC")
    List<RoomPlayer> findUsersBestRooms(@Param("userId") UUID userId);

    // ==== BULK OPERATIONS ====

    // Remove all players from a room (when the game ends)
    @Modifying
    @Transactional
    @Query("DELETE FROM RoomPlayer rp WHERE rp.room.id = :roomId")
    void removeAllPlayersFromRoom(@Param("roomId") UUID roomId);

    // Update all players' chips in a room (for special events)
    @Modifying
    @Transactional
    @Query("UPDATE RoomPlayer rp SET rp.chipBalance = rp.chipBalance + :bonusChips " +
            "WHERE rp.room.id = :roomId")
    int addBonusChipsToAllPlayers(@Param("roomId") UUID roomId, @Param("bonusChips") Integer bonusChips);

}

    



