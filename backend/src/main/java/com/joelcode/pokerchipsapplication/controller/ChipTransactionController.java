package com.joelcode.pokerchipsapplication.controller;

import com.joelcode.pokerchipsapplication.dto.other.ChipDistribution;
import com.joelcode.pokerchipsapplication.dto.other.HourlyTransactionActivity;
import com.joelcode.pokerchipsapplication.dto.other.PlayerChipStatistics;
import com.joelcode.pokerchipsapplication.dto.request.TransferChipsRequest;
import com.joelcode.pokerchipsapplication.entities.ChipTransaction;
import com.joelcode.pokerchipsapplication.entities.Room;
import com.joelcode.pokerchipsapplication.entities.ChipTransaction.transactionType;
import com.joelcode.pokerchipsapplication.service.ChipTransactionService;
import com.joelcode.pokerchipsapplication.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class ChipTransactionController {

    @Autowired
    private ChipTransactionService chipTransactionService;

    @Autowired
    private RoomService roomService;

    /**
     * Transfer chips between players
     * POST /api/transactions/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferChips(@Valid @RequestBody TransferChipsRequest request) {
        chipTransactionService.transferChips(
                request.getFromPlayerId(),
                request.getToPlayerId(),
                request.getAmount(),                    // FIX: Added missing amount
                request.getType()                       // FIX: Added missing type
        );
        return ResponseEntity.ok().build();
    }

    /**
     * Bulk transfer chips to multiple players
     * POST /api/transactions/bulk-transfer
     */
    @PostMapping("/bulk-transfer")
    public ResponseEntity<List<ChipTransaction>> bulkTransfer(
            @RequestParam UUID fromPlayerId,
            @RequestParam transactionType type,        // FIX: Added type parameter
            @RequestBody List<ChipDistribution> distributions) {
        List<ChipTransaction> transactions = chipTransactionService.recordBulkTransfer(
                fromPlayerId, distributions, type
        );
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get room transaction history
     * GET /api/transactions/room/ABC123
     */
    @GetMapping("/room/{roomCode}")
    public ResponseEntity<List<ChipTransaction>> getRoomTransactions(@PathVariable String roomCode) {
        Room room = roomService.findByCode(roomCode);
        List<ChipTransaction> transactions = chipTransactionService.getRoomTransactionHistory(room);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get room transaction history (paginated)
     * GET /api/transactions/room/ABC123/paginated?page=0&size=20
     */
    @GetMapping("/room/{roomCode}/paginated")
    public ResponseEntity<Page<ChipTransaction>> getRoomTransactionsPaginated(
            @PathVariable String roomCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Room room = roomService.findByCode(roomCode);
        Pageable pageable = PageRequest.of(page, size);
        Page<ChipTransaction> transactions = chipTransactionService.getRoomTransactionHistoryPaginated(
                room, pageable
        );
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get player transaction history
     * GET /api/transactions/player/{playerId}
     */
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<ChipTransaction>> getPlayerTransactions(@PathVariable UUID playerId) {
        List<ChipTransaction> transactions = chipTransactionService.getPlayerTransactions(playerId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transactions by type
     * GET /api/transactions/room/ABC123/type/TRANSFER
     */
    @GetMapping("/room/{roomCode}/type/{type}")
    public ResponseEntity<List<ChipTransaction>> getTransactionsByType(
            @PathVariable String roomCode,
            @PathVariable ChipTransaction.transactionType type) {
        Room room = roomService.findByCode(roomCode);
        List<ChipTransaction> transactions = chipTransactionService.getTransactionByType(type, room);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get recent transactions
     * GET /api/transactions/room/{roomId}/recent?hours=24
     */
    @GetMapping("/room/{roomId}/recent")
    public ResponseEntity<List<ChipTransaction>> getRecentTransactions(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "24") int hours) {
        List<ChipTransaction> transactions = chipTransactionService.getRecentTransactions(roomId, hours);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get largest transactions
     * GET /api/transactions/room/{roomId}/largest?limit=10
     */
    @GetMapping("/room/{roomId}/largest")
    public ResponseEntity<List<ChipTransaction>> getLargestTransactions(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ChipTransaction> transactions = chipTransactionService.getLargestTransactions(roomId, limit);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get player statistics
     * GET /api/transactions/player/{playerId}/stats
     */
    @GetMapping("/player/{playerId}/stats")
    public ResponseEntity<PlayerChipStatistics> getPlayerStatistics(@PathVariable UUID playerId) {
        PlayerChipStatistics stats = chipTransactionService.getPlayerStatistics(playerId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get room transaction activity by hour
     * GET /api/transactions/room/{roomId}/activity
     */
    @GetMapping("/room/{roomId}/activity")
    public ResponseEntity<List<HourlyTransactionActivity>> getTransactionActivity(
            @PathVariable UUID roomId) {
        List<HourlyTransactionActivity> activity = chipTransactionService.getRoomTransactionActivity(roomId);
        return ResponseEntity.ok(activity);
    }
}
