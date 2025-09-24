package com.anujl.collaborative_code_editor.listener;

import com.anujl.collaborative_code_editor.service.ProjectUserRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class ProjectWebSocketEventListener {

    private final ProjectUserRegistry registry;
    private final SimpMessagingTemplate messagingTemplate;

    public ProjectWebSocketEventListener(ProjectUserRegistry registry, SimpMessagingTemplate messagingTemplate) {
        this.registry = registry;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getFirstNativeHeader("username");
        String projectId = accessor.getFirstNativeHeader("projectId");

        System.out.println(accessor);
        System.out.println(username);
        System.out.println(projectId);
        if (username != null && projectId != null) {
            registry.addUserToProject(projectId, username);
            System.out.println("added");
            // Broadcast updated user list
            messagingTemplate.convertAndSend(
                    "/topic/project/" + projectId + "/code",
                    registry.getAProjectUser(projectId)
            );
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getFirstNativeHeader("username");
        String projectId = accessor.getFirstNativeHeader("projectId");

        if (username != null && projectId != null) {
            registry.removeUserFromProject(projectId, username);

            // Broadcast updated user list
//            messagingTemplate.convertAndSend(
//                    "/topic/project/" + projectId + "/users",
//                    registry.getProjectUsers(projectId)
//            );
        }
    }
}
