package com.joelcode.pokerchipsapplication.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chip_transaction")
public class ChipTransaction {

    public ChipTransaction(RoomPlayer fromPlayer, RoomPlayer toPlayer, Room room, int chipsAmount, transactionType transactionType, LocalDateTime createdAt) {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.room = room;
        this.chipsAmount = chipsAmount;
        this.transactionType = transactionType;
        this.createdAt = createdAt;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // retrieve fee from specific player (nullable for pot wins)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "from_player_id")
    private RoomPlayer fromPlayer;

    // transference of fees to new player (nullable for pot contributions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, name = "to_player_id")
    private RoomPlayer toPlayer;

    // link transaction to player in specific room
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "room")
    private Room room;

    // link transaction to specific game round
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private GameRound round;

    private int chipsAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private transactionType transactionType;

    private LocalDateTime createdAt;

    public ChipTransaction() {

    }

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

    public GameRound getRound() {
        return round;
    }

    public void setRound(GameRound round) {
        this.round = round;
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

    public transactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(transactionType type) {
        this.transactionType = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getHappenedAt() {
        return happenedAt;
    }

    public enum transactionType {
        CALL {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " called $" + amount : "Unknown player called $" + amount;
            }
        },
        RAISE {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " raised to $" + amount : "Unknown player raised to $" + amount;
            }
        },
        BUYIN {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return to != null ? to.getUser().getUsername() + " bought in for $" + amount : "Unknown player bought in for $" + amount;
            }
        },
        BET {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " bet $" + amount : "Unknown player bet $" + amount;
            }
        },
        CHECK {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " checked" : "Unknown player checked";
            }
        },
        FOLD {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " folded" : "Unknown player folded";
            }
        },
        ALL_IN {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " went all-in for $" + amount : "Unknown player went all-in for $" + amount;
            }
        },
        WIN {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return to != null ? to.getUser().getUsername() + " won $" + amount : "Unknown player won $" + amount;
            }
        },
        BLIND_SMALL {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " posted small blind $" + amount : "Unknown player posted small blind $" + amount;
            }
        },
        BLIND_BIG {
            @Override
            public String describe(RoomPlayer from, RoomPlayer to, int amount) {
                return from != null ? from.getUser().getUsername() + " posted big blind $" + amount : "Unknown player posted big blind $" + amount;
            }
        };

        public abstract String describe(RoomPlayer from, RoomPlayer to, int amount);
    }
}

