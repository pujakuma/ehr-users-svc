package com.ehr.usersvc.service;

import com.ehr.usersvc.model.UserProfile;


public interface UserProfileService {
	
	public UserProfile createUserProfile(UserProfile request);

	public UserProfile getUserInfoById(String id);

	public UserProfile deleteProfile(String id);

	UserProfile updatePatientRecord(UserProfile record);

	
}
