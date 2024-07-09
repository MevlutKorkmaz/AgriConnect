package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Image;
import com.gri.agriconnect.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    private final String FOLDER_PATH = "C:\\Users\\MK\\Desktop\\images\\";

    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String filePath = FOLDER_PATH + file.getOriginalFilename();

        Image image = imageRepository.save(Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build());

        file.transferTo(new File(filePath));

        if (image != null) {
//            return "file uploaded successfully : " + filePath;
            return image.getImageId();
        }
        return null;
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<Image> image = imageRepository.findByName(fileName);
        if (image.isPresent()) {
            String filePath = image.get().getFilePath();
            return Files.readAllBytes(new File(filePath).toPath());
        } else {
            throw new IOException("File not found with name: " + fileName);
        }
    }

    public byte[] downloadImageById(String imageId) throws IOException {
        Optional<Image> image = imageRepository.findByImageId(imageId);
        if (image.isPresent()) {
            String filePath = image.get().getFilePath();
            return Files.readAllBytes(new File(filePath).toPath());
        } else {
            throw new IOException("File not found with id: " + imageId);
        }
    }
}
