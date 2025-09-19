package com.anujl.collaborative_code_editor.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.anujl.collaborative_code_editor.dto.CodeRequest;
import com.anujl.collaborative_code_editor.dto.ProjectDTO;
import com.anujl.collaborative_code_editor.entity.ProjectEntity;
import com.anujl.collaborative_code_editor.repository.ProjectRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;

import com.mongodb.DuplicateKeyException;
import lombok.Data;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.asynchttpclient.Dsl.asyncHttpClient;


@Service
public class ProjectService {

    @Autowired
    ProjectRepo projectRepo;
@Autowired
UserService userService;

    @Autowired
    ModelMapper modelMapper;
    public String save(ProjectDTO p) {
        try {
            p.setCodeContent(Arrays.asList("line 1", "line 2", "line 3"));

            ProjectEntity projectEntity=projectRepo.save(modelMapper.map(p, ProjectEntity.class));

            userService.addProjectToUsername(projectEntity.getId(),p.getAuthor());
            System.out.println("Project saved with ID: " + projectEntity.getId()+ "to user: "+p.getAuthor());

            projectRepo.save(modelMapper.map(p,ProjectEntity.class));

        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Project with the same name already exists for this user.");

        }

        return  "Project saved successfully";
    }

    public Optional<ProjectDTO> findById(String id) {
        ProjectEntity project = projectRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        project.setInUse(true);
        projectRepo.save(project);

        ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
        return Optional.of(dto);
    }



    public void deleteById(String id) {
        if (projectRepo.existsById(id)) {
            projectRepo.deleteById(id);
        } else {
            throw new RuntimeException("Project not found with id: " + id);
        }
    }

    //TODO: update needs to be modified
    public String update(String id, ProjectDTO updated) {
        Optional<ProjectEntity> optionalProject = projectRepo.findById(id);
        if (optionalProject.isPresent()) {
            ProjectEntity existingProject = optionalProject.get();
            existingProject.setName(updated.getName());
            existingProject.setLanguage(updated.getLanguage());
            existingProject.setCodeContent(updated.getCodeContent());
            projectRepo.save(existingProject);
            return "Project updated successfully";
        } else {
            throw new RuntimeException("Project not found with id: " + id);
        }
    }

    //shareable
    public String generateShareableLink(String projectId) {
        ProjectEntity project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (project.getShareId() == null ||project.getShareId().isEmpty()) {
            project.setShareId(UUID.randomUUID().toString());
            project.setShareLinkExpiry(LocalDateTime.now().plusHours(1));
            projectRepo.save(project);
        }

        String baseUrl = "http://localhost:4200/login?shareId=";
        https://yourapp.com/login?shareId=abc123

        return baseUrl + project.getShareId();
    }


