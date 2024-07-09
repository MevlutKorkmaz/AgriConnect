package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email); // Ensure email uniqueness
    Optional<User> findByEmailVerificationToken(String token);
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    List<User> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName); // Search by name
}

