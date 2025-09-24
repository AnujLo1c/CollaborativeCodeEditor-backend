package com.anujl.collaborative_code_editor.service;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProjectUserRegistry {
    private final Map<String, Set<String>> projectUsers = new ConcurrentHashMap<>();

    public void addUserToProject(String projectId, String username) {
        projectUsers.computeIfAbsent(projectId, k -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public void removeUserFromProject(String projectId, String username) {
        projectUsers.getOrDefault(projectId, Collections.emptySet()).remove(username);
    }

    public Set<String> getProjectUsers(String projectId) {
        return projectUsers.getOrDefault(projectId, Collections.emptySet());
    }
    public String getAProjectUser(String projectId) {
        Set<String> users= projectUsers.getOrDefault(projectId, Collections.emptySet());
        if(!users.isEmpty())
        return users.toArray(new String[0])[0];

        return "empty user list";
    }
}
