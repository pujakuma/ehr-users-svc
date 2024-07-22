package com.ehr.usersvc.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ehr.usersvc.model.UserProfile;

public interface UserProfileRepository extends MongoRepository<UserProfile, String>{

	boolean existsByUserId(String userId);

	Optional<UserProfile> findByUserId(String id);

}
