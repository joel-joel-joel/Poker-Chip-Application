package com.joelcode.pokerchipsapplication.service;

import com.joelcode.pokerchipsapplication.entities.Room;
import com.joelcode.pokerchipsapplication.entities.RoomPlayer;
import com.joelcode.pokerchipsapplication.entities.User;
import com.joelcode.pokerchipsapplication.repositories.RoomPlayerRepo;
import com.joelcode.pokerchipsapplication.repositories.RoomRepo;
import com.joelcode.pokerchipsapplication.service.events.PlayerEvent;
import com.joelcode.pokerchipsapplication.exceptions.custom.AlreadyInRoomException;
import com.joelcode.pokerchipsapplication.exceptions.custom.InsufficientChipsException;
import com.joelcode.pokerchipsapplication.exceptions.custom.InvalidTransferException;
import com.joelcode.pokerchipsapplication.exceptions.custom.PlayerNotInRoomException;
import com.joelcode.pokerchipsapplication.exceptions.custom.RoomFullException;
import com.joelcode.pokerchipsapplication.exceptions.custom.RoomNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class RoomPlayerService {

    @Autowired
    private RoomPlayerRepo roomPlayerRepo;

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ==== PLAYER ROOM MANAGEMENT ====
    public RoomPlayer addRoomPlayer(Room room, User user, int position) {
        Optional<RoomPlayer> existing = roomPlayerRepo.findByUserAndRoom(user, room);
        if (existing.isPresent()) {
            throw new AlreadyInRoomException("Room player already exists");
        }

        // Check room capacity
        Long currentPlayers = roomPlayerRepo.getPlayerCountInRoom(room.getId());
        if (currentPlayers >= room.getMaxPlayers()) {
            throw new RoomFullException("Room is full");
        }

        RoomPlayer roomPlayer = new RoomPlayer();
        roomPlayer.setRoom(room);
        roomPlayer.setUser(user);
        roomPlayer.setPosition(position);
        roomPlayer.setChipBalance(room.getStaringChips());
        roomPlayer.setStatus(RoomPlayer.PlayerStatus.ACTIVE);

        RoomPlayer savedPlayer = roomPlayerRepo.save(roomPlayer);

        // Notify other players
        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/room/" + room.getCode() + "/players",
                    new PlayerEvent("PLAYER_JOINED", user.getUsername(), position));
        }

        return savedPlayer;
    }

    public RoomPlayer joinRoom(String roomCode, User user) {
        Room room = roomRepo.findByCode(roomCode)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        Long currentPlayers = roomPlayerRepo.getPlayerCountInRoom(room.getId());
        return addRoomPlayer(room, user, currentPlayers.intValue());
    }

    public void removePlayerFromRoom(UUID roomId, UUID userId) {
        RoomPlayer roomPlayer = roomPlayerRepo.findByUserIDAndRoomId(userId, roomId)
                .orElseThrow(() -> new PlayerNotInRoomException("Player not in room"));

        roomPlayerRepo.delete(roomPlayer);

        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/room/" + roomPlayer.getRoom().getCode() + "/players",
                    new PlayerEvent("PLAYER_LEFT", roomPlayer.getUser().getUsername(), roomPlayer.getPosition()));
        }
    }

    // ==== PLAYER LOOKUP METHODS ====
    public RoomPlayer findByUserAndRoom(User user, Room room) {
        return roomPlayerRepo.findByUserAndRoom(user, room)
                .orElseThrow(() -> new PlayerNotInRoomException("Player not in room"));
    }

    public RoomPlayer findByUserIdAndRoomID(UUID userID, UUID roomID) {
        return roomPlayerRepo.findByUserIDAndRoomId(userID, roomID)
                .orElseThrow(() -> new PlayerNotInRoomException("Player not in room"));
    }

    public RoomPlayer findByUserID (UUID userID) {
        return roomPlayerRepo.findById(userID)
                .orElseThrow(() -> new PlayerNotInRoomException("Player not in room"));
    }

    public List<RoomPlayer> getUserRooms(User user) {
        return roomPlayerRepo.findByUser(user);
    }

    public boolean isUserInRoom(User user, Room room) {
        return roomPlayerRepo.existsByUserAndRoom(user, room);
    }

    // ==== POKER-SPECIFIC QUERIES ====
    public List<RoomPlayer> getRoomLeaderboard(String roomCode) {
        return roomPlayerRepo.findPlayersInRoomOrderByChips(roomCode);
    }

    public List<RoomPlayer> getPlayersWithMinimumChips(UUID roomId, Integer minChips) {
        return roomPlayerRepo.findPlayersWithMinimumChips(roomId, minChips);
    }

    public List<RoomPlayer> getEliminatedPlayers(UUID roomId) {
        return roomPlayerRepo.findEliminatedPlayers(roomId);
    }

    public List<RoomPlayer> getTopPlayersByChips(String roomCode, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return roomPlayerRepo.findTopPlayersByChips(roomCode, pageable);
    }

    public List<RoomPlayer> getUsersBestRooms(UUID userId) {
        return roomPlayerRepo.findUsersBestRooms(userId);
    }

    // ==== CHIP OPERATIONS ====
    public void updatePlayerChips(UUID playerId, Integer newBalance) {
        RoomPlayer player = roomPlayerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotInRoomException("Player not found"));
        player.setChipBalance(newBalance);
        roomPlayerRepo.save(player);
    }

    public void transferPlayerChips(UUID toPlayerId, UUID fromPlayerId, Integer amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidTransferException("Amount must be positive");
        }
        RoomPlayer toPlayer = roomPlayerRepo.findById(toPlayerId)
                .orElseThrow(() -> new PlayerNotInRoomException("Recipient player not found"));
        RoomPlayer fromPlayer = roomPlayerRepo.findById(fromPlayerId)
                .orElseThrow(() -> new PlayerNotInRoomException("Sender player not found"));

        if (!fromPlayer.getRoom().getId().equals(toPlayer.getRoom().getId())) {
            throw new InvalidTransferException("Players must be in same room");
        }

        if (fromPlayer.getChipBalance() < amount) {
            throw new InsufficientChipsException("Not enough chips");
        }

        fromPlayer.setChipBalance(fromPlayer.getChipBalance() - amount);
        toPlayer.setChipBalance(toPlayer.getChipBalance() + amount);
        roomPlayerRepo.save(fromPlayer);
        roomPlayerRepo.save(toPlayer);
    }

    // ==== ROOM STATISTICS ====
    public Long getTotalChipsInRoom(UUID roomId) {
        return roomPlayerRepo.getTotalChipsInRoom(roomId);
    }

    public Double getAverageChipsInRoom(UUID roomId) {
        return roomPlayerRepo.getAverageChipsInRoom(roomId);
    }

    public Long getPlayerCountInRoom(UUID roomId) {
        return roomPlayerRepo.getPlayerCountInRoom(roomId);
    }

    // ==== PLAYER LIST HELPERS ====
    public List<RoomPlayer> getPlayersInRoom(Room room) {
        return roomPlayerRepo.findAllByRoom(room);
    }

    public List<RoomPlayer> getPlayersInRoom(String roomCode) {
        Room room = roomRepo.findByCode(roomCode)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        return roomPlayerRepo.findAllByRoom(room);
    }

    // ==== BULK OPERATIONS ====

    public void clearRoom(UUID roomId) {
        roomPlayerRepo.removeAllPlayersFromRoom(roomId);
    }
}
