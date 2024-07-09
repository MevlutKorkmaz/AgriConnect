package com.gri.agriconnect.controller;

import com.gri.agriconnect.dto.UserDTO;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
@Tag(name = "User", description = "API for managing users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", description = "Adds a new user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Creating a new user");
        try {
            User createdUser = userService.createUser(user);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Sign in", description = "Signs in a user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User signed in",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Signing in user with email: {}", userDTO.getEmail());
        Optional<String> userId = userService.authenticateUser(userDTO.getEmail(), userDTO.getPassword());
        if (userId.isPresent()) {
            return ResponseEntity.ok(userId.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @Operation(summary = "Get user by ID", description = "Fetches a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User fetched",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        logger.info("Fetching user with ID: {}", userId);
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all users", description = "Fetches all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users fetched",
                    content = @Content(schema = @Schema(implementation = User.class)))
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Update user", description = "Updates a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @Valid @RequestBody User userDetails) {
        logger.info("Updating user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUser(userId, userDetails);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        logger.info("Deleting user with ID: {}", userId);
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Search users by name", description = "Searches for users by their first or last name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        logger.info("Searching users by name: {}", name);
        List<User> users = userService.searchUsersByName(name);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "Update user status", description = "Updates the status of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable String userId, @RequestParam boolean accountEnabled) {
        logger.info("Updating status of user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUserStatus(userId, accountEnabled);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user lock status", description = "Updates the lock status of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User lock status updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/lock")
    public ResponseEntity<User> updateUserLockStatus(@PathVariable String userId, @RequestParam boolean accountLocked) {
        logger.info("Updating lock status of user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUserLockStatus(userId, accountLocked);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update user password", description = "Updates the password of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User password updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/password")
    public ResponseEntity<User> updateUserPassword(@PathVariable String userId, @RequestParam String newPassword) {
        logger.info("Updating password of user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUserPassword(userId, newPassword);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update profile photo", description = "Updates the profile photo of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile photo updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/profile-photo")
    public ResponseEntity<Void> updateProfilePhoto(@PathVariable String userId, @RequestParam("file") MultipartFile profilePhoto) {
        logger.info("Updating profile photo of user with ID: {}", userId);
        try {
            userService.updateProfilePhoto(userId, profilePhoto);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload profile photo");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Update cover photo", description = "Updates the cover photo of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover photo updated",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/cover-photo")
    public ResponseEntity<Void> updateCoverPhoto(@PathVariable String userId, @RequestParam("file") MultipartFile coverPhoto) {
        logger.info("Updating cover photo of user with ID: {}", userId);
        try {
            userService.updateCoverPhoto(userId, coverPhoto);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload cover photo");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get profile photo", description = "Retrieves the profile photo of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile photo retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable String userId) {
        logger.info("Fetching profile photo of user with ID: {}", userId);
        try {
            byte[] imageData = userService.getProfilePhoto(userId);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageData);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve profile photo");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Get cover photo", description = "Retrieves the cover photo of a user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover photo retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/cover-photo")
    public ResponseEntity<byte[]> getCoverPhoto(@PathVariable String userId) {
        logger.info("Fetching cover photo of user with ID: {}", userId);
        try {
            byte[] imageData = userService.getCoverPhoto(userId);
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.IMAGE_JPEG).body(imageData);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve cover photo");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

//    // Email Verification
//    @Operation(summary = "Send verification email", description = "Sends a verification email to the user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Verification email sent"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @PostMapping("/{userId}/send-verification-email")
//    public ResponseEntity<Void> sendVerificationEmail(@PathVariable String userId) {
//        logger.info("Sending verification email to user with ID: {}", userId);
//        Optional<User> user = userService.getUserById(userId);
//        if (user.isPresent()) {
//            userService.sendVerificationEmail(user.get());
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    @Operation(summary = "Verify email token", description = "Verifies the email token of the user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Email verified"),
//            @ApiResponse(responseCode = "400", description = "Invalid token")
//    })
//    @GetMapping("/verify-email")
//    public ResponseEntity<Void> verifyEmailToken(@RequestParam String token) {
//        logger.info("Verifying email token");
//        try {
//            userService.verifyEmailToken(token);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }

    // Password Reset
//    @Operation(summary = "Request password reset", description = "Requests a password reset for the user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Password reset requested"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @PostMapping("/request-password-reset")
//    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
//        logger.info("Requesting password reset for email: {}", email);
//        try {
//            userService.requestPasswordReset(email);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }

//    @Operation(summary = "Reset password", description = "Resets the password of the user using the provided token")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Password reset"),
//            @ApiResponse(responseCode = "400", description = "Invalid token")
//    })
//    @PostMapping("/reset-password")
//    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
//        logger.info("Resetting password using token");
//        try {
//            userService.resetPassword(token, newPassword);
//            return ResponseEntity.ok().build();
//        } catch (RuntimeException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }

//    // Notifications
//    @Operation(summary = "Manage notifications", description = "Enables or disables email notifications for the user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Notification preferences updated"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @PatchMapping("/{userId}/manage-notifications")
//    public ResponseEntity<Void> manageNotifications(@PathVariable String userId, @RequestParam boolean enable) {
//        logger.info("Managing notifications for user with ID: {}", userId);
//        Optional<User> user = userService.getUserById(userId);
//        if (user.isPresent()) {
//            userService.manageNotifications(userId, enable);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

//    @Operation(summary = "Send notification", description = "Sends a notification to the user")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Notification sent"),
//            @ApiResponse(responseCode = "404", description = "User not found")
//    })
//    @PostMapping("/{userId}/send-notification")
//    public ResponseEntity<Void> sendNotification(@PathVariable String userId, @RequestParam String message) {
//        logger.info("Sending notification to user with ID: {}", userId);
//        Optional<User> user = userService.getUserById(userId);
//        if (user.isPresent()) {
//            userService.sendNotification(userId, message);
//            return ResponseEntity.ok().build();
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
