package com.ehr.usersvc.service;

import java.util.Optional;

import com.ehr.usersvc.model.UserProfile;
import com.ehr.usersvc.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserProfileRepository userRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public UserProfile createUserProfile(UserProfile request) {
        logger.info("Creating user profile for user ID: {}", request.getUserId());
        request.setStatus("ACTIVE");
        UserProfile savedUser = userRepo.save(request);
        kafkaTemplate.send("userProfileTopic", "UserProfileCreated", savedUser);
        logger.info("User profile created successfully for user ID: {}", request.getUserId());
        return savedUser;
    }

    @Override
    @Cacheable(value = "userProfiles", key = "#id")
    public UserProfile getUserInfoById(String id) {
        logger.debug("Fetching user profile for user ID: {}", id);
        return userRepo.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = "userProfiles", key = "#id")
    public UserProfile deleteProfile(String id) {
        logger.info("Request to delete/deactivate profile for user ID: {}", id);
        Optional<UserProfile> response = userRepo.findByUserId(id);
        if (response.isPresent()) {
            UserProfile userprofile = response.get();
            userprofile.setStatus("DEACTIVATED");
            UserProfile updatedProfile = userRepo.save(userprofile);
            kafkaTemplate.send("userProfileTopic", "UserProfileDeactivated", updatedProfile);
            logger.info("User profile deactivated for user ID: {}", id);
            return updatedProfile;
        }
        logger.warn("User profile not found for user ID: {}", id);
        return null;
    }

    @Override
    @CacheEvict(value = "userProfiles", key = "#record.userId")
    @CachePut(value = "userProfiles", key = "#record.userId")
    public UserProfile updatePatientRecord(UserProfile record) {
        logger.info("Updating user profile for user ID: {}", record.getUserId());
        UserProfile updatedRecord = userRepo.save(record);
        kafkaTemplate.send("userProfileTopic", "UserProfileUpdated", updatedRecord);
        logger.info("User profile updated successfully for user ID: {}", record.getUserId());
        return updatedRecord;
    }
}