package com.anujl.collaborative_code_editor.repository;

import com.anujl.collaborative_code_editor.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);

}

