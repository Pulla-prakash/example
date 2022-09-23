package com.vcare.service;

import java.util.List;

import com.vcare.beans.ContractService;
public interface ContractServicesService {
	
	List<ContractService> getAllConServices();
	ContractService addConServices(ContractService contractServices);
	 void UpdateConServices(ContractService contractServices);
	 void deleteconServicesById(int bookingId );
	 ContractService getByConServiceId(int bookingId);
	
		List<ContractService> getAllActiveConServices();

}
