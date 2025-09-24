package com.anujl.collaborative_code_editor.controller;


import com.anujl.collaborative_code_editor.dto.wc.CodeChange;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@RestController
public class CodeEditorController {


        @MessageMapping("/project/{projectId}/code")
        @SendTo("/topic/project/{projectId}/code")
        public List<CodeChange> handleCodeChange(@DestinationVariable String projectId,
                                                 @Payload List<CodeChange> change) {
if(change.get(0).getStartLine()<0){
    //to handle new user login
    return List.of(new CodeChange(-1));
}
//            projectCode.putIfAbsent(projectId, new StringBuffer());
//            StringBuffer code = projectCode.get(projectId);
//
//            applyChange(code, change);
            System.out.println( "changes:"+change);
            System.out.println(change.get(0).getUserName());

            return change; // broadcast updated code
        }

    }
