package com.anujl.collaborative_code_editor;

import com.anujl.collaborative_code_editor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Base64;

@SpringBootApplication( exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
public class CollaborativeCodeEditorApplication implements CommandLineRunner {
@Autowired
	UserService userService;
	public static void main(String[] args) {

		SpringApplication.run(CollaborativeCodeEditorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

//		KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
//		keyGen.init(256);
//		SecretKey key = keyGen.generateKey();
//		System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));



	}
}
