package ru.mail.sadovinalex.contacterbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class ContacterbotApplication {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		SpringApplication.run(ru.contacterbot.ContacterBot.class, args);
	}
}
