package com.vcare.service;

import java.util.List;

import com.vcare.beans.LabTestAppointment;

public interface LabTestAppointmentService {
	
	List<LabTestAppointment> getAllLabAppointment();
	List<LabTestAppointment> getAllActiveLabAppointment();
	LabTestAppointment addLabAppointment(LabTestAppointment labAppointment);
	void updateLabAppointment(LabTestAppointment labAppointment);
	void deleteLabAppointmentById(int labAppId);
	LabTestAppointment getlabAppointmentById(int labAppId);


}
