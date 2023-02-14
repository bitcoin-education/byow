package com.byow.wallet.byow.database.entities;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "extended_pubkey")
public class ExtendedPubkeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    public ExtendedPubkeyEntity(String key, String type, WalletEntity wallet) {
        this.key = key;
        this.type = type;
        this.wallet = wallet;
    }

    public ExtendedPubkeyEntity() {
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}
