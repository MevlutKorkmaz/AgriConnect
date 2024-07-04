package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.User;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}

