package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.User;
import com.gri.agriconnect.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        logger.info("Creating a new user");
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        logger.info("Fetching user with ID: {}", userId);
        Optional<User> user = userService.getUserById(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable String userId, @Valid @RequestBody User userDetails) {
        logger.info("Updating user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUser(userId, userDetails);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByAccountName(@RequestParam String accountName) {
        logger.info("Searching users by account name: {}", accountName);
        List<User> users = userService.searchUsersByAccountName(accountName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<User> updateUserStatus(@PathVariable String userId, @RequestParam boolean enabled) {
        logger.info("Updating status of user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUserStatus(userId, enabled);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/lock")
    public ResponseEntity<User> updateUserLockStatus(@PathVariable String userId, @RequestParam boolean accountLocked) {
        logger.info("Updating lock status of user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUserLockStatus(userId, accountLocked);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<User> updateUserPassword(@PathVariable String userId, @RequestParam String newPassword) {
        logger.info("Updating password of user with ID: {}", userId);
        Optional<User> updatedUser = userService.updateUserPassword(userId, newPassword);
        return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
