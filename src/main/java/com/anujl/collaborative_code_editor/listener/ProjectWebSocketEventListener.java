package com.anujl.collaborative_code_editor.listener;

import com.anujl.collaborative_code_editor.service.ProjectUserRegistry;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProjectWebSocketEventListener {

    private final ProjectUserRegistry registry;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Object> sessionLocks = new ConcurrentHashMap<>();


    public ProjectWebSocketEventListener(ProjectUserRegistry registry, SimpMessagingTemplate messagingTemplate) {
        this.registry = registry;
        this.messagingTemplate = messagingTemplate;
    }

    private Object getSessionLock(String sessionId) {
        return sessionLocks.computeIfAbsent(sessionId, k -> new Object());
    }

    private void cleanupSessionLock(String sessionId) {
        sessionLocks.remove(sessionId);
    }
    @EventListener
    public void handleConnect(SessionConnectEvent event) {  // ← Changed to SessionConnectEvent
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = accessor.getFirstNativeHeader("username");
        String projectId = accessor.getFirstNativeHeader("projectId");
        String sessionId = accessor.getSessionId();

        System.out.println("🎯 ===== WEB SOCKET CONNECTING 1=====");
        System.out.println("🎯 Session ID: " + sessionId);
        System.out.println("🎯 Username: " + username);
        System.out.println("🎯 Project ID: " + projectId);
        System.out.println("🎯 Native Headers: " + accessor.getMessage());
        System.out.println("🎯 =================================");

        if (username != null && projectId != null && sessionId != null) {
            registry.addUserToProject(projectId, username, sessionId);



            System.out.println("✅ User added to registry: " + username);


            System.out.println("📊 Current sessions: " + registry.getSessionMap());
        } else {
            System.out.println("❌ Missing connection parameters - Username: " + username + ", ProjectId: " + projectId);
        }
    }
    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String sessionId = accessor.getSessionId();
        String username = registry.getUsernameBySessionId(sessionId);
        String projectId = registry.getProjectIdBySessionId(sessionId);
        System.out.println("🎯 ===== WEB SOCKET CONNECTED =====");
        System.out.println(accessor);
        System.out.println("🎯 Session ID: " + sessionId);
        System.out.println("🎯 Username: " + username);
        System.out.println("🎯 Project ID: " + projectId);
        System.out.println("🎯 =================================");


        if (username != null && projectId != null && sessionId != null) {

            if(!registry.isProjectActive(projectId)){
                //TODO:: project missing
                String projectCode;
                registry.addProjectToProjectId("project",projectId);
            }
            else{
                String projectCode= registry.getProjectByProjectId(projectId);

                messagingTemplate.convertAndSend(
                        "/topic/project/" + projectId + "/code",
                        Map.of("username", username,"projectCode",projectCode)
                );
            }

            // Debug: Show current sessions
            System.out.println("📊 Current sessions: " + registry.getSessionMap());
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        System.out.println("❌ ===== WEB SOCKET DISCONNECTED =====");
        System.out.println("❌ Session ID: " + sessionId);
        System.out.println("❌ ==================================");


        String username = registry.getUsernameBySessionId(sessionId);
        String projectId = registry.getProjectIdBySessionId(sessionId);

        System.out.println("📋 From Registry - Username: " + username + ", ProjectId: " + projectId);

        if (username != null && projectId != null) {
            registry.removeUserBySessionId(sessionId);
            if(!registry.isProjectActiveForDisconnect(projectId)){
                registry.removeProjectToProjectId(projectId);
            }
            System.out.println("✅ User removed via session: " + username + " from project: " + projectId);

        } else {
            System.out.println("⚠️  No user found for session: " + sessionId);
        }

        System.out.println("📊 Remaining sessions: " + registry.getSessionMap());
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        System.out.println("🔗 SessionConnectEvent fired - Initial handshake");
    }

    @EventListener
    public void handleWebSocketSubscribeEvent(SessionSubscribeEvent event) {
        String destination = (String) event.getMessage().getHeaders().get("simpDestination");
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        System.out.println("📥 Subscribe - Session: " + sessionId + ", To: " + destination);
    }

    @EventListener
    public void handleWebSocketUnsubscribeEvent(SessionUnsubscribeEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        System.out.println("📤 Unsubscribe - Session: " + sessionId);
    }

    // Optional: Handle connection errors
    @EventListener
    public void handleTransportErrorEvent(SessionConnectEvent event) {
        System.out.println("🚨 Transport error occurred");
    }
}