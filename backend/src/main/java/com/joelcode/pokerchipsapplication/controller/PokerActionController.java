package com.joelcode.pokerchipsapplication.controller;

import com.joelcode.pokerchipsapplication.dto.request.PokerActionRequest;
import com.joelcode.pokerchipsapplication.dto.response.*;
import com.joelcode.pokerchipsapplication.entities.*;
import com.joelcode.pokerchipsapplication.service.*;
import com.joelcode.pokerchipsapplication.repositories.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/poker")
@CrossOrigin(origins = "*")
public class PokerActionController {

    private final GameRoundService gameRoundService;
    private final RoomRepo roomRepo;
    private final RoomPlayerRepo roomPlayerRepo;

    public PokerActionController(GameRoundService gameRoundService,
                                 RoomRepo roomRepo, RoomPlayerRepo roomPlayerRepo) {
        this.gameRoundService = gameRoundService;
        this.roomRepo = roomRepo;
        this.roomPlayerRepo = roomPlayerRepo;
    }

    @PostMapping("/round/start")
    public ResponseEntity<RoundStateResponse> startRound(@RequestParam String roomCode) {
        GameRound round = gameRoundService.startNewRound(roomCode);
        return ResponseEntity.ok(buildRoundStateResponse(round));
    }

    @PostMapping("/action")
    public ResponseEntity<RoundStateResponse> performAction(@RequestBody PokerActionRequest request) {
        switch (request.getAction()) {
            case BET -> gameRoundService.placeBet(request.getRoundId(), request.getPlayerId(), request.getAmount());
            case CALL -> gameRoundService.call(request.getRoundId(), request.getPlayerId());
            case RAISE -> gameRoundService.raise(request.getRoundId(), request.getPlayerId(), request.getAmount());
            case CHECK -> gameRoundService.check(request.getRoundId(), request.getPlayerId());
            case FOLD -> gameRoundService.fold(request.getRoundId(), request.getPlayerId());
            case ALL_IN -> gameRoundService.allIn(request.getRoundId(), request.getPlayerId());
        }

        GameRound round = gameRoundService.getRoundById(request.getRoundId());
        return ResponseEntity.ok(buildRoundStateResponse(round));
    }

    @PostMapping("/round/{roundId}/end")
    public ResponseEntity<RoundStateResponse> endRound(@PathVariable UUID roundId,
                                                       @RequestParam UUID winnerId) {
        gameRoundService.endRound(roundId, winnerId);
        GameRound round = gameRoundService.getRoundById(roundId);
        return ResponseEntity.ok(buildRoundStateResponse(round));
    }

    @GetMapping("/round/{roundId}/state")
    public ResponseEntity<RoundStateResponse> getRoundState(@PathVariable UUID roundId) {
        GameRound round = gameRoundService.getRoundById(roundId);
        return ResponseEntity.ok(buildRoundStateResponse(round));
    }

    @GetMapping("/room/{roomCode}/current-round")
    public ResponseEntity<RoundStateResponse> getCurrentRound(@PathVariable String roomCode) {
        Room room = roomRepo.findByCode(roomCode).orElseThrow();
        GameRound round = gameRoundService.getCurrentRound(room.getId());
        if (round == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(buildRoundStateResponse(round));
    }

    private RoundStateResponse buildRoundStateResponse(GameRound round) {
        List<RoomPlayer> players = roomPlayerRepo.findAllByRoom_Id(round.getRoom().getId());
        List<PlayerRoundState> playerStates = players.stream()
            .map(p -> new PlayerRoundState(
                p.getId(),
                p.getUser().getUsername(),
                p.getChipBalance(),
                p.getCurrentHandBet(),
                p.getStatus().name(),
                p.isAllIn()
            ))
            .collect(Collectors.toList());

        return new RoundStateResponse(
            round.getId(),
            round.getRoundNumber(),
            round.getPotAmount(),
            round.getCurrentBet(),
            playerStates,
            round.getStatus().name()
        );
    }
}
