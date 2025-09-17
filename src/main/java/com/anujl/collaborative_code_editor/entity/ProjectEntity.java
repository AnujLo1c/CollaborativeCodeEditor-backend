package com.anujl.collaborative_code_editor.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "projects")
@CompoundIndex(def = "{'author': 1, 'name': 1}", unique = true)
public class ProjectEntity {

    @Id
    private String id;

    private String name;
    private String language;
    private String author;

    private List<String> codeContent= new ArrayList<>();;
    private boolean inUse;
private String shareId;
@CreatedDate
private LocalDateTime shareLinkExpiry;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
