package com.ehr.usersvc.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class AccessTestController {
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('DOCTOR') or hasRole('NURSE') or hasRole('PATIENT') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/doctor")
	@PreAuthorize("hasRole('DOCTOR')")
	public String doctorAccess() {
		return "DOCTOR Content.";
	}

	@GetMapping("/nurse")
	@PreAuthorize("hasRole('NURSE')")
	public String nurseAccess() {
		return "NURSE Content.";
	}

	@GetMapping("/patient")
	@PreAuthorize("hasRole('PATIENT')")
	public String patientAccess() {
		return "PATIENT Content.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}
}
