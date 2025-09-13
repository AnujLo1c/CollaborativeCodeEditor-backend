package com.anujl.collaborative_code_editor;

//import com.anujl.collaborative_code_editor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Base64;

@SpringBootApplication( exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
})
@EnableMongoAuditing
public class CollaborativeCodeEditorApplication  {
@Autowired
	public static void main(String[] args) {

		SpringApplication.run(CollaborativeCodeEditorApplication.class, args);
	}


}
