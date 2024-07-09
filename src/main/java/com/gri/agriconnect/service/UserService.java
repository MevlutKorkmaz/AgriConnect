package com.gri.agriconnect.service;

import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.UserRepository;
import com.gri.agriconnect.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;

    @Autowired
    public UserService(UserRepository userRepository, ImageService imageService) {
        this.userRepository = userRepository;
        this.imageService = imageService;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> updateUser(String userId, User userDetails) {
        return userRepository.findById(userId).map(user -> {
            user.setFirstName(userDetails.getFirstName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setPhoneNo(userDetails.getPhoneNo());
            user.setLocation(userDetails.getLocation());
            user.setFollowerCount(userDetails.getFollowerCount());
            user.setFollowingCount(userDetails.getFollowingCount());
            user.setConversationCount(userDetails.getConversationCount());
            user.setProductCount(userDetails.getProductCount());
            user.setPostCount(userDetails.getPostCount());
            user.setFollowerIds(userDetails.getFollowerIds());
            user.setFollowingIds(userDetails.getFollowingIds());
            user.setConversationIds(userDetails.getConversationIds());
            user.setProductIds(userDetails.getProductIds());
            user.setPostIds(userDetails.getPostIds());
            user.setAccountLocked(userDetails.getAccountLocked());
            user.setAccountEnabled(userDetails.getAccountEnabled());
            user.setEmailVerified(userDetails.getEmailVerified());
            user.setEmailVerificationToken(userDetails.getEmailVerificationToken());
            user.setProfilePhotoId(userDetails.getProfilePhotoId());
            user.setPrivateAccount(userDetails.getPrivateAccount());
            user.setBio(userDetails.getBio());
            user.setCoverPhotoId(userDetails.getCoverPhotoId());
            user.setDateOfBirth(userDetails.getDateOfBirth());
            user.setSocialMediaLinks(userDetails.getSocialMediaLinks());
            user.setInterests(userDetails.getInterests());
            user.setEmailNotificationsEnabled(userDetails.getEmailNotificationsEnabled());
            return userRepository.save(user);
        });
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public List<User> searchUsersByName(String name) {
        return userRepository.findByFirstNameContainingOrLastNameContaining(name, name);
    }

    public Optional<User> updateUserStatus(String userId, boolean accountEnabled) {
        return userRepository.findById(userId).map(user -> {
            user.setAccountEnabled(accountEnabled);
            return userRepository.save(user);
        });
    }

    public Optional<User> updateUserLockStatus(String userId, boolean accountLocked) {
        return userRepository.findById(userId).map(user -> {
            user.setAccountLocked(accountLocked);
            return userRepository.save(user);
        });
    }

    public Optional<User> updateUserPassword(String userId, String newPassword) {
        return userRepository.findById(userId).map(user -> {
            user.setPassword(newPassword);
            return userRepository.save(user);
        });
    }

    // Methods for profile photo and cover photo
    public void updateProfilePhoto(String userId, MultipartFile profilePhoto) throws IOException {
        userRepository.findById(userId).ifPresent(user -> {
            try {
                String profilePhotoId = imageService.uploadImageToFileSystem(profilePhoto);
                user.setProfilePhotoId(profilePhotoId);
                userRepository.save(user);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload profile photo", e);
            }
        });
    }

    public void updateCoverPhoto(String userId, MultipartFile coverPhoto) throws IOException {
        userRepository.findById(userId).ifPresent(user -> {
            try {
                String coverPhotoId = imageService.uploadImageToFileSystem(coverPhoto);
                user.setCoverPhotoId(coverPhotoId);
                userRepository.save(user);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload cover photo", e);
            }
        });
    }

    public byte[] getProfilePhoto(String userId) throws IOException {
        return userRepository.findById(userId).map(user -> {
            try {
                return imageService.downloadImageById(user.getProfilePhotoId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to retrieve profile photo", e);
            }
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public byte[] getCoverPhoto(String userId) throws IOException {
        return userRepository.findById(userId).map(user -> {
            try {
                return imageService.downloadImageById(user.getCoverPhotoId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to retrieve cover photo", e);
            }
        }).orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Methods to add and remove posts and products
    public void addPostToUser(String userId, String postId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addPost(postId);
            userRepository.save(user);
        });
    }

    public void removePostFromUser(String userId, String postId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.removePost(postId);
            userRepository.save(user);
        });
    }

    public void addProductToUser(String userId, String productId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addProduct(productId);
            userRepository.save(user);
        });
    }

    public void removeProductFromUser(String userId, String productId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.removeProduct(productId);
            userRepository.save(user);
        });
    }
}
