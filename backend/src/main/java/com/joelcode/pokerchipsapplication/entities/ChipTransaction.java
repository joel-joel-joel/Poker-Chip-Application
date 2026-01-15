package com.joelcode.pokerchipsapplication.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chip_transaction")
public class ChipTransaction {

    public ChipTransaction(RoomPlayer fromPlayer, RoomPlayer toPlayer, Room room, int chipsAmount) {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.room = room;
        this.chipsAmount = chipsAmount;
    }

    public ChipTransaction() {}

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // retrieve fee from specific player
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true, nullable = false, name = "from_player_id")
    private RoomPlayer fromPlayer;

    // transference of fees to new player
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "to_player_id")
    private RoomPlayer toPlayer;

    // link transaction to player in specific room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room")
    private Room room;

    private int chipsAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private transactionType transactionType;

    // Creates description of transaction type
    public String getDescription() {
        return transactionType.describe(fromPlayer, toPlayer, chipsAmount);
    }

    @CreationTimestamp
    private LocalDateTime happenedAt;
    
    public RoomPlayer getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(RoomPlayer fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public RoomPlayer getToPlayer() {
        return toPlayer;
    }

    public void setToPlayer(RoomPlayer toPlayer) {
        this.toPlayer = toPlayer;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getChipsAmount() {
        return chipsAmount;
    }

    public void setChipsAmount(int chipsAmount) {
        this.chipsAmount = chipsAmount;
    }

    public void setDescription(String description) {
        getDescription();
    }

    public void setTransactionType(transactionType type) {
    }

    public enum transactionType {
        CALL {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from.getUser().getUsername() + " called with $" + amount;
            }
        },
        RAISE {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from.getUser().getUsername() + " raised with $" + amount;
            }
        },
        BUYIN {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return to.getUser().getUsername() + " bought in with $" + amount;
            }
        };

        public abstract String describe(RoomPlayer from, RoomPlayer to, int amount);
    }
}

