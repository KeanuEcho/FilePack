package com.belongme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.ServletContext;

@SpringBootApplication
public class FilePackApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(FilePackApplication.class, args);

	}

}
