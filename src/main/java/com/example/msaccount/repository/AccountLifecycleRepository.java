package com.example.msaccount.repository;

import com.example.msaccount.entity.AccountLifecycle;
import com.example.msaccount.entity.LifecycleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AccountLifecycleRepository extends JpaRepository<AccountLifecycle, String> {

    List<AccountLifecycle> findAllByStatusAndPurgeAtLessThanEqual(LifecycleStatus status, Instant cutoffTime);
}