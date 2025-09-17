package com.anujl.collaborative_code_editor.entity;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;


@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "usersDetails")

@Document(collection = "users")
public class UserEntity implements UserDetails {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotBlank
    @Indexed(unique = true)
    private String username;

    @Email  @NotBlank
    private  String email;
    @NotBlank
    private String password;
    private List<String> projects= new ArrayList<>();

    private Set<String> ref_projects= new HashSet<>();

    @Override
    public String toString() {
        StringBuilder sb= new StringBuilder();
        sb.append("Projects: ");
        for(String p:projects){
            sb.append(p).append(", ");
        }
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                "email='" + email + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                '}'+sb.toString();
    }

    private LocalDateTime lastLoginTime;
//    List<GrantedAuthority> authorities=new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(Collections.singleton(new SimpleGrantedAuthority("USER")));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public Set<String> getRefProjects() {
        return ref_projects;
    }

    public void setRefProjects(Set<String> ref_projects) {
        this.ref_projects = ref_projects;
    }
}
