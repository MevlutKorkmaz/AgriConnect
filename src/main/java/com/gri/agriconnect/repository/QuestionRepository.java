package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {
    List<Question> findByUserId(String userId);
    List<Question> findByCategoryTagsContaining(String tag);
    List<Question> findByTitleContainingOrBodyContaining(String title, String body);

    @Query(sort = "{ createdAt: -1 }")
    List<Question> findAllByOrderByCreatedAtDesc(); // Newest

    @Query(sort = "{ createdAt: 1 }")
    List<Question> findAllByOrderByCreatedAtAsc(); // Oldest

    @Query("{ commentCount: 0 }")
    List<Question> findByCommentCountIsZero(); // Unanswered
}
