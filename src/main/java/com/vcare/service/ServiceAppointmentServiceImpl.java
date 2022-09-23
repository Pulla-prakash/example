package com.vcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vcare.beans.ServiceAppointment;
import com.vcare.repository.ServiceAppointmentRepository;

@Service
public class ServiceAppointmentServiceImpl implements ServiceAppointmentService {
	
	@Autowired
	ServiceAppointmentRepository serviceAppointmentRepository;

	@Override
	public List<ServiceAppointment> getAllServiceAppointment() {
		return serviceAppointmentRepository.findAll();
	}
	@Override
	public ServiceAppointment addServiceAppointment(ServiceAppointment serviceAppointment) {
		return serviceAppointmentRepository.save(serviceAppointment);
	}
	@Override
	public void UpdateServiceAppointment(ServiceAppointment serviceAppointment) {
		serviceAppointmentRepository.save(serviceAppointment);
	}
	@Override
	public void deleteServiceAppointmentById(int serviceAppId) {
		serviceAppointmentRepository.deleteById(serviceAppId);
	}
	@Override
	public ServiceAppointment getByServiceAppointmentId(int serviceAppId) {
		return serviceAppointmentRepository.getById(serviceAppId);
	}
	@Override
    public List<ServiceAppointment> getServiceApplointmentListBetweenDatesByContractId(String from, String to,
            int contractid) {
        return serviceAppointmentRepository.serviceApplointmentListBetweenDatesByContractId(from, to, contractid);
    }
}
