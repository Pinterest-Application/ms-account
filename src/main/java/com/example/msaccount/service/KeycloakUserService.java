package com.example.msaccount.service;

import com.example.msaccount.dto.UpdateUserRequest;
import jakarta.ws.rs.RedirectionException;
import jakarta.ws.rs.WebApplicationException;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KeycloakUserService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakUserService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public void updateUser(String userId, UpdateUserRequest updateUserRequest) {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();

            if (updateUserRequest.getFirstName() != null) user.setFirstName(updateUserRequest.getFirstName());
            if (updateUserRequest.getLastName() != null) user.setLastName(updateUserRequest.getLastName());

            userResource.update(user);
    }

    public void deleteUser(String userId) {
        keycloak.realm(realm).users().get(userId).remove();
    }

    public void logoutUser(String userId) {
        keycloak.realm(realm).users().get(userId).logout();
    }

    public void toggleUserStatus(String userId, boolean enabled) {
        UserResource userResource = keycloak.realm(realm).users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(enabled);
        userResource.update(user);
    }
}