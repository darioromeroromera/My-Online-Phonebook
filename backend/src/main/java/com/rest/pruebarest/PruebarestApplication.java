package com.rest.pruebarest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.rest.pruebarest.helpers.CheckerHelper;
import com.rest.pruebarest.helpers.ImageHelper;
import com.rest.pruebarest.helpers.JWTHelper;
import com.rest.pruebarest.repos.ContactRepo;
import com.rest.pruebarest.repos.ContactGroupRepo;
import com.rest.pruebarest.repos.UserRepo;

@SpringBootApplication
public class PruebarestApplication {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ContactRepo contactRepo;

	@Autowired ContactGroupRepo groupRepo;

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
		CheckerHelper.setGroupRepo(groupRepo);
		return true;
	}

}
