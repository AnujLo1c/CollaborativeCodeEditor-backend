package com.anujl.collaborative_code_editor.controller;

import java.util.List;

import com.anujl.collaborative_code_editor.dto.wc.CodeChange;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CodeEditorController {


    @MessageMapping("/project/{projectId}/code")
    @SendTo("/topic/project/{projectId}/code")
//    public String handleCodeChange(@Payload List<CodeChange> changes) {
        public String handleCodeChange(@Payload String s) {

//            for(CodeChange change:changes){
//        if (!change.getRemovedText().isEmpty()) {
//            sharedCode.delete(change.getPosition(),
//                              change.getPosition() + change.getRemovedText().length());
//        }
//        if (!change.getInsertedText().isEmpty()) {
//            sharedCode.insert(change.getPosition(), change.getInsertedText());
//        }
//    }
//        return sharedCode.toString();
        return s+"Anuj";
    }
}