    public void saveRefProject(String shareId,String username) {
        Optional<ProjectEntity> project = projectRepo.findByShareId(shareId);
        System.out.println("aefa");
if(project.isPresent()){
    ProjectEntity projectEntity= project.get();
if(projectEntity.getShareLinkExpiry().isBefore(LocalDateTime.now())){
    System.out.println("Expiry"+ projectEntity.getShareLinkExpiry()+"  now: "+LocalDateTime.now());
    throw new RuntimeException("Invalid or expired share link");
}
else{


    userService.addRefProjectToUsername(projectEntity.getId(),username);
    System.out.println("Reference project added to user: "+username+" project id: "+projectEntity.getId());
}

}
else{
    throw new RuntimeException("Invalid or expired share link");
}

    }

//    private final String JUDGE0_URL = "https://ce.judge0.com/submissions/?base64_encoded=false&wait=true";
//    @PostMapping("/execute")
//    public Map<String,String> executeCode(CodeRequest request) {
//
//
////        @Data
////        public class CodeRequest {
////            private String language;
////            private String code;
////            private String input;
////        }
//
//        RestTemplate restTemplate = new RestTemplate();
//
//            // Map language name to Judge0 language ID
//            Map<String, Integer> languageMap = new HashMap<>();
//            languageMap.put("java", 62); // Java 17
//
//            // Prepare request body
//            Map<String, Object> body = new HashMap<>();
//            body.put("language_id", languageMap.getOrDefault(request.getLanguage().toLowerCase(), 62));
//            body.put("source_code", request.getCode());
//            body.put("stdin", request.getInput());  // send user input here
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//            try {
//                ResponseEntity<Map> response = restTemplate.postForEntity(JUDGE0_URL, entity, Map.class);
//                return ResponseEntity.ok(response.getBody()).getBody();
//            } catch (Exception e) {
//                Map<String, String> error = new HashMap<>();
//                error.put("error", e.getMessage());
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Map) error).getBody();
//            }
//        }


    private static final String RAPIDAPI_KEY = "61ea511999mshad44bf5c7e844a3p18acd6jsn42c3b593da9a";
    private static final String RAPIDAPI_HOST = "judge0-ce.p.rapidapi.com";

    private static final Map<String, Integer> LANGUAGE_MAP = new HashMap<>() {{
        put("java", 62); // Java 17
        put("python", 71); // Python 3
        put("c", 50); // C
        put("cpp", 54); // C++
    }};

    @PostMapping("/execute")
    public ResponseEntity<Map<String, Object>> executeCode(@RequestBody CodeRequest request) {
        Map<String, Object> resultMap = new HashMap<>();

        try (AsyncHttpClient client = asyncHttpClient()) {

            String code = request.getCode();
            String input = request.getInput() != null ? request.getInput() : "";
            int languageId = LANGUAGE_MAP.getOrDefault(request.getLanguage().toLowerCase(), 62);

            // Base64 encode code and input using UTF-8
            String encodedCode = Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8));
            String encodedInput = Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));

            // Create JSON body safely
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("language_id", languageId);
            bodyMap.put("source_code", encodedCode);
            bodyMap.put("stdin", encodedInput);

            ObjectMapper mapper = new ObjectMapper();
            String postBody = mapper.writeValueAsString(bodyMap);

            // 1️⃣ Submit code asynchronously
            CompletableFuture<Response> postFuture = client.prepare("POST",
                            "https://judge0-ce.p.rapidapi.com/submissions?base64_encoded=true&wait=false&fields=*")
                    .setHeader("x-rapidapi-key", RAPIDAPI_KEY)
                    .setHeader("x-rapidapi-host", RAPIDAPI_HOST)
                    .setHeader("Content-Type", "application/json")
                    .setBody(postBody)
                    .execute()
                    .toCompletableFuture();

            Response postResponse = postFuture.get(10, TimeUnit.SECONDS);
            String responseBody = postResponse.getResponseBody();

            // Parse token using JSON instead of regex
            JsonNode tokenNode = mapper.readTree(responseBody).get("token");
            if (tokenNode == null) {
                throw new RuntimeException("No token returned from Judge0: " + responseBody);
            }
            String token = tokenNode.asText();

            // 2️⃣ Poll until result is ready
            boolean done = false;
            while (!done) {
                CompletableFuture<Response> getFuture = client.prepare("GET",
                                "https://judge0-ce.p.rapidapi.com/submissions/" + token + "?base64_encoded=true&fields=*")
                        .setHeader("x-rapidapi-key", RAPIDAPI_KEY)
                        .setHeader("x-rapidapi-host", RAPIDAPI_HOST)
                        .execute()
                        .toCompletableFuture();

                Response getResponse = getFuture.get(5, TimeUnit.SECONDS);
                String body = getResponse.getResponseBody();

                JsonNode rootNode = mapper.readTree(body);
                int statusId = rootNode.path("status").path("id").asInt();

                // Status IDs 3=Accepted, 4=Compilation Error, 5=Runtime Error
                if (statusId == 3 || statusId == 4 || statusId == 5) {

                    // Decode stdout and stderr safely
                    String stdout = "";
                    String stderr = "";

                    JsonNode stdoutNode = rootNode.get("stdout");
                    JsonNode stderrNode = rootNode.get("stderr");

                    if (stdoutNode != null && !stdoutNode.isNull()) {
                        stdout = new String(Base64.getDecoder().decode(stdoutNode.asText()), StandardCharsets.UTF_8);
                    }
                    if (stderrNode != null && !stderrNode.isNull()) {
                        stderr = new String(Base64.getDecoder().decode(stderrNode.asText()), StandardCharsets.UTF_8);
                    }

                    resultMap.put("stdout", stdout);
                    resultMap.put("stderr", stderr);
                    resultMap.put("status", rootNode.path("status").path("description").asText());
                    done = true;
                } else {
                    TimeUnit.SECONDS.sleep(1);
                }
            }

            return ResponseEntity.ok(resultMap);

        } catch (Exception e) {
            e.printStackTrace(); // log the error
            resultMap.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(resultMap);
        }
    }

}

