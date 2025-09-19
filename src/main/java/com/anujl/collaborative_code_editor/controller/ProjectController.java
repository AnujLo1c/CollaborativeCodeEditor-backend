package com.anujl.collaborative_code_editor.controller;


import com.anujl.collaborative_code_editor.dto.CodeRequest;
import com.anujl.collaborative_code_editor.dto.ProjectDTO;
import com.anujl.collaborative_code_editor.dto.ProjectShareDTO;
import com.anujl.collaborative_code_editor.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;


@CrossOrigin(origins = "http://localhost:4200/")
@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody ProjectDTO p) {
        System.out.println("service called"+p);
        return ResponseEntity.ok(projectService.save(p));
    }

    @GetMapping("/{id}")
//    public Map<String, Object> get(@PathVariable String id) {
    public ProjectDTO get(@PathVariable String id) {
        return projectService.findById(id).orElseThrow(() -> new RuntimeException("Project not found"))
                ;

    //TODO:: make inuse true
//        return Map.of(
//                "id", "123",
//                "name", "Anonymous Project",
//                "code", "print('hello world')",
//                "inUse", false
//        );


    }


    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @RequestBody ProjectDTO updated) {

        return ResponseEntity
                .ok(projectService.update(id,updated));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        projectService.deleteById(id);
    }

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeCode(@RequestBody CodeRequest request) {

        return projectService.executeCode(request);
    }

    //shareable

    @PostMapping("/share/{shareId}")
    public ResponseEntity<Void> saveRefProject(@PathVariable String shareId,@RequestParam String username) {
        projectService.saveRefProject(shareId,username);
        System.out.println("Requested by: " + username);

        return ResponseEntity.ok().build();
    }
    @PostMapping("/{projectId}/share")
    public  ResponseEntity<Object> generateShareableLink(@PathVariable  String projectId){
        String link=projectService.generateShareableLink(projectId);

        return ResponseEntity.ok(Map.of("link",link));
    }

}
