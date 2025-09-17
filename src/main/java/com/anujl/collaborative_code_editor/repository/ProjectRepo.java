package com.anujl.collaborative_code_editor.repository;

import com.anujl.collaborative_code_editor.entity.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProjectRepo extends MongoRepository<ProjectEntity,String> {
    Optional<ProjectEntity> findByShareId(String shareId);
}
