package com.vcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcare.beans.Hospital;
import com.vcare.repository.HospitalRepository;

@Service
public class HospitalServiceImpl implements HospitalService {
	@Autowired
	HospitalRepository hospitalRepository;
	@Override
	public Hospital addHospital(Hospital hop) {
		return hospitalRepository.save(hop);
	}
	@Override
	public Hospital getHospitalById(int hospitalId) {
		return hospitalRepository.findById(hospitalId).get();
	}
	@Override
	public void updateHospital(Hospital hop) {
		this.hospitalRepository.save(hop);
	}
	@Override
	public void deleteHospitalById(int hospitalId) {
		hospitalRepository.deleteById(hospitalId);
	}
	@Override
	public List<Hospital> getAllHospitals() {
		return hospitalRepository.findAll();
	}
	@Override
	public Hospital getAllHospital(int hospitalId) {
		return hospitalRepository.getById(hospitalId);
	}

	

}
