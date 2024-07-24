package com.ehr.usersvc.controller;

import java.util.List;
import java.util.Optional;

import com.ehr.usersvc.model.TreatmentHistory;
import com.ehr.usersvc.model.UserProfile;
import com.ehr.usersvc.payload.request.ProfilePutRequest;
import com.ehr.usersvc.payload.response.MessageResponse;
import com.ehr.usersvc.repository.TreatmentHistoryRepository;
import com.ehr.usersvc.repository.UserProfileRepository;
import com.ehr.usersvc.repository.UserRepository;
import com.ehr.usersvc.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    @Autowired
    UserProfileService profileService;
    @Autowired
    private UserProfileRepository userRepo;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserProfileService userProfileService;
    @Autowired
    private TreatmentHistoryRepository treatmentHistoryRepository;

    @PostMapping("/createProfile")
    public ResponseEntity<MessageResponse> createProfile(@RequestBody UserProfile request) {
        logger.info("Attempting to create a new user profile for user ID: {}", request.getUserId());
        try {
            if (userRepo.existsByUserId(request.getUserId())) {
                logger.warn("Creation failed: User profile already exists for user ID: {}", request.getUserId());
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: User is already created !"));
            }
            UserProfile response = profileService.createUserProfile(request);
            logger.info("User profile successfully created with ID: {}", response.getId());
            return new ResponseEntity<>(new MessageResponse(response.getId() + " successfully created"), HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Failed to create user profile for user ID: {}", request.getUserId(), e);
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getprofile/{userid}")
    @Cacheable(value = "userProfiles", key = "#id")
    public ResponseEntity<UserProfile> getUserProfileById(@PathVariable("userid") String id) {
        logger.debug("Fetching user profile for user ID: {}", id);
        Optional<UserProfile> response = userRepo.findByUserId(id);
        if (response.isPresent() && response.get().getStatus().equalsIgnoreCase("ACTIVE")) {
            logger.info("User profile found and active for user ID: {}", id);
            return new ResponseEntity<>(response.get(), HttpStatus.OK);
        } else {
            logger.warn("User profile not found or inactive for user ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/deleteprofile/{userid}")
    @CacheEvict(value = "userProfiles", key = "#id")
    public ResponseEntity<MessageResponse> deleteProfile(@PathVariable("userid") String id) {
        logger.info("Attempting to deactivate/delete user profile for user ID: {}", id);
        try {
            Optional<UserProfile> response = userRepo.findByUserId(id);
            if (response.isPresent()) {
                userRepo.deleteById(response.get().getId());
                userRepository.deleteById(id);
                logger.info("User profile successfully deactivated for user ID: {}", id);
                return new ResponseEntity<>(new MessageResponse(id + " successfully deactivated"), HttpStatus.OK);
            } else {
                logger.warn("Deactivation failed: No user profile found for user ID: {}", id);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Failed to deactivate user profile for user ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/updateprofile/{userid}")
    @CacheEvict(value = "userProfiles", key = "#id")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable("userid") String id, @RequestBody ProfilePutRequest request) {
        logger.info("Attempting to update user profile for user ID: {}", id);
        Optional<UserProfile> response = userRepo.findByUserId(id);
        if (response.isPresent()) {
            UserProfile userprofile = response.get();
            userprofile.setAddress(request.getAddress());
            userprofile.setEmailId(request.getEmailId());
            userprofile.setMobileno(request.getMobileno());
            userprofile.setProfilPhoto(request.getProfilPhoto());
            UserProfile updatedProfile = userProfileService.updatePatientRecord(userprofile);
            logger.info("User profile successfully updated for user ID: {}", id);
            return new ResponseEntity<>(updatedProfile, HttpStatus.OK);
        } else {
            logger.warn("Update failed: No user profile found for user ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/treatment")
    public List<TreatmentHistory> getTreatment(@RequestParam(value = "patientId") Long patientId) {
        logger.debug("Fetching treatment history for patient ID: {}", patientId);
        List<TreatmentHistory> treatments = treatmentHistoryRepository.findByPatientId(String.valueOf(patientId));
        logger.info("Treatment history fetched for patient ID: {}", patientId);
        return treatments;
    }
}
