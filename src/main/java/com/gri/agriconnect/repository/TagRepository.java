package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.model.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends MongoRepository<Tag, String> {
    List<Tag> findByName(String name);
    Optional<Tag> findBy(String id);
}
