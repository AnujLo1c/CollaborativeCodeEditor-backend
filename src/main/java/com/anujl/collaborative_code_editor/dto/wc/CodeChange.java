package com.anujl.collaborative_code_editor.dto.wc;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeChange {

public CodeChange(Integer temp){
    startLine=temp;
}
    private Integer startLine;
    private Integer startColumn;

    // Position where the change ends (for deletion/replacement)
    private Integer endLine;
    private Integer endColumn;

    // Type of change: INSERT, DELETE, REPLACE, CURSOR_MOVE, SELECTION_CHANGE, etc.
    private ChangeType changeType;

    // Content inserted (if any)
    private String insertedText;

    // Content removed (if any)
    private String removedText;

    // Timestamp of the change (optional, useful for ordering changes in collab editing)
    private Long timestamp;

    private  String userName;
    // Enum to make the change type explicit
    public enum ChangeType {
        INSERT,
        DELETE,
        REPLACE,
        CURSOR_MOVE,

    }
}
