package com.ehr.usersvc.repository;

import org.springframework.data.repository.CrudRepository;

import com.ehr.usersvc.model.ConfirmationToken;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
	ConfirmationToken findByConfirmationToken(String confirmationToken);

}
