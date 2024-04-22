package com.rest.pruebarest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.rest.pruebarest.controllers.CheckerHelper;
import com.rest.pruebarest.controllers.ImageHelper;
import com.rest.pruebarest.controllers.JWTHelper;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.UserRepo;

@SpringBootApplication
public class PruebarestApplication {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	public static void main(String[] args) {
		SpringApplication.run(PruebarestApplication.class, args);
	}

	@Bean
	public boolean setJWTHelperRepo() {
		JWTHelper.setUserRepo(userRepo);
		return true;
	}

	@Bean
	public boolean setImageHelperRepo() {
		ImageHelper.setContactRepo(contactRepo);
		ImageHelper.setUserRepo(userRepo);
		return true;
	}

	@Bean
	public boolean setCheckerHelperRepo() {
		CheckerHelper.setContactRepo(contactRepo);
		return true;
	}

}
