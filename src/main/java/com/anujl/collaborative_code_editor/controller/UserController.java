package com.anujl.collaborative_code_editor.controller;

import com.anujl.collaborative_code_editor.dto.UserDataDTO;
import com.anujl.collaborative_code_editor.dto.UserLoginDto;
import com.anujl.collaborative_code_editor.dto.UserMainDTO;
import com.anujl.collaborative_code_editor.dto.UserResponseDTO;
import com.anujl.collaborative_code_editor.entity.UserEntity;
import com.anujl.collaborative_code_editor.service.UserService;

import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    UserService userService;
//Todo: add request respose dto
    @PostMapping("/register")
    public String saveUser(@RequestBody UserEntity userEntity){

        userService.saveUser(userEntity);
        return "saved";
    }
    @PostMapping("/login")
    public String loginUser(@RequestBody UserLoginDto userLoginDto){
        System.out.println("Login attempt for user: " + userLoginDto.getUsername());
        return userService.verify(userLoginDto);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@RequestBody Map<String,String> body){
        String token = body.get("token").trim();

        userService.logout(token.replace(" ",""));
        return ResponseEntity.ok("Success" );
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> allUsers(){
        System.out.println("Fetching all users");
       return ResponseEntity.ok(userService.getAllUsers());
    }
    @PostMapping("/add-project")
    public ResponseEntity<Void> addProject(@NotBlank String projectId,@NotBlank String username){
        userService.addProjectToUsername(projectId,username);
        return ResponseEntity.ok(
                ).build();
    }
    @PostMapping("/add-ref-project")
    public ResponseEntity<Void> addRefProject(@NotBlank String projectId,@NotBlank String username){
        userService.addRefProjectToUsername(projectId,username);
        return ResponseEntity.ok(
        ).build();
    }
    @GetMapping("/fetch-projects")
    public ResponseEntity<Map<String, ArrayList<String>>> fetchProjects(@NotBlank String username){

        return  ResponseEntity.ok(Map.of("projects", userService.fetchProjectByUsername(username)));
    }
    @GetMapping("/fetch-ref-projects")
    public ResponseEntity<Map<String, ArrayList<String>>> fetchRefProjects(@NotBlank String username){

        return  ResponseEntity.ok(Map.of("refProjects", userService.fetchRefProjectByUsername(username)));
    }
    @GetMapping("/fetch-all-projects")
    public ResponseEntity<Map<String, UserDataDTO>> fetchAllProjects(@NotBlank String username){

        return  ResponseEntity.ok(Map.of("allProjects", userService.fetchProjectRefProjectByUsername(username)));
    }
}
