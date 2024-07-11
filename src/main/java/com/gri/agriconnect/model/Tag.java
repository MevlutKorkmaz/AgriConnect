package com.gri.agriconnect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "tags")
public class Tag {
    @Id String id;
    String name;
    List<String> contentIds;
}
