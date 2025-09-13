package com.anujl.collaborative_code_editor.repository;

import com.anujl.collaborative_code_editor.entity.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectRepo extends MongoRepository<ProjectEntity,String> {
}
