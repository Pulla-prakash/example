package com.vcare.service;

import java.util.List;
import com.vcare.beans.Laboratory;

public interface LaboratoryService {
	List<Laboratory> getAllLaboratory();
	List<Laboratory> getAllActiveLaboratory();
	Laboratory addLaboratory(Laboratory laboratory);
	void updateLaboratory(Laboratory laboratory);
	void deleteLaboratory(int labId);
	Laboratory getLaboratoryById(int labId);
	String validateduplicate(String email);
	public String sendSimpleEmail(String toEmail, String body, String subject);
}
