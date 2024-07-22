package com.ehr.usersvc.service;

import java.util.Optional;

import com.ehr.usersvc.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ehr.usersvc.model.UserProfile;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private UserProfileRepository userRepo;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public UserProfile createUserProfile(UserProfile request) {
        // verifyUserAccess(userId);
        UserProfile user = new UserProfile();
        String status = "ACTIVE";
        request.setStatus(status);
        return userRepo.save(request);
    }

    @Override
    public UserProfile getUserInfoById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserProfile deleteProfile(String id) {

        Optional<UserProfile> response = userRepo.findByUserId(id);
        //String status="DEACTIVATED";

        UserProfile userprofile = new UserProfile();
        String profileid = response.get().getId();

        return null;

    }
    public UserProfile updatePatientRecord(UserProfile record) {
        UserProfile updatedRecord = userRepo.save(record);
        kafkaTemplate.send("patientRecordUpdatedTopic", "PatientRecordUpdated", updatedRecord);
        return updatedRecord;
    }

}
