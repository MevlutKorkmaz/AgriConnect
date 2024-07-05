package com.gri.agriconnect.service;



import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


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
            user.setAccountName(userDetails.getAccountName());
            user.setFullName(userDetails.getFullName());
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
            return userRepository.save(user);
        });
    }


    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }


    public List<User> searchUsersByAccountName(String accountName) {
        return userRepository.findByAccountNameContaining(accountName);
    }


    public Optional<User> updateUserStatus(String userId, boolean enabled) {
        return userRepository.findById(userId).map(user -> {
            user.setEnabled(enabled);
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
}

