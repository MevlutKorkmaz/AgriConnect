package com.gri.agriconnect.service;

import com.gri.agriconnect.dto.CommentDTO;
import com.gri.agriconnect.model.Comment;
import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.model.Question;
import com.gri.agriconnect.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final QuestionService questionService;

    @Autowired
    public CommentService(CommentRepository commentRepository, PostService postService, QuestionService questionService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.questionService = questionService;
    }

    public Comment saveComment(CommentDTO commentDTO) {
        boolean isValid = false;
        Comment comment = new Comment(
                commentDTO.getUserId(),
                commentDTO.getContent(),
                commentDTO.getPostId(),
                commentDTO.getQuestionId()
        );

        if (comment.getPostId() != null) {
            Optional<Post> postOpt = postService.getPostById(comment.getPostId());
            if (postOpt.isPresent()) {
                Post post = postOpt.get();
                Comment savedComment = commentRepository.save(comment);
                post.getCommentIds().add(savedComment.getCommentId());
                postService.savePost(post);
                isValid = true;
            }
        }

        if (comment.getQuestionId() != null) {
            Optional<Question> questionOpt = questionService.getQuestionById(comment.getQuestionId());
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                Comment savedComment = commentRepository.save(comment);
                question.getCommentIds().add(savedComment.getCommentId());
                questionService.saveQuestion(question);
                isValid = true;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException("Post or Question with given ID does not exist.");
        }

        comment.setUpdatedAt(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    public List<Comment> getCommentsByUserId(String userId) {
        return commentRepository.findByUserId(userId);
    }

    public List<Comment> getCommentsByPostId(String postId) {
        return commentRepository.findByPostId(postId);
    }

    public List<Comment> getCommentsByQuestionId(String questionId) {
        return commentRepository.findByQuestionId(questionId);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    public void deleteComment(String commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Comment comment = commentOpt.get();

            if (comment.getPostId() != null) {
                Optional<Post> postOpt = postService.getPostById(comment.getPostId());
                if (postOpt.isPresent()) {
                    Post post = postOpt.get();
                    post.getCommentIds().remove(commentId);
                    postService.savePost(post);
                }
            }

            if (comment.getQuestionId() != null) {
                Optional<Question> questionOpt = questionService.getQuestionById(comment.getQuestionId());
                if (questionOpt.isPresent()) {
                    Question question = questionOpt.get();
                    question.getCommentIds().remove(commentId);
                    questionService.saveQuestion(question);
                }
            }

            commentRepository.deleteById(commentId);
        } else {
            throw new IllegalArgumentException("Comment with ID " + commentId + " does not exist.");
        }
    }

    public Comment updateComment(String commentId, CommentDTO updatedCommentDTO) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setContent(updatedCommentDTO.getContent());
            comment.setUpdatedAt(LocalDateTime.now());
            comment.markAsEdited(updatedCommentDTO.getContent());
            return commentRepository.save(comment);
        }).orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " does not exist."));
    }

    public Comment replyToComment(String parentCommentId, CommentDTO replyDTO) {
        Optional<Comment> parentCommentOpt = commentRepository.findById(parentCommentId);
        if (parentCommentOpt.isPresent()) {
            Comment reply = new Comment(
                    replyDTO.getUserId(),
                    replyDTO.getContent(),
                    replyDTO.getPostId(),
                    replyDTO.getQuestionId()
            );
            reply.setParentCommentId(parentCommentId);
            reply.setCreatedAt(LocalDateTime.now());
            reply.setUpdatedAt(LocalDateTime.now());
            Comment savedReply = commentRepository.save(reply);

            if (reply.getPostId() != null) {
                Optional<Post> postOpt = postService.getPostById(reply.getPostId());
                if (postOpt.isPresent()) {
                    Post post = postOpt.get();
                    post.getCommentIds().add(savedReply.getCommentId());
                    postService.savePost(post);
                }
            }

            if (reply.getQuestionId() != null) {
                Optional<Question> questionOpt = questionService.getQuestionById(reply.getQuestionId());
                if (questionOpt.isPresent()) {
                    Question question = questionOpt.get();
                    question.getCommentIds().add(savedReply.getCommentId());
                    questionService.saveQuestion(question);
                }
            }

            return savedReply;
        } else {
            throw new IllegalArgumentException("Parent comment with ID " + parentCommentId + " does not exist.");
        }
    }

    public List<Comment> getRepliesForComment(String commentId) {
        return commentRepository.findByParentCommentId(commentId);
    }

    public Comment likeComment(String commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setLikeCount(comment.getLikeCount() + 1);
            return commentRepository.save(comment);
        }).orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " does not exist."));
    }

    public Comment unlikeComment(String commentId) {
        return commentRepository.findById(commentId).map(comment -> {
            comment.setLikeCount(Math.max(comment.getLikeCount() - 1, 0));
            return commentRepository.save(comment);
        }).orElseThrow(() -> new IllegalArgumentException("Comment with ID " + commentId + " does not exist."));
    }
}
