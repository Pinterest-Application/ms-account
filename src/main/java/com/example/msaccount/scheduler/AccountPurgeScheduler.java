package com.example.msaccount.scheduler;

import com.example.msaccount.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountPurgeScheduler {

    private final UserService userService;

    // Runs once every day at 02:00 AM UTC
    @Scheduled(cron = "0 0 2 * * *", zone = "UTC")
    @SchedulerLock(name = "purgeExpiredAccountsLock", lockAtLeastFor = "5m", lockAtMostFor = "15m")
    public void runAccountPurge() {
        userService.purgeExpiredAccounts();
    }
}