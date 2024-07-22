package com.ehr.usersvc.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "treatmentHistories")
public class TreatmentHistory {
    @Id
    private String id;

    @Field("patientId")
    private String patientId;

    @Field("details")
    private String details;

    // Constructors
    public TreatmentHistory() {
    }

    public TreatmentHistory(String patientId, String details) {
        this.patientId = patientId;
        this.details = details;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "TreatmentHistory{" +
                "id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
