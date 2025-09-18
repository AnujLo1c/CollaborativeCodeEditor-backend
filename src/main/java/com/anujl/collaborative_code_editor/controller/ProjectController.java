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
    public ResponseEntity<Map<String, String>> executeCode(@RequestBody CodeRequest request) {
        String language = request.getLanguage().toLowerCase();
        String code = request.getCode();
        String input = request.getInput();

        ProcessBuilder processBuilder;
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("codeExec");
            Path tempFile;

            switch (language) {
                case "java":
                    tempFile = tempDir.resolve("Main.java");
                    Files.write(tempFile, code.getBytes());
                    processBuilder = new ProcessBuilder(
                            "docker", "run", "--rm",
                            "-i", // important: interactive mode for stdin
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
                            "-i", // interactive for stdin
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
                            "-i",
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
                            "-i",
                            "-v", tempDir.toAbsolutePath() + ":/app",
                            "gcc:latest",
                            "bash", "-c", "g++ /app/program.cpp -o /app/program && /app/program"
                    );
                    break;
                default:
                    return ResponseEntity.badRequest().body(Map.of("output", "Unsupported language: " + language));
            }

            Process process = processBuilder.start();

            // Pass input properly using a separate thread to avoid blocking
            Thread inputThread = new Thread(() -> {
                try (OutputStream os = process.getOutputStream()) {
                    if (input != null && !input.isEmpty()) {
                        os.write(input.getBytes());
                        os.write("\n".getBytes());
                        os.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            String output = new String(process.getInputStream().readAllBytes());
            String errorOutput = new String(process.getErrorStream().readAllBytes());

            int exitCode = process.waitFor();
            inputThread.join();

            if (exitCode != 0) {
                return ResponseEntity.badRequest().body(Map.of("output", "Execution Error:\n" + errorOutput));
            }
            return ResponseEntity.ok(Map.of("output", output));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("output", "Execution failed: " + e.getMessage()));
        } finally {
            if (tempDir != null) {
                try (Stream<Path> walk = Files.walk(tempDir)) {
                    walk.sorted((a, b) -> b.compareTo(a))
                            .forEach(path -> {
                                try {
                                    Files.delete(path);
                                } catch (Exception ignored) {}
                            });
                } catch (Exception ignored) {}
            }
        }
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
