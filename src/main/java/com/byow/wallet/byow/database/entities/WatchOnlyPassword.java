package com.byow.wallet.byow.database.entities;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Table(name = "watch_only_password")
public class WatchOnlyPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "password", length = 500, nullable = false)
    private String password = "";

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    public WatchOnlyPassword(String password, WalletEntity wallet) {
        this.password = password;
        this.wallet = wallet;
    }

    public WatchOnlyPassword() {
    }

    public String getPassword() {
        return password;
    }
}
