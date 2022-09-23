package com.vcare.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.speedment.jpastreamer.application.JPAStreamer;
import com.vcare.beans.Services;
import com.vcare.repository.ServiceRepository;

@Service
public class ServiceServiceImpl implements ServiceService {
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired 
	JPAStreamer jpaStreamer;
	
	@Override
	public List<Services> getAllServices() {
		return serviceRepository.findAll();
	}
	@Override
	public Services addServices(Services service) {
		return serviceRepository.save(service);
	}
	@Override
	public void UpdateServices(Services service) {
		serviceRepository.save(service);
	}
	@Override
	public void deleteServicesById(int serviceId) {
		serviceRepository.deleteById(serviceId);
	}
	@Override
	public Services getById(int serviceId) {
		return serviceRepository.findById(serviceId).get();
	}
	@Override
	public List<Services> findSubCatagoryByCatagory(int Id) {
		return serviceRepository.findservicesbyHospitalBranch(Id);
	}
	public List<Services> servicesByBranch(int branchId){
		return jpaStreamer.stream(Services.class).filter(a -> branchId==a.getHospitalbranch().getHospitalBranchId()).collect(Collectors.toList());
	}
	@Override
	public List<Services> getAllActiveServices() {
		return serviceRepository.getAllActiveServices();
	}
}