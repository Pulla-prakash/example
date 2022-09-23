package com.vcare;

import javax.mail.MessagingException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages= {"com.vcare.controller","com.vcare.service","com.vcare.utils"})
@EntityScan("com.vcare.beans")
@EnableJpaRepositories("com.vcare.repository")
public class VcareProjectApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(VcareProjectApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void triggerMail() throws MessagingException {
	//	serviceemail.sendSimpleEmail("ajithnetha@gmail.com","body","subject" );
	}
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(VcareProjectApplication.class);
    }
}
