package com.anujl.collaborative_code_editor.controller;

import com.anujl.collaborative_code_editor.dto.CodeRequest;
import com.anujl.collaborative_code_editor.dto.ProjectDTO;
import com.anujl.collaborative_code_editor.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;


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
        return projectService.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

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
        public ResponseEntity<Map<String,String>> executeCode(@RequestBody CodeRequest request) {
            String language = request.getLanguage().toLowerCase();
            String code = request.getCode();

            try {
                Path tempDir = Files.createTempDirectory("codeExec");
                Path tempFile;
                ProcessBuilder processBuilder;
Map<String,String> map=new HashMap<>();
                switch (language) {
                    case "java":
                        tempFile = tempDir.resolve("Main.java");
                        Files.write(tempFile, code.getBytes());


                        processBuilder = new ProcessBuilder(
                                "docker", "run", "--rm",
                                "-v", tempDir.toAbsolutePath() + ":/app",
                                "openjdk:17",
                                "bash", "-c", "javac /app/Main.java && java -cp /app Main"
                        );
                        break;

                    case "python":
                        tempFile = tempDir.resolve("script.py");
                        Files.write(tempFile, code.getBytes());


                        processBuilder = new ProcessBuilder(
                                "docker", "run", "--rm",
                                "-v", tempDir.toAbsolutePath() + ":/app",
                                "python:3.10",
                                "python3", "/app/script.py"
                        );
                        break;

                    case "c":
                        tempFile = tempDir.resolve("program.c");
                        Files.write(tempFile, code.getBytes());


                        processBuilder = new ProcessBuilder(
                                "docker", "run", "--rm",
                                "-v", tempDir.toAbsolutePath() + ":/app",
                                "gcc:latest",
                                "bash", "-c", "gcc /app/program.c -o /app/program && /app/program"
                        );
                        break;

                    case "cpp":
                    case "c++":
                        tempFile = tempDir.resolve("program.cpp");
                        Files.write(tempFile, code.getBytes());


                        processBuilder = new ProcessBuilder(
                                "docker", "run", "--rm",
                                "-v", tempDir.toAbsolutePath() + ":/app",
                                "gcc:latest",
                                "bash", "-c", "g++ /app/program.cpp -o /app/program && /app/program"
                        );
                        break;

                    default:
                        map.put("output","Unsupported language: " + language);
                        return ResponseEntity.badRequest().body(map);
                }

                Process runProcess = processBuilder.start();
                String output = new String(runProcess.getInputStream().readAllBytes());
                String errorOutput = new String(runProcess.getErrorStream().readAllBytes());

                int exitCode = runProcess.waitFor();

                if (exitCode != 0) {
                    return ResponseEntity.badRequest().body(Map.of("output","Execution Error:\n" + errorOutput));
                }
map.put("output",output);
                return ResponseEntity.ok(map);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("output","Execution failed: " + e.getMessage()));
            }
        }


}
