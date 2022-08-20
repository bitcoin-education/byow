package com.byow.wallet.byow.database.entities;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "wallet")
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "mnemonic_seed", length = 500, nullable = false)
    private String mnemonicSeed;

    @Column(name = "number_of_generated_addresses")
    private Integer numberOfGeneratedAddresses;

    @Column(name = "created_at")
    @CreatedDate
    private Date createdAt;

    public WalletEntity() {
    }

    public WalletEntity(String name, String mnemonicSeed, Integer numberOfGeneratedAddresses, Date createdAt) {
        this.name = name;
        this.mnemonicSeed = mnemonicSeed;
        this.numberOfGeneratedAddresses = numberOfGeneratedAddresses;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public String getMnemonicSeed() {
        return mnemonicSeed;
    }

    public Integer getNumberOfGeneratedAddresses() {
        return numberOfGeneratedAddresses;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
