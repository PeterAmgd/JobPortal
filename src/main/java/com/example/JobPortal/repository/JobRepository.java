package com.example.JobPortal.repository;

import com.example.JobPortal.model.Job;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, ObjectId> {
}
