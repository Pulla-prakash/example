package com.vcare.utils;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailUtil {

	public static void sendSimpleEmail(JavaMailSender MailSender, String toEmail, String body, String subject) {
		SimpleMailMessage message = new SimpleMailMessage();
		System.out.println("Mail Sent...");
		message.setFrom("pullaprakash12@gmail.com");
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		MailSender.send(message);
		System.out.println("Mail Sent...");
	}
}
