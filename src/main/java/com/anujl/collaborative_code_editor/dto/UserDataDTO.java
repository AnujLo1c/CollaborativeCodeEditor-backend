package com.anujl.collaborative_code_editor.dto;

import lombok.Data;

import java.util.ArrayList;


@Data
public class UserDataDTO {
    private ArrayList<String> projects;
    private ArrayList<String> ref_projects;

    public UserDataDTO(ArrayList<String> projects, ArrayList<String> ref_projects) {
        this.projects = projects;
        this.ref_projects= ref_projects;
    }
}