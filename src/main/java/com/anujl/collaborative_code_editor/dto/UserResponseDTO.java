package com.anujl.collaborative_code_editor.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@AllArgsConstructor

public class UserResponseDTO {
    private String username;

    @Email
    private  String email;
    private LocalDateTime lastLoginTime;
}
