package com.gri.agriconnect.controller;

import com.gri.agriconnect.dto.QuestionDTO;
import com.gri.agriconnect.model.Image;
import com.gri.agriconnect.model.Question;
import com.gri.agriconnect.service.StatService;
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
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/stat")
@Validated
@Tag(name = "Stat", description = "Processes for personalized content recommendations")
public class StatController {

    private static final Logger logger = LoggerFactory.getLogger(StatController.class);

    private final StatService statService;

    @Autowired
    public StatController(StatService statService){
        this.statService = statService;
    }

    @Operation(summary = "Get a img recommendation for user",
            description = "Returns a image that is in accordance with users' most interacted tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recommended Image returned",
                    content = @Content(schema = @Schema(implementation = Image.class))),
            @ApiResponse(responseCode = "400", description = "Recommendation Image failed")
    })
    @GetMapping("/toppref")
    public ResponseEntity<?> toppref(@Validated @PathVariable String userId) throws IOException {

        logger.info("Looking for a image to recommend");
        try {

            byte[] imageData = statService.getRecommendation(userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(imageData);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
