package com.ehr.usersvc.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;



public class ProfilePostRequest {

	
	private String userId;
	@NotBlank
	@Size(max = 20)
	private String name;
	private String emailId;

	private String mobileno;
	private String address;
	private String profilPhoto;
	private String status;
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
	

}
