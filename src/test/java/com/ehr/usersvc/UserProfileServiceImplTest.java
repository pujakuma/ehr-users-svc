package com.ehr.usersvc;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ehr.usersvc.model.UserProfile;
import com.ehr.usersvc.repository.UserProfileRepository;
import com.ehr.usersvc.service.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;
@SpringBootTest
public class UserProfileServiceImplTest {

    @Mock
    private UserProfileRepository userRepo;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    @Autowired
    private UserProfileServiceImpl userProfileService;

    @BeforeEach
    void setUp() {
        // Initialize mocks created above
    }

    @Test
    void testCreateUserProfile() {
        UserProfile request = new UserProfile();
        request.setId("123");
        request.setStatus("PENDING");

        UserProfile savedUser = new UserProfile();
        savedUser.setId("123");
        savedUser.setStatus("ACTIVE");

        when(userRepo.save(any(UserProfile.class))).thenReturn(savedUser);

        UserProfile result = userProfileService.createUserProfile(request);

        assertEquals("ACTIVE", result.getStatus());
        verify(userRepo).save(request);
    }

    @Test
    void testUpdatePatientRecord() {
        UserProfile record = new UserProfile();
        record.setId("123");
        record.setStatus("ACTIVE");

        when(userRepo.save(any(UserProfile.class))).thenReturn(record);

        UserProfile updatedRecord = userProfileService.updatePatientRecord(record);

        assertEquals("123", updatedRecord.getId());
        verify(kafkaTemplate).send("patientRecordUpdatedTopic", "PatientRecordUpdated", record);
        verify(userRepo).save(record);
    }

    @Test
    void testDeleteProfile() {
        UserProfile existingUser = new UserProfile();
        existingUser.setId("123");

        when(userRepo.findByUserId("123")).thenReturn(Optional.of(existingUser));

        userProfileService.deleteProfile("123");

        verify(userRepo).findByUserId("123");
        // Additional assertions can be made based on method's expected behavior
    }
}
