package com.example.msaccount.service;

import com.example.msaccount.dto.UpdateUserRequest;
import com.example.msaccount.dto.UserResponse;
import com.example.msaccount.entity.AccountLifecycle;
import com.example.msaccount.entity.LifecycleStatus;
import com.example.msaccount.repository.AccountLifecycleRepository;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class UserService {

    private final Keycloak keycloak;
    private final String realm;
    private final AccountLifecycleRepository accountLifecycleRepository;

    public UserService(Keycloak keycloak, @Value("${lib-auth-kyc.keycloak.realm}") String realm, AccountLifecycleRepository accountLifecycleRepository) {
        this.keycloak = keycloak;
        this.realm = realm;
        this.accountLifecycleRepository = accountLifecycleRepository;
    }

    public UserResponse getUserById(String userId) {
        UserRepresentation user = getUserResource(userId).toRepresentation();

        String latestPictureUrl = user.firstAttribute("picture");

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .pictureUrl(latestPictureUrl)
                .enabled(Boolean.TRUE.equals(user.isEnabled()))
                .emailVerified(Boolean.TRUE.equals(user.isEmailVerified()))
                .attributes(user.getAttributes())
                .build();
    }

    public void updateUser(String userId, UpdateUserRequest updateUserRequest) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();

        if (updateUserRequest.getFirstName() != null) user.setFirstName(updateUserRequest.getFirstName());
        if (updateUserRequest.getLastName() != null) user.setLastName(updateUserRequest.getLastName());

        userResource.update(user);
    }

    public void revokeAllSessions(String userId) {
        getUserResource(userId).logout();
    }

    public void deactivateUser(String userId) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(false);
        userResource.update(user);

        revokeAllSessions(userId);
    }

    public void deleteUser(String userId) {
        deactivateUser(userId);

        AccountLifecycle lifecycle = AccountLifecycle.builder()
                .userId(userId)
                .status(LifecycleStatus.PENDING_DELETION)
                .requestedAt(Instant.now())
                .purgeAt(Instant.now().plus(30, ChronoUnit.DAYS))
                .build();

        accountLifecycleRepository.save(lifecycle);

        // todo: WIP
    }

    public void cancelUserDeletion(String userId) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(true);
        userResource.update(user);

        accountLifecycleRepository.findById(userId).ifPresent(record -> {
            record.setStatus(LifecycleStatus.CANCELLED);
            accountLifecycleRepository.save(record);
        });
    }

    public void purgeExpiredAccounts() {
        List<AccountLifecycle> expired = accountLifecycleRepository
                .findAllByStatusAndPurgeAtLessThanEqual(LifecycleStatus.PENDING_DELETION, Instant.now());

        if (expired.isEmpty()) {
            return;
        }

        for (AccountLifecycle record : expired) {
            String userId = record.getUserId();
            try {
                getUserResource(userId).remove();

                record.setStatus(LifecycleStatus.PURGED);
                accountLifecycleRepository.save(record);
            } catch (Exception ex) {
                record.setStatus(LifecycleStatus.FAILED);
                accountLifecycleRepository.save(record);
                // todo: improve error handling
            }
        }
    }

    private UserResource getUserResource(String userId) {
        return keycloak.realm(realm).users().get(userId);
    }
}