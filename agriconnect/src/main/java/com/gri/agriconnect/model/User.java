package com.gri.agriconnect.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Unique
    private String accountName;
    private String fullName;

    private String password;
    private String email;

    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private String phoneNo;
    private String location;
}