package com.anujl.collaborative_code_editor.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProjectDTO {

    private String id;
    private String name;
    private String language;
    private  String author;
    private List<String> codeContent;

    @Builder.Default
    private boolean inUse=false;

    @Override
   public String toString() {
        return "ProjectDTO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", codeContent=" + codeContent + " "+inUse+
                '}';
    }

}
