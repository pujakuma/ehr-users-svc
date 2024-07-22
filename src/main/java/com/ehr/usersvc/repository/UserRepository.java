package com.ehr.usersvc.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ehr.usersvc.model.User;
import com.ehr.usersvc.payload.request.ForgetPasswordRequest;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);

User findByEmailIgnoreCase(String email);

ForgetPasswordRequest findByEmailIgnoreCase(ForgetPasswordRequest email);

 
  

}
