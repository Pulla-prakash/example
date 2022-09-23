package com.vcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcare.beans.LabTestAppointment;
import com.vcare.repository.LabTestAppointmentRepository;

@Service
public class LabTestAppointmentServiceImpl implements LabTestAppointmentService{
		@Autowired
		LabTestAppointmentRepository labAppointmentRepository;
		@Override
		public List<LabTestAppointment> getAllLabAppointment() {
			return labAppointmentRepository.findAll();
		}
		@Override
		public LabTestAppointment addLabAppointment(LabTestAppointment labAppointment) {
			return labAppointmentRepository.save(labAppointment);
		}
		@Override
		public void updateLabAppointment(LabTestAppointment labAppointment) {
			labAppointmentRepository.save(labAppointment);
		}
		@Override
		public void deleteLabAppointmentById(int labAppId) {
			labAppointmentRepository.deleteById(labAppId);
		}
		@Override
		public LabTestAppointment getlabAppointmentById(int labAppId) {
			return labAppointmentRepository.getById(labAppId);
		}
		@Override
		public List<LabTestAppointment> getAllActiveLabAppointment() {
			return labAppointmentRepository.getAllActiveLabTestAppointment();
		}
}
