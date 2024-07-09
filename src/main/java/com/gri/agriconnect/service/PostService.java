package com.gri.agriconnect.service;

import com.gri.agriconnect.dto.PostDTO;
import com.gri.agriconnect.model.Post;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public PostService(PostRepository postRepository, UserService userService, ImageService imageService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.imageService = imageService;
    }

    public Post createPost(String userId, String title, PostDTO postDTO) throws IOException {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isPresent()) {
            Post post = new Post(userId, title, postDTO.getContent());

            // Handle tags
            List<String> tags = postDTO.getCategoryTags();
            if (tags != null) {
                for (String tag : tags) {
                    post.addCategoryTag(tag);
                }
            }

            // Handle image file
            MultipartFile imageFile = postDTO.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageId = imageService.uploadImageToFileSystem(imageFile);
                post.addImageId(imageId);
            }

            return postRepository.save(post);
        } else {
            throw new IllegalArgumentException("User does not exist.");
        }
    }

    public Post savePost(Post post) {
        Optional<User> userOpt = userService.getUserById(post.getUserId());
        if (userOpt.isPresent()) {
            Post savedPost = postRepository.save(post);
            userService.addPostToUser(post.getUserId(), savedPost.getPostId());
            return savedPost;
        } else {
            throw new IllegalArgumentException("User with ID " + post.getUserId() + " does not exist.");
        }
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findByUserId(userId);
    }

    public Optional<Post> getPostById(String postId) {
        return postRepository.findById(postId);
    }

    public void deletePost(String postId) {
        Optional<Post> postOpt = postRepository.findById(postId);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();
            userService.removePostFromUser(post.getUserId(), postId);
            postRepository.deleteById(postId);
        } else {
            throw new IllegalArgumentException("Post with ID " + postId + " does not exist.");
        }
    }

    public Post updatePost(String postId, PostDTO postDTO) throws IOException {
        return postRepository.findById(postId).map(post -> {
            post.setContent(postDTO.getContent());
            post.setCategoryTags(postDTO.getCategoryTags());

            MultipartFile imageFile = postDTO.getImageFile();
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageId = imageService.uploadImageToFileSystem(imageFile);
                    post.addImageId(imageId);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }
            post.setUpdatedAt(LocalDateTime.now());
            return postRepository.save(post);
        }).orElseThrow(() -> new IllegalArgumentException("Post with ID " + postId + " does not exist."));
    }

    public void addCategoryTag(String postId, String tag) {
        postRepository.findById(postId).ifPresent(post -> {
            post.addCategoryTag(tag);
            postRepository.save(post);
        });
    }

    public void removeCategoryTag(String postId, String tag) {
        postRepository.findById(postId).ifPresent(post -> {
            post.removeCategoryTag(tag);
            postRepository.save(post);
        });
    }

    public void addImage(String postId, MultipartFile image) throws IOException {
        postRepository.findById(postId).ifPresent(post -> {
            try {
                String imageId = imageService.uploadImageToFileSystem(image);
                post.addImageId(imageId);
                postRepository.save(post);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        });
    }

    public void addImages(String postId, MultipartFile[] images) throws IOException {
        postRepository.findById(postId).ifPresent(post -> {
            try {
                for (MultipartFile image : images) {
                    String imageId = imageService.uploadImageToFileSystem(image);
                    post.addImageId(imageId);
                }
                postRepository.save(post);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload images", e);
            }
        });
    }

    public void removeImage(String postId, String imageId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.removeImage(imageId);
            postRepository.save(post);
        });
    }

    public Post likePost(String postId) {
        return postRepository.findById(postId).map(post -> {
            post.setLikeCount(post.getLikeCount() + 1);
            return postRepository.save(post);
        }).orElseThrow(() -> new IllegalArgumentException("Post with ID " + postId + " does not exist."));
    }

    public Post unlikePost(String postId) {
        return postRepository.findById(postId).map(post -> {
            post.setLikeCount(Math.max(post.getLikeCount() - 1, 0));
            return postRepository.save(post);
        }).orElseThrow(() -> new IllegalArgumentException("Post with ID " + postId + " does not exist."));
    }

    public void incrementShareCount(String postId) {
        postRepository.findById(postId).ifPresent(post -> {
            post.incrementShareCount();
            postRepository.save(post);
        });
    }

    // New method to search posts by category tags
    public List<Post> searchPostsByTag(String tag) {
        return postRepository.findByCategoryTagsContaining(tag);
    }

    // Updated method to search posts by title or content
    public List<Post> searchPostsByTitleOrContent(String searchText) {
        return postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(searchText, searchText);
    }
}
