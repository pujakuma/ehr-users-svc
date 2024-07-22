package com.ehr.usersvc;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.ehr.usersvc.controller.ProfileController;
import com.ehr.usersvc.model.UserProfile;
import com.ehr.usersvc.payload.request.ProfilePutRequest;
import com.ehr.usersvc.payload.response.MessageResponse;
import com.ehr.usersvc.repository.UserProfileRepository;
import com.ehr.usersvc.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

public class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    public void setup(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    @Test
    public void testCreateProfile_UserExists() throws Exception {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId("user123");

        given(userProfileRepository.existsByUserId(anyString())).willReturn(true);

        mockMvc.perform(post("/api/createProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: User is already created !"));
    }

    @Test
    public void testCreateProfile_Success() throws Exception {
        UserProfile userProfile = new UserProfile();
        userProfile.setId("1");
        userProfile.setUserId("user123");

        given(userProfileRepository.existsByUserId(anyString())).willReturn(false);
        given(userProfileService.createUserProfile(any(UserProfile.class))).willReturn(userProfile);

        mockMvc.perform(post("/api/createProfile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"user123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("1successfully created"));
    }

    @Test
    public void testGetUserProfileById_NotFound() throws Exception {
        given(userProfileRepository.findByUserId(anyString())).willReturn(Optional.empty());

        mockMvc.perform(get("/api/getprofile/user123"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserProfileById_Found() throws Exception {
        UserProfile userProfile = new UserProfile();
        userProfile.setStatus("ACTIVE");
        given(userProfileRepository.findByUserId("user123")).willReturn(Optional.of(userProfile));

        mockMvc.perform(get("/api/getprofile/user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    // Additional tests for deleteProfile and updateProfile
}