package com.anujl.collaborative_code_editor.service;

import com.anujl.collaborative_code_editor.entity.UserEntity;
import com.anujl.collaborative_code_editor.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> Optuser=userRepo.findByUsername(username);
        if(Optuser.isPresent()){
            UserEntity userEntity =Optuser.get();
            System.out.println("login");
return userEntity;
        }else
throw new UsernameNotFoundException("Username not present");
    }
}
