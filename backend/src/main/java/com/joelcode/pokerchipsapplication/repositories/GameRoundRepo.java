package com.joelcode.pokerchipsapplication.repositories;

import com.joelcode.pokerchipsapplication.entities.GameRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GameRoundRepo extends JpaRepository<GameRound, UUID> {

    Optional<GameRound> findByRoomIdAndStatus(UUID roomId, GameRound.RoundStatus status);

    List<GameRound> findByRoomIdOrderByRoundNumberDesc(UUID roomId);

    @Query("SELECT MAX(gr.roundNumber) FROM GameRound gr WHERE gr.room.id = :roomId")
    Integer getMaxRoundNumber(@Param("roomId") UUID roomId);
}
