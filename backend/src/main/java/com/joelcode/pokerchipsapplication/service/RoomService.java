package com.joelcode.pokerchipsapplication.service;

import com.joelcode.pokerchipsapplication.dto.response.GameStateResponse;
import com.joelcode.pokerchipsapplication.entities.Room;
import com.joelcode.pokerchipsapplication.entities.RoomPlayer;
import com.joelcode.pokerchipsapplication.entities.User;
import com.joelcode.pokerchipsapplication.repositories.RoomPlayerRepo;
import com.joelcode.pokerchipsapplication.repositories.RoomRepo;
import com.joelcode.pokerchipsapplication.service.events.GameEndedEvent;
import com.joelcode.pokerchipsapplication.service.events.GameEvent;
import com.joelcode.pokerchipsapplication.exceptions.custom.InsufficientPlayersException;
import com.joelcode.pokerchipsapplication.exceptions.custom.InvalidGameStateException;
import com.joelcode.pokerchipsapplication.exceptions.custom.UnauthorizedActionException;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepo roomRepo;

    @Autowired
    private RoomPlayerRepo roomPlayerRepo;

    @Autowired
    private RoomPlayerService roomPlayerService;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // ==== ROOM CREATION & MANAGEMENT ====
    public Room createNewRoom(String name, Integer maxPlayers, Integer startingChips, User host) {
        String roomCode = generateUniqueRoomCode();

        Room room = new Room();
        room.setRoomName(name);
        room.setMaxPlayers(maxPlayers);
        room.setStaringChips(startingChips);
        room.setHost(host);
        room.setStatus(Room.roomStatus.WAITING);
        room.setCode(roomCode);

        Room savedRoom = roomRepo.save(room);

        roomPlayerService.addRoomPlayer(savedRoom, host, 0);

        return savedRoom;

    }

    private String generatedUniqueRoomCode() {
        final char[] ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        final int LENGTH = 6;
        final int MAX_ATTEMPTS = 128;
        SecureRandom random = new SecureRandom();

        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            char[] buff = new char[LENGTH];
            for (int i = 0; i < LENGTH; i++) {
                buff[i] = ALPHANUM[random.nextInt(ALPHANUM.length)];
            }
            String code = new String(buff);
            if (roomRepo.findByCode(code).isEmpty()) {
                return code;
            }
        }
        throw new IllegalStateException("Unable to generate a unique room code after " + MAX_ATTEMPTS + " attempts");
    }

    public Room findByCode(String roomCode){
        return roomRepo.findByCode(roomCode)
                .orElseThrow(() -> new IllegalStateException("Unable to find room with code " + roomCode));
    }

    public Room findById(UUID roomId) {
        return roomRepo.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("Unable to find room by ID" + roomId));
    }


    // ==== ROOM STATUS & LIFECYCLE ====

    public void startRoom(String roomCode, User host) {
        Room room = findByCode(roomCode);

        if (!room.getHost().getId().equals(host.getId())) {
            throw new UnauthorizedActionException("Only host can start the room.");
        }

        if (!room.getStatus().equals(Room.roomStatus.WAITING)) {
            throw new InvalidGameStateException("Game has already started or ended.");
        }

        Long playerCount = roomPlayerRepo.getPlayerCountInRoom(room.getId());
        if (playerCount < 2){
            throw new InsufficientPlayersException("Game must have at least 2 players to start.");
        }

        roomRepo.updateRoomStatus(room.getId(), Room.roomStatus.ACTIVE);

        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/room/" + roomCode,
                    new GameEvent("GAME_STARTED", "Game has started!"));
        }
    }

    public void endRoom(String roomCode, User host) {
        Room room = findByCode(roomCode);

        if (!room.getHost().getId().equals(host.getId())) {
            throw new UnauthorizedActionException("Only host can end the room.");
        }

        roomRepo.updateRoomStatus(room.getId(), Room.roomStatus.END);

        // Get final standings
        List<RoomPlayer> finalStandings = roomPlayerService.getRoomLeaderboard(roomCode);

        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/room/" + roomCode,
                    new GameEndedEvent(roomCode, finalStandings));
        }
    }

    // ==== ROOM DISCOVERY ====

    List<Room> getAvailableRoms(User host) {
        return roomRepo.findAvailableRooms(Room.roomStatus.WAITING);
    }

    public Page<Room> getWaitingRooms(Pageable pageable) {
        return roomRepo.findByStatus(Room.roomStatus.WAITING, pageable);
    }

    public List<Room> getActiveRoomsWithMinPlayers(int minPlayers) {
        return roomRepo.findActiveRoomsWithMinPlayers(minPlayers);
    }

    public List<Room> getUserRooms(User host) {
        return roomRepo.findByHost(host);
    }

    public Page<Room> getUserRoomsPaginated(User host, Pageable pageable) {
        return roomRepo.findByHost(host, pageable);
    }

    public List<Room> getRoomsByStatus(Room.roomStatus status) {
        return roomRepo.findByStatus(status);
    }

    // ==== ROOM STATISTICS ====

    public Long getRoomCount(){
        return roomRepo.count();
    }

    public GameStateResponse getRoomState(String roomCode) {
        Room room = findByCode(roomCode);
        List<RoomPlayer> players = roomPlayerService.getPlayersInRoom(room);

        return new GameStateResponse();
    }

    // ==== UTILITY METHODS ====

    private String generateUniqueRoomCode() {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        } while (roomRepo.findByCode(code).isPresent());
        return code;
    }

    public void endAllRoomsWithStatus(Room.roomStatus status) {
        roomRepo.endAllRoomsWithStatus(status, LocalDateTime.now());
    }


}
