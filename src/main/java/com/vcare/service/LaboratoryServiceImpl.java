package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.vcare.beans.Laboratory;
import com.vcare.repository.LaboratoryRepository;

@Service
public class LaboratoryServiceImpl implements LaboratoryService{
	@Autowired
	LaboratoryRepository laboratoryRepository;
	@Autowired
	JavaMailSender javaMailSender;
	@Override
	public List<Laboratory> getAllLaboratory() {
		return laboratoryRepository.findAll();
	}
	@Override
	public Laboratory addLaboratory(Laboratory laboratory) {
		return laboratoryRepository.save(laboratory);
	}
	@Override
	public void updateLaboratory(Laboratory laboratory) {
		laboratoryRepository.save(laboratory);
	}
	@Override
	public void deleteLaboratory(int labId) {
		laboratoryRepository.deleteById(labId);
	}
	@Override
	public Laboratory getLaboratoryById(int labId) {
		return laboratoryRepository.getById(labId);
	}
	@Override
	public String validateduplicate(String email) {
		return laboratoryRepository.findByLaboratoryEmail(email);
	}
	@Override
	public String sendSimpleEmail(String toEmail, String body, String subject) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("pullaprakash12@gmail.com");
		message.setTo(toEmail);
		message.setText(body);
		message.setSubject(subject);
		javaMailSender.send(message);
		return "successfulRegistration";
	}
	@Override
	public List<Laboratory> getAllActiveLaboratory() {
		return laboratoryRepository.getAllActiveLaboratory();
	}
}
