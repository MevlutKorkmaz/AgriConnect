package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Image;
import com.gri.agriconnect.model.Tag;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.model.UserStat;
import com.gri.agriconnect.repository.ImageRepository;
import com.gri.agriconnect.repository.TagRepository;
import com.gri.agriconnect.repository.UserStatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

@Service
public class StatService {

    private UserStatRepository userStatRepository;
    private TagRepository tagRepository;
    private ImageRepository imageRepository;
    private ImageService imageService;

    @Autowired
    public StatService(UserStatRepository userStatRepository, TagRepository tagRepository,
                       ImageRepository imageRepository, ImageService imageService){
        this.userStatRepository = userStatRepository;
        this.tagRepository = tagRepository;
        this.imageRepository = imageRepository;
        this.imageService = imageService;
    }

    public byte[] getRecommendation(String userId) {
        //get the users favorite tag.
        List<UserStat> query = userStatRepository.findByUserId(userId);

        UserStat highestStat;
        Integer tmpRatio = 0;

        if (query.size() != 0) {
            highestStat = query.get(0);
        } else {
            //user don't have any tag preference. return random image.
            int max = imageRepository.findAll().size();
            int rndInx = (int) ((Math.random() * (max - 0)) + 0);
            Image img = imageRepository.findAll().get(rndInx);
            try {
                return imageService.downloadImageById(img.getImageId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        for (UserStat uS : query) {
            if (uS.getRate() > tmpRatio) {
                tmpRatio = uS.getRate();
                highestStat = uS;
            }
        }

        Optional<Tag> favTag = tagRepository.findById(highestStat.getTagId());
        if (favTag.isPresent()) {
            Image img = imageRepository.findByTagsIn(asList(favTag.get().getName())).get(0);
            try {
                return imageService.downloadImageById(img.getImageId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        return null;
    }
}
