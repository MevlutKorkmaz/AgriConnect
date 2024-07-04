package com.gri.agriconnect.model;

import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Document(collection = "users")
public class User {
    @Id
    @GeneratedValue
    private String userId;

    @NotBlank
    private String accountName;
    private String fullName;

    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;
    private boolean accountLocked;
    private boolean enabled;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    private String phoneNo;
    private String location;

    private List<String> conversationIds; // List of conversation IDs the user is part of
    private List<String> productIds;
    private List<String> postIds;
}









