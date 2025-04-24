package com.example.JobPortal.service;

import com.example.JobPortal.model.User;
import com.example.JobPortal.model.UserRole;
import com.example.JobPortal.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.insert(user);
    }

    public User addJobToRecruiter(String email, String jobId) {
        User user = getUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != UserRole.RECRUITER) {
            throw new RuntimeException("Only recruiters can add job IDs");
        }

        if (user.getJobIds() == null) {
            user.setJobIds(new ArrayList<>());
        }

        user.getJobIds().add(new ObjectId(jobId));
        return userRepository.save(user);
    }

    public User removeJobFromRecruiter(String email, String jobId) {
        User user = getUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != UserRole.RECRUITER) {
            throw new RuntimeException("Only recruiters can remove job IDs");
        }

        user.getJobIds().remove(new ObjectId(jobId));
        return userRepository.save(user);
    }
}
