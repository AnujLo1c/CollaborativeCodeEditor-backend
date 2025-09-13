package com.anujl.collaborative_code_editor.dto.wc;

import lombok.Data;

@Data
 public class CodeChange {
    private int position;
    private String insertedText;
    private String removedText;
}
