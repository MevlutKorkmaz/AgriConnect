package com.gri.agriconnect.service;

import com.gri.agriconnect.dto.QuestionDTO;
import com.gri.agriconnect.model.Question;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, UserService userService, ImageService imageService) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    public Question createQuestion(QuestionDTO questionDTO) throws IOException {
        Optional<User> userOpt = userService.getUserById(questionDTO.getUserId()); // Replace with actual userId from authentication
        if (userOpt.isPresent()) {
            Question question = new Question(questionDTO.getTitle(), questionDTO.getBody(), questionDTO.getUserId());

            // Handle tags
            List<String> tags = questionDTO.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    question.addCategoryTag(tag);
                }
            }

            // Handle file
            MultipartFile file = questionDTO.getFile();
            if (file != null && !file.isEmpty()) {
                String imageId = imageService.uploadImageToFileSystem(file);
                question.addImage(imageId);
            }

            return questionRepository.save(question);
        } else {
            throw new IllegalArgumentException("User does not exist.");
        }
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByUserId(String userId) {
        return questionRepository.findByUserId(userId);
    }

    public Optional<Question> getQuestionById(String questionId) {
        return questionRepository.findById(questionId);
    }

    public void deleteQuestion(String questionId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (questionOpt.isPresent()) {
            questionRepository.deleteById(questionId);
        } else {
            throw new IllegalArgumentException("Question with ID " + questionId + " does not exist.");
        }
    }

    public Question updateQuestion(String questionId, QuestionDTO updatedQuestionDTO) {
        return questionRepository.findById(questionId).map(question -> {
            question.setTitle(updatedQuestionDTO.getTitle());
            question.setBody(updatedQuestionDTO.getBody());
            question.setUserId(updatedQuestionDTO.getUserId());

            List<String> tags = updatedQuestionDTO.getTags();
            if (tags != null) {
                question.getCategoryTags().clear();
                for (String tag : tags) {
                    question.addCategoryTag(tag);
                }
            }

            MultipartFile file = updatedQuestionDTO.getFile();
            if (file != null && !file.isEmpty()) {
                try {
                    String imageId = imageService.uploadImageToFileSystem(file);
                    question.addImage(imageId);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }

            question.setUpdatedAt(LocalDateTime.now());
            return questionRepository.save(question);
        }).orElseThrow(() -> new IllegalArgumentException("Question with ID " + questionId + " does not exist."));
    }

    public void addCategoryTag(String questionId, String tag) {
        questionRepository.findById(questionId).ifPresent(question -> {
            question.addCategoryTag(tag);
            questionRepository.save(question);
        });
    }

    public void removeCategoryTag(String questionId, String tag) {
        questionRepository.findById(questionId).ifPresent(question -> {
            question.removeCategoryTag(tag);
            questionRepository.save(question);
        });
    }

    public void addImage(String questionId, MultipartFile image) throws IOException {
        questionRepository.findById(questionId).ifPresent(question -> {
            try {
                String imageId = imageService.uploadImageToFileSystem(image);
                question.addImage(imageId);
                questionRepository.save(question);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        });
    }

    public void addImages(String questionId, MultipartFile[] images) throws IOException {
        questionRepository.findById(questionId).ifPresent(question -> {
            for (MultipartFile image : images) {
                try {
                    String imageId = imageService.uploadImageToFileSystem(image);
                    question.addImage(imageId);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }
            questionRepository.save(question);
        });
    }

    public void removeImage(String questionId, String imageId) {
        questionRepository.findById(questionId).ifPresent(question -> {
            question.removeImage(imageId);
            questionRepository.save(question);
        });
    }

    public Question likeQuestion(String questionId) {
        return questionRepository.findById(questionId).map(question -> {
            question.incrementLikeCount();
            return questionRepository.save(question);
        }).orElseThrow(() -> new IllegalArgumentException("Question with ID " + questionId + " does not exist."));
    }

    public Question unlikeQuestion(String questionId) {
        return questionRepository.findById(questionId).map(question -> {
            question.setLikeCount(Math.max(question.getLikeCount() - 1, 0));
            return questionRepository.save(question);
        }).orElseThrow(() -> new IllegalArgumentException("Question with ID " + questionId + " does not exist."));
    }

    // New method to search questions by category tags
    public List<Question> searchQuestionsByTag(String tag) {
        return questionRepository.findByCategoryTagsContaining(tag);
    }

    // New method to search questions by title or body
    public List<Question> searchQuestionsByTitleOrBody(String searchText) {
        return questionRepository.findByTitleContainingOrBodyContaining(searchText, searchText);
    }

    // Add saveQuestion method
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }
}
