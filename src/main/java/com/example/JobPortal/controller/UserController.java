package com.example.JobPortal.controller;

import com.example.JobPortal.model.User;
import com.example.JobPortal.model.UserRole;
import com.example.JobPortal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Optional<User>> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>("Email already taken", HttpStatus.BAD_REQUEST);
        }

        // Set role-specific fields to null if not applicable
        if (user.getRole() == UserRole.JOB_SEEKER) {
            user.setCompany(null);
            user.setLocation(null);
            user.setJobIds(null);
        } else if (user.getRole() == UserRole.RECRUITER) {
            user.setSkills(null);
        }

        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> payload, HttpServletRequest request) {
        String email = payload.get("email");
        String password = payload.get("password");

        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Email not found"), HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            return new ResponseEntity<>(Map.of("error", "Wrong password"), HttpStatus.UNAUTHORIZED);
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
        SecurityContextHolder.getContext().setAuthentication(authToken);
        HttpSession session = request.getSession(true);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", session.getId());
        responseBody.put("user", user);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @PostMapping("/{id}/appendjob")
    public ResponseEntity<?> appendJob(@PathVariable String email, @RequestBody String jobId) {
        try {
            return new ResponseEntity<>(userService.addJobToRecruiter(email, jobId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/removejob")
    public ResponseEntity<?> removeJob(@PathVariable String email, @RequestBody String jobId) {
        return new ResponseEntity<>(userService.removeJobFromRecruiter(email, jobId), HttpStatus.OK);
    }
}
