package com.example.JobPortal.repository;

import com.example.JobPortal.model.JobApplication;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobApplicationRepository extends MongoRepository<JobApplication, ObjectId> {
    Optional<JobApplication> findByJobId(ObjectId jobId);
}
