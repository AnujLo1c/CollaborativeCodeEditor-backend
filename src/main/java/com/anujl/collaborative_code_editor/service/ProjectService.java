package com.anujl.collaborative_code_editor.service;

import com.anujl.collaborative_code_editor.dto.ProjectDTO;
import com.anujl.collaborative_code_editor.entity.ProjectEntity;
import com.anujl.collaborative_code_editor.repository.ProjectRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ProjectService {

    @Autowired
    ProjectRepo projectRepo;
@Autowired
UserService userService;

    @Autowired
    ModelMapper modelMapper;
    public String save(ProjectDTO p) {
        System.out.println(p.toString());
        ProjectEntity projectEntity=projectRepo.save(modelMapper.map(p, ProjectEntity.class));
userService.addProjectToUsername(projectEntity.getId(),p.getAuthor());
        return  "Project saved successfully";
    }

    public Optional<ProjectDTO> findById(String id) {
        return projectRepo.findById(id)
                .map(project -> modelMapper.map(project, ProjectDTO.class));
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
}
