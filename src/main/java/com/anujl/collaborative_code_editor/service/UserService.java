package com.anujl.collaborative_code_editor.service;

import com.anujl.collaborative_code_editor.dto.UserDataDTO;
import com.anujl.collaborative_code_editor.dto.UserLoginDto;
import com.anujl.collaborative_code_editor.dto.UserResponseDTO;
import com.anujl.collaborative_code_editor.entity.UserEntity;
import com.anujl.collaborative_code_editor.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
@Autowired
    PasswordEncoder passwordEncoder;
@Autowired
    AuthenticationManager authenticationManager;
@Autowired
JwtService jwtService;

@Autowired
    ModelMapper modelMapper;

@Transactional
    public void saveUser(UserEntity userEntity){
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
    if (userRepo.findByUsername(userEntity.getUsername()).isPresent()) {
        throw new RuntimeException("Username already exists!");
    }
    userEntity.setLastLoginTime(LocalDateTime.now());
        userRepo.save(userEntity);
    }

    public List<UserResponseDTO> getAllUsers() {
return userRepo.findAll().stream().map(m->new UserResponseDTO(m.getUsername(),m.getEmail(),m.getLastLoginTime())).toList();
    }


    public String verify(UserLoginDto userLoginDto)  {
        Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLoginDto.getUsername(), userLoginDto.getPassword()));
  if(authentication.isAuthenticated()){
      String token= jwtService.generateToken(userLoginDto.getUsername());
UserEntity userEntity= userRepo.findByUsername(userLoginDto.getUsername())
              .orElseThrow(() -> new RuntimeException("User not found"));
if(userEntity==null){
    throw new RuntimeException("User not found");
}
      userEntity.setLastLoginTime(LocalDateTime.now());
      userRepo.save(userEntity);
      return token;
  }
  else{
      return "Invalid";
  }
    }

    public void logout(String token) {
userRepo.findByUsername(jwtService.extractUsername(token))
        .ifPresent(user -> {

            System.out.println("User " + user.getUsername() + " logged out.");
        user.setLastLoginTime(LocalDateTime.now());
        userRepo.save(user);
        });
    }

    public void addProjectToUsername(String projectId, String username) {
        UserEntity userEntity= userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Exists"));
userEntity.getProjects().add(projectId);
        System.out.println(userEntity);
userRepo.save(userEntity);
    }
    public void addRefProjectToUsername(String projectId, String username) {
        UserEntity userEntity= userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Exists"));
        userEntity.getRef_projects()
                .add(projectId);
        userRepo.save(userEntity);
    }
    public ArrayList<String> fetchProjectByUsername(String username){
    UserEntity userEntity=userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username Not Exists"));
    return userEntity.getProjects();
    }
    public ArrayList<String> fetchRefProjectByUsername(String username){
        UserEntity userEntity=userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Exists"));
        return userEntity.getRef_projects();
    }

    public UserDataDTO fetchProjectRefProjectByUsername(String username){
        UserEntity userEntity=userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username Not Exists"));
        return new UserDataDTO(userEntity.getProjects(),userEntity.getRef_projects());
    }
}
