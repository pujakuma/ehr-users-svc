package com.ehr.usersvc.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ehr.usersvc.model.ERole;
import com.ehr.usersvc.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
