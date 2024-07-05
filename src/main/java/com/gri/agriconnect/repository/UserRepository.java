package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User createUser(User user);
    User updateUser(User user);
    Optional<User> getUserById(String userId);
    List<User> getAllUsers();
    Optional<User> updateUser(String userId, User userDetails);
    void deleteUser(String userId);
    List<User> searchUsersByAccountName(String accountName);
    Optional<User> updateUserStatus(String userId, boolean enabled);
    Optional<User> updateUserLockStatus(String userId, boolean accountLocked);
    Optional<User> updateUserPassword(String userId, String newPassword);

    List<User> findByAccountNameContaining(String accountName);
}
