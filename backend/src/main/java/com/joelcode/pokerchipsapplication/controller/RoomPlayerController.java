package com.joelcode.pokerchipsapplication.controller;
import com.joelcode.pokerchipsapplication.dto.response.PlayerResponse;
import com.joelcode.pokerchipsapplication.entities.RoomPlayer;
import com.joelcode.pokerchipsapplication.security.UserPrincipal;
import com.joelcode.pokerchipsapplication.service.RoomPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/room-players")
@CrossOrigin(origins = "*")
public class RoomPlayerController {

    @Autowired
    private RoomPlayerService roomPlayerService;

    // Join a room - POST /api/room-players/join/ABC123
    @PostMapping("/join/{roomCode}")
    public ResponseEntity<PlayerResponse> joinRoom(
            @PathVariable String roomCode,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        RoomPlayer player = roomPlayerService.joinRoom(roomCode, principal.getUser());
        return ResponseEntity.ok(new PlayerResponse(player));
    }

    // Leave a room - DELETE /api/room-players/room/{roomId}
    @DeleteMapping("/room/{roomId}")
    public ResponseEntity<Void> leaveRoom(
            @PathVariable UUID roomId,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        roomPlayerService.removePlayerFromRoom(roomId, principal.getUser().getId());
        return ResponseEntity.ok().build();
    }

    // Get players in a room by code - GET /api/room-players/room/ABC123
    @GetMapping("/room/{roomCode}")
    public ResponseEntity<List<PlayerResponse>> getPlayersInRoom(@PathVariable String roomCode) {
        List<RoomPlayer> players = roomPlayerService.getPlayersInRoom(roomCode);
        List<PlayerResponse> response = players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get room leaderboard - GET /api/room-players/room/ABC123/leaderboard
    @GetMapping("/room/{roomCode}/leaderboard")
    public ResponseEntity<List<PlayerResponse>> getLeaderboard(@PathVariable String roomCode) {
        List<RoomPlayer> players = roomPlayerService.getRoomLeaderboard(roomCode);
        List<PlayerResponse> response = players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get top players by chips - GET /api/room-players/room/ABC123/top?limit=5
    @GetMapping("/room/{roomCode}/top")
    public ResponseEntity<List<PlayerResponse>> getTopPlayers(
            @PathVariable String roomCode,
            @RequestParam(defaultValue = "5") int limit) {
        List<RoomPlayer> players = roomPlayerService.getTopPlayersByChips(roomCode, limit);
        List<PlayerResponse> response = players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get eliminated players - GET /api/room-players/room/{roomId}/eliminated
    @GetMapping("/room/{roomId}/eliminated")
    public ResponseEntity<List<PlayerResponse>> getEliminatedPlayers(@PathVariable UUID roomId) {
        List<RoomPlayer> players = roomPlayerService.getEliminatedPlayers(roomId);
        List<PlayerResponse> response = players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get user's rooms - GET /api/room-players/my-rooms
    @GetMapping("/my-rooms")
    public ResponseEntity<List<PlayerResponse>> getMyRooms(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<RoomPlayer> players = roomPlayerService.getUserRooms(principal.getUser());
        List<PlayerResponse> response = players.stream()
                .map(PlayerResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get room statistics - GET /api/room-players/room/{roomId}/stats
    @GetMapping("/room/{roomId}/stats")
    public ResponseEntity<RoomStats> getRoomStats(@PathVariable UUID roomId) {
        Long totalChips = roomPlayerService.getTotalChipsInRoom(roomId);
        Double averageChips = roomPlayerService.getAverageChipsInRoom(roomId);
        Long playerCount = roomPlayerService.getPlayerCountInRoom(roomId);

        RoomStats stats = new RoomStats(totalChips, averageChips, playerCount);
        return ResponseEntity.ok(stats);
    }


    // Helper class for room statistics
    static class RoomStats {
        private Long totalChips;
        private Double averageChips;
        private Long playerCount;

        public RoomStats(Long totalChips, Double averageChips, Long playerCount) {
            this.totalChips = totalChips;
            this.averageChips = averageChips;
            this.playerCount = playerCount;
        }

        public Long getTotalChips() { return totalChips; }
        public Double getAverageChips() { return averageChips; }
        public Long getPlayerCount() { return playerCount; }
    }
}
