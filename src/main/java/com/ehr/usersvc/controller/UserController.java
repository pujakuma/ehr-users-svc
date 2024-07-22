package com.ehr.usersvc.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.ehr.usersvc.model.ConfirmationToken;
import com.ehr.usersvc.model.ERole;
import com.ehr.usersvc.model.Role;
import com.ehr.usersvc.model.User;
import com.ehr.usersvc.payload.request.LoginRequest;
import com.ehr.usersvc.payload.response.JwtResponse;
import com.ehr.usersvc.payload.response.MessageResponse;
import com.ehr.usersvc.repository.ConfirmationTokenRepository;
import com.ehr.usersvc.repository.RoleRepository;
import com.ehr.usersvc.repository.UserRepository;
import com.ehr.usersvc.security.jwt.JwtUtils;
import com.ehr.usersvc.security.services.UserDetailsImpl;
import com.ehr.usersvc.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ehr.usersvc.payload.request.SignupRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")

public class UserController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @PostMapping("/auth/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "doctor":
                        Role docRole = roleRepository.findByName(ERole.ROLE_DOCTOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(docRole);
                        break;
                    case "nurse":
                        Role nurseRole = roleRepository.findByName(ERole.ROLE_NURSE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(nurseRole);

                        break;

                    case "patient":
                        Role patientRole = roleRepository.findByName(ERole.ROLE_PATIENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(patientRole);

                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    /**
     * Receive email of the user, create token and send it via email to the user
     */

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotUserPassword(@RequestBody User user) {
        User existingUser = userRepository.findByEmailIgnoreCase(user.getEmail());
        if (existingUser != null) {
            // create token
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);
            // save it
            confirmationTokenRepository.save(confirmationToken);
            // create the email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom("pujasa25@gmail.com");
            mailMessage.setText("To complete the password reset process, please click here: "
                    + "http://localhost:8080/confirm-reset?token=" + confirmationToken.getConfirmationToken());
            emailSenderService.sendEmail(mailMessage);
            return ResponseEntity.ok(new MessageResponse("Request to reset password received. Check your inbox for the reset link !"));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(" This email does not exist ! "));
        }
    }

    /**
     * Receive the token from the link sent via email and display form to reset password
     */

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetUserPassword(@RequestBody User user) {

        if (user.getEmail() != null) {
            // use email to find user
            User tokenUser = userRepository.findByEmailIgnoreCase(user.getEmail());
            tokenUser.setEnabled(true);
            tokenUser.setPassword(encoder.encode(user.getPassword()));
            // System.out.println(tokenUser.getPassword());
            userRepository.save(tokenUser);
            return ResponseEntity.ok(new MessageResponse("Password successfully reset. You can now log in with the new credentials."));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(" The link is invalid or broken! "));
        }

    }
}
