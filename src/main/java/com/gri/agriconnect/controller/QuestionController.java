package com.gri.agriconnect.controller;

import com.gri.agriconnect.dto.QuestionDTO;
import com.gri.agriconnect.model.Question;
import com.gri.agriconnect.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/questions")
@Validated
@Tag(name = "Question", description = "API for managing questions")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "Create a new question", description = "Adds a new question to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Question created",
                    content = @Content(schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create")
    public ResponseEntity<Question> createQuestion(@Validated @ModelAttribute QuestionDTO questionDTO) throws IOException {
        logger.info("Creating a new question");
        try {
            Question createdQuestion = questionService.createQuestion(questionDTO);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get all questions", description = "Fetches all questions in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions fetched",
                    content = @Content(schema = @Schema(implementation = Question.class)))
    })
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        logger.info("Fetching all questions");
        List<Question> questions = questionService.getAllQuestions();
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @Operation(summary = "Get questions by user ID", description = "Fetches questions by their user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions fetched",
                    content = @Content(schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "No questions found")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Question>> getQuestionsByUserId(@PathVariable String userId) {
        logger.info("Fetching questions for user ID: {}", userId);
        List<Question> questions = questionService.getQuestionsByUserId(userId);
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No questions found for user ID: " + userId);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @Operation(summary = "Get question by ID", description = "Fetches a question by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question fetched",
                    content = @Content(schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable String questionId) {
        logger.info("Fetching question with ID: {}", questionId);
        Optional<Question> question = questionService.getQuestionById(questionId);
        return question.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found with ID: " + questionId));
    }

    @Operation(summary = "Delete question", description = "Deletes a question by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question deleted"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String questionId) {
        logger.info("Deleting question with ID: {}", questionId);
        try {
            questionService.deleteQuestion(questionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Update question", description = "Updates a question by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated",
                    content = @Content(schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @PutMapping("/{questionId}")
    public ResponseEntity<Question> updateQuestion(@PathVariable String questionId, @Valid @RequestBody QuestionDTO questionDTO) {
        logger.info("Updating question with ID: {}", questionId);
        try {
            Question updatedQuestion = questionService.updateQuestion(questionId, questionDTO);
            return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Search questions by tag", description = "Fetch questions by a specific tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of questions",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "No questions found for the tag")
    })
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Question>> searchQuestionsByTag(
            @Parameter(description = "Tag to search questions by") @PathVariable String tag) {
        logger.info("Searching questions by tag: {}", tag);
        List<Question> questions = questionService.searchQuestionsByTag(tag);
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No questions found for the tag: " + tag);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @Operation(summary = "Search questions by title or body", description = "Fetch questions by title or body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of questions",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Question.class))),
            @ApiResponse(responseCode = "404", description = "No questions found for the search text")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestionsByTitleOrBody(
            @Parameter(description = "Search text for title or body") @RequestParam String searchText) {
        logger.info("Searching questions by title or body: {}", searchText);
        List<Question> questions = questionService.searchQuestionsByTitleOrBody(searchText);
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No questions found for the search text: " + searchText);
        }
        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    @Operation(summary = "Like a question", description = "Like a question by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question liked successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @PostMapping("/{questionId}/like")
    public ResponseEntity<Question> likeQuestion(
            @Parameter(description = "ID of the question to like") @PathVariable String questionId) {
        logger.info("Liking question with ID: {}", questionId);
        try {
            Question likedQuestion = questionService.likeQuestion(questionId);
            return new ResponseEntity<>(likedQuestion, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Unlike a question", description = "Unlike a question by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question unliked successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @PostMapping("/{questionId}/unlike")
    public ResponseEntity<Question> unlikeQuestion(
            @Parameter(description = "ID of the question to unlike") @PathVariable String questionId) {
        logger.info("Unliking question with ID: {}", questionId);
        try {
            Question unlikedQuestion = questionService.unlikeQuestion(questionId);
            return new ResponseEntity<>(unlikedQuestion, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Add images to question", description = "Adds multiple images to a question by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images added successfully"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    @PostMapping("/{questionId}/images")
    public ResponseEntity<Void> addImages(
            @Parameter(description = "ID of the question to add images to") @PathVariable String questionId,
            @RequestParam("files") MultipartFile[] images) {
        logger.info("Adding images to question with ID: {}", questionId);
        try {
            questionService.addImages(questionId, images);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload images");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
