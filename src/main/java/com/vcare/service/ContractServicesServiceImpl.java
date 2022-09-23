package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vcare.beans.ContractService;
import com.vcare.repository.ContractServiceRepository;

@Service
public class ContractServicesServiceImpl implements ContractServicesService {
	
	@Autowired
	ContractServiceRepository contractServiceRepository;
	@Override
	public List<ContractService> getAllConServices() {
		return contractServiceRepository.findAll();
	}
	@Override
	public ContractService addConServices(ContractService contractServices) {
		return contractServiceRepository.save(contractServices);
	}
	@Override
	public void UpdateConServices(ContractService contractServices) {
		contractServiceRepository.save(contractServices);
	}
	@Override
	public void deleteconServicesById(int bookingId) {
		contractServiceRepository.deleteById(bookingId);
	}
	@Override
	public ContractService getByConServiceId(int bookingId) {
		return contractServiceRepository.getById(bookingId);
	}
	@Override
	public List<ContractService> getAllActiveConServices() {
		return contractServiceRepository.getAllActivecontractservices();
	}
}
