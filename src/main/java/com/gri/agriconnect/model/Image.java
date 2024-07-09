package com.gri.agriconnect.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "images")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    private String imageId;
    private String name;
    private String type;
    private String filePath;
}
