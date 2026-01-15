package com.joelcode.pokerchipsapplication.controller;

import com.joelcode.pokerchipsapplication.dto.request.CreateRoomRequest;
import com.joelcode.pokerchipsapplication.dto.response.GameStateResponse;
import com.joelcode.pokerchipsapplication.dto.response.RoomResponse;
import com.joelcode.pokerchipsapplication.entities.Room;
import com.joelcode.pokerchipsapplication.security.UserPrincipal;
import com.joelcode.pokerchipsapplication.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")


public class RoomController {
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // Create a new room - POST /api/rooms
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Room room = roomService.createNewRoom(request.getName(), request.getMaxPlayers(), request.getStartingChips(), principal.getUser());
        return ResponseEntity.ok(new RoomResponse(room));
    }

    // Get room by code = GET /api/rooms/ABC123
    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> getRoomByCode(@PathVariable String roomCode) {
        Room room = roomService.findByCode(roomCode);
        return ResponseEntity.ok(new RoomResponse(room));
    }

    // Get room game state - GET /api/rooms/ABC123/state
    @GetMapping("/{roomCode}/state")
    public ResponseEntity<GameStateResponse> getRoomState(@PathVariable String roomCode) {
        GameStateResponse state = roomService.getRoomState(roomCode);
        return ResponseEntity.ok(state);
    }

    // Get available rooms - GET /api/rooms/available
    @GetMapping("/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() {
        List<Room> rooms = roomService.getActiveRoomsWithMinPlayers(2);
        List<RoomResponse> response = rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get waiting rooms with pagination - GET /api/rooms/waiting?page=0&size=10
    @GetMapping("/waiting")
    public ResponseEntity<Page<RoomResponse>> getWaitingRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Room> rooms = roomService.getWaitingRooms(pageable);
        Page<RoomResponse> response = rooms.map(RoomResponse::new);
        return ResponseEntity.ok(response);
    }

    // Get active rooms with minimum players - GET /api/rooms/active?minPlayers=2
    @GetMapping("/active")
    public ResponseEntity<List<RoomResponse>> getActiveRooms(
            @RequestParam(defaultValue = "2") int minPlayers) {
        List<Room> rooms = roomService.getActiveRoomsWithMinPlayers(minPlayers);
        List<RoomResponse> response = rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Get user's rooms - GET /api/rooms/my-rooms

    @GetMapping("/my-rooms")
    public ResponseEntity<List<RoomResponse>> getMyRooms(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<Room> rooms = roomService.getUserRooms(principal.getUser());
        List<RoomResponse> response = rooms.stream()
                .map(RoomResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Start a room - POST /api/rooms/ABC123/start

    @PostMapping("/{roomCode}/start")
    public ResponseEntity<Void> startRoom(
            @PathVariable String roomCode,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        roomService.startRoom(roomCode, principal.getUser());
        return ResponseEntity.ok().build();
    }

    // End a room - POST /api/rooms/ABC123/end
    @PostMapping("/{roomCode}/end")
    public ResponseEntity<Void> endRoom(
            @PathVariable String roomCode,
            Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        roomService.endRoom(roomCode, principal.getUser());
        return ResponseEntity.ok().build();
    }

    // Get active room count - GET /api/rooms/stats/active-count

    @GetMapping("/stats/active-count")
    public ResponseEntity<Long> getActiveRoomCount() {
        Long count = roomService.getRoomCount();
        return ResponseEntity.ok(count);
    }



}
