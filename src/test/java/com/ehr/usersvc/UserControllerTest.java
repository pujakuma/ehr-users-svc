package com.ehr.usersvc;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import com.ehr.usersvc.controller.UserController;
import com.ehr.usersvc.model.User;
import com.ehr.usersvc.payload.request.LoginRequest;
import com.ehr.usersvc.payload.request.SignupRequest;
import com.ehr.usersvc.payload.response.MessageResponse;
import com.ehr.usersvc.repository.UserRepository;
import com.ehr.usersvc.security.jwt.JwtUtils;
import com.ehr.usersvc.service.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("password");

        // Mocking the authentication process
        when(jwtUtils.generateJwtToken(any())).thenReturn("dummyToken");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummyToken"));
    }

    @Test
    public void testRegisterUser_UsernameExists() throws Exception {
        SignupRequest signUpRequest = new SignupRequest();
        signUpRequest.setUsername("existingUser");
        signUpRequest.setEmail("user@example.com");
        signUpRequest.setPassword("password");

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"existingUser\",\"email\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    public void testForgotPassword_UserNotFound() throws Exception {
        when(userRepository.findByEmailIgnoreCase("nonexistent@example.com")).thenReturn(null);

        mockMvc.perform(post("/api/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nonexistent@example.com\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This email does not exist !"));
    }

    // Additional tests for resetPassword and other methods
}
