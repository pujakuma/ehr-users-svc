package com.ehr.usersvc.model;

import java.sql.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile")
public class UserProfile {
	
	public UserProfile(){
		
	}
	
	public UserProfile(String userId, @NotBlank @Size(max = 20) String name, String emailId, String mobileno,
			String address, String profilPhoto, String status, Date createdDate) {
		super();
		this.userId = userId;
		this.name = name;
		this.emailId = emailId;
		this.mobileno = mobileno;
		this.address = address;
		this.profilPhoto = profilPhoto;
		this.status = status;
		this.createdDate = createdDate;
	}

	@Id
	private String id;
	
	private String userId;
	@NotBlank
	  @Size(max = 20)
	private String name;
	private String emailId;

	private String mobileno;
	private String address;
	private String profilPhoto;
	private String status;
	private Date createdDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getMobileno() {
		return mobileno;
	}
	public void setMobileno(String mobileno) {
		this.mobileno = mobileno;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getProfilPhoto() {
		return profilPhoto;
	}
	public void setProfilPhoto(String profilPhoto) {
		this.profilPhoto = profilPhoto;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	
	
	

}
