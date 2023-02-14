package com.byow.wallet.byow.database.entities;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import static javax.persistence.FetchType.EAGER;

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

    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "wallet_id")
    private List<ExtendedPubkeyEntity> extendedPubkeys;

    @OneToOne(fetch = EAGER, mappedBy = "wallet")
    @JoinColumn(name = "wallet_id")
    private WatchOnlyPassword watchOnlyPassword;

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

    public List<ExtendedPubkeyEntity> getExtendedPubkeys() {
        return extendedPubkeys;
    }

    public long getId() {
        return id;
    }

    public boolean isWatchOnly() {
        return mnemonicSeed.isBlank();
    }

    public WatchOnlyPassword getWatchOnlyPassword() {
        return watchOnlyPassword;
    }
}
