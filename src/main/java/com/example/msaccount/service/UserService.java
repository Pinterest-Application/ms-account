package com.example.msaccount.service;

import com.example.msaccount.dto.UpdateUserRequest;
import com.example.msaccount.dto.UserResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final Keycloak keycloak;
    private final String realm;

    public UserService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public void updateUser(String userId, UpdateUserRequest updateUserRequest) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();

        if (updateUserRequest.getFirstName() != null) user.setFirstName(updateUserRequest.getFirstName());
        if (updateUserRequest.getLastName() != null) user.setLastName(updateUserRequest.getLastName());

        userResource.update(user);
    }

    public void deleteUser(String userId) {
        getUserResource(userId).remove();
    }

    public void deactivateUser(String userId) {
        UserResource userResource = getUserResource(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(false);
        userResource.update(user);
    }

    public UserResponse getUserById(String userId) {
        UserRepresentation user = getUserResource(userId).toRepresentation();

        System.out.println("Gələn atributlar: " + user.getAttributes());
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

    private UserResource getUserResource(String userId) {
        return keycloak.realm(realm).users().get(userId);
    }

}