package com.ehr.usersvc.repository;

import com.ehr.usersvc.model.TreatmentHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreatmentHistoryRepository extends MongoRepository<TreatmentHistory, String> {
    List<TreatmentHistory> findByPatientId(String patientId);
}
