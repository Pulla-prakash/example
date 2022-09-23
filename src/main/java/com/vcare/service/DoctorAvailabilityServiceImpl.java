package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vcare.beans.DoctorAvailability;
import com.vcare.repository.DoctorAvailabilityRepository;

@Service
public class DoctorAvailabilityServiceImpl implements DoctorAvailabilityService {
	@Autowired
	DoctorAvailabilityRepository doctorAvailabilityRepository;

	@Override
	public DoctorAvailability addDoctorAvailability(DoctorAvailability availability) {
		return doctorAvailabilityRepository.save(availability);
	}
	@Override
	public DoctorAvailability getDoctorAvailabilityId(int availabilityId) {
		return doctorAvailabilityRepository.getById(availabilityId);
	}
	@Override
	public void editEmployee(DoctorAvailability availability) {
		doctorAvailabilityRepository.save(availability);
	}
	@Override
	public void deleteEmployeeById(int availabilityId) {
		doctorAvailabilityRepository.deleteById(availabilityId);
	}
	@Override
	public List<DoctorAvailability> getAllDoctorAvailability() {
		return doctorAvailabilityRepository.findAll();
	}
}
