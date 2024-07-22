package com.ehr.usersvc.model;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ConfirmationToken {
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	
	private long tokenid;


	private String confirmationToken;
	
	@CreatedDate
	private LocalDate createdDate;
	
	/*@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")*/
	@DBRef
    private User user;
	
	public ConfirmationToken() {
	}
	
	public ConfirmationToken(User user) {
		this.user = user;
		//createdDate = new Date();
		confirmationToken = UUID.randomUUID().toString();
	}

	public String getConfirmationToken() {
		return confirmationToken;
	}

	public void setConfirmationToken(String confirmationToken) {
		this.confirmationToken = confirmationToken;
	}

	public LocalDate getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDate createdDate) {
		this.createdDate = createdDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public long getTokenid() {
		return tokenid;
	}

	public void setTokenid(long tokenid) {
		this.tokenid = tokenid;
	}

}
