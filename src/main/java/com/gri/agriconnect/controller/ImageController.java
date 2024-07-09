package com.gri.agriconnect.controller;

import com.gri.agriconnect.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@Validated
@Tag(name = "Image", description = "API for managing images")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageService service;

    @Autowired
    public ImageController(ImageService service) {
        this.service = service;
    }

    @Operation(summary = "Upload an image", description = "Uploads an image to the file system and saves metadata")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/fileSystem")
    public ResponseEntity<?> uploadImageToFileSystem(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = service.uploadImageToFileSystem(file);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(uploadImage);
    }

    @Operation(summary = "Download an image by file name", description = "Downloads an image from the file system by file name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image downloaded successfully",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/fileSystem/name/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = service.downloadImageFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @Operation(summary = "Download an image by ID", description = "Downloads an image from the file system by image ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image downloaded successfully",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
            @ApiResponse(responseCode = "404", description = "Image not found")
    })
    @GetMapping("/fileSystem/id/{imageId}")
    public ResponseEntity<?> downloadImageById(@PathVariable String imageId) throws IOException {
        byte[] imageData = service.downloadImageById(imageId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }
}

//@ControllerAdvice
//class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
//        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("File too large!");
//    }
//}
