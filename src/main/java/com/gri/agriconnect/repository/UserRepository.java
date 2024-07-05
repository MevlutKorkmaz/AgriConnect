package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    List<User> findByAccountNameContaining(String accountName);
}
