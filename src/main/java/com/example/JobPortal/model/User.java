package com.example.JobPortal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    private ObjectId id;

    private String name;
    private String email;
    private String password;
//    private UserRole role;
    private String role;
    // Fields for JOB_SEEKER
    private List<String> skills;

    // Fields for RECRUITER
    private String company;
    private String location;
    private List<ObjectId> jobIds;

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }

    @JsonProperty("jobIds")
    public List<String> getJobIdsString() {
        return jobIds != null ? jobIds.stream().map(ObjectId::toHexString).collect(Collectors.toList()) : null;
    }

    public void addJobId(ObjectId jobId) {
        if (this.jobIds != null) {
            this.jobIds.add(jobId);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole()));
    }

    @Override
    public String getUsername() {
        //spring boot as a default ct a it's username but we wanna it with email
        return email;
    }

    //note that it not override getPassword from UserDetail jut because we ue lombok it already make this function
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void removeJobId(ObjectId jobId) {
        if (this.jobIds != null) {
            this.jobIds.remove(jobId);
        }
    }
}
