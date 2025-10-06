package com.anujl.collaborative_code_editor.service;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;




@Component
public class ProjectUserRegistry {
    private final Map<String, Set<String>> projectUsers = new ConcurrentHashMap<>();
    private final Map<String, UserSession> sessionToUserMap = new ConcurrentHashMap<>();
    private final Map<String, String> projectIdToProjectMap = new ConcurrentHashMap<>();

    public void addProjectToProjectId(String project,String projectId){
        projectIdToProjectMap.put(projectId,project);
    }
    public void removeProjectToProjectId(String projectId){
        projectIdToProjectMap.remove(projectId);
    }

public  boolean isProjectActive(String projectId){
        return projectIdToProjectMap.containsKey((projectId));
}

    public void addUserToProject(String projectId, String username, String sessionId) {
        // Store in project users map
        projectUsers.computeIfAbsent(projectId, k -> ConcurrentHashMap.newKeySet()).add(username);

        // Store session mapping for disconnect handling
        sessionToUserMap.put(sessionId, new UserSession(username, projectId));

        System.out.println("✅ Added user to registry - Session: " + sessionId +
                ", User: " + username + ", Project: " + projectId);
    }

    public void removeUserFromProject(String projectId, String username, String sessionId) {
        // Remove from project users
        Set<String> users = projectUsers.get(projectId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                projectUsers.remove(projectId);
            }
        }

        // Remove from session mapping
        sessionToUserMap.remove(sessionId);

        System.out.println("✅ Removed user from registry - Session: " + sessionId +
                ", User: " + username + ", Project: " + projectId);
    }
public boolean isProjectActiveForDisconnect(String projectId){
if(projectUsers.getOrDefault(projectId,new HashSet<>()).isEmpty()){
    projectUsers.remove(projectId);
    return false;
}
return  true;
}


    public void removeUserBySessionId(String sessionId) {
        UserSession userSession = sessionToUserMap.get(sessionId);
        if (userSession != null) {
            removeUserFromProject(userSession.getProjectId(), userSession.getUsername(), sessionId);
            sessionToUserMap.remove(sessionId);
        }
    }

    public Set<String> getProjectUsers(String projectId) {
        return projectUsers.getOrDefault(projectId, Collections.emptySet());
    }

    public String getAProjectUser(String projectId, String username) {
        Set<String> users = projectUsers.getOrDefault(projectId, Collections.emptySet());
        if (users.contains(username)) {
            return username;
        }
        return null;
    }

    public String getUsernameBySessionId(String sessionId) {
        UserSession userSession = sessionToUserMap.get(sessionId);
        System.out.println("Get username "+userSession);
        return userSession != null ? userSession.getUsername() : null;
    }

    public String getProjectIdBySessionId(String sessionId) {
        UserSession userSession = sessionToUserMap.get(sessionId);
        System.out.println("Get project "+userSession);
        return userSession != null ? userSession.getProjectId() : null;
    }

    // Helper method to get all sessions for debugging
    public Map<String, UserSession> getSessionMap() {
        return new HashMap<>(sessionToUserMap);
    }

    public String getProjectByProjectId(String projectId) {
        return projectIdToProjectMap.getOrDefault(projectId,"Nill");
    }

    private static class UserSession {
        private final String username;
        private final String projectId;

        public UserSession(String username, String projectId) {
            this.username = username;
            this.projectId = projectId;
        }

        public String getUsername() { return username; }
        public String getProjectId() { return projectId; }

        @Override
        public String toString() {
            return "UserSession{username='" + username + "', projectId='" + projectId + "'}";
        }
    }
}