package com.example.msaccount.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "account_lifecycle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountLifecycle {

    @Id
    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "purge_at", nullable = false)
    private Instant purgeAt;
}