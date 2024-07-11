package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.UserStat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStatRepository extends MongoRepository<UserStat, String> {
    List<UserStat> findByUserId(String userId);
}
