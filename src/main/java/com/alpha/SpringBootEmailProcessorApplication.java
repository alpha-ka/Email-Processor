package com.alpha;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.alpha.emailprocessor.EmailProcessor;

@SpringBootApplication
public class SpringBootEmailProcessorApplication {

	public static void main(String[] args) throws MessagingException, IOException {
		ConfigurableApplicationContext context=SpringApplication.run(SpringBootEmailProcessorApplication.class, args);
		EmailProcessor emailProcessor=context.getBean(EmailProcessor.class);
		emailProcessor.readMails();
	}

}
