package com.anujl.collaborative_code_editor.service;


import com.anujl.collaborative_code_editor.dto.ProjectDTO;
import com.anujl.collaborative_code_editor.entity.ProjectEntity;
import com.anujl.collaborative_code_editor.repository.ProjectRepo;
import com.mongodb.DuplicateKeyException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;


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

}
