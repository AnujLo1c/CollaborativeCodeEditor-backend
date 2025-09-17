package com.anujl.collaborative_code_editor.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
public class ProjectShareDTO {
    private String shareId;
    private LocalDateTime shareLinkExpiry;
}
