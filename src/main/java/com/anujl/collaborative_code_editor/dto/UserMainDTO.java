package com.anujl.collaborative_code_editor.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMainDTO {
    private String username;

    @Email
    private  String email;
    private LocalDateTime lastLoginTime;
    private ArrayList<String> projects=new ArrayList<>();
    private ArrayList<String> ref_projects=new ArrayList<>();
}
