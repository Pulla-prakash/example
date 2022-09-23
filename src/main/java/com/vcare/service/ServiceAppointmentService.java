package com.vcare.service;

import java.util.List;
import com.vcare.beans.ServiceAppointment;

public interface ServiceAppointmentService {
	
	List<ServiceAppointment> getAllServiceAppointment();
	ServiceAppointment addServiceAppointment(ServiceAppointment serviceAppointment);
	void UpdateServiceAppointment(ServiceAppointment serviceAppointment);
	void deleteServiceAppointmentById(int serviceAppId);
	ServiceAppointment getByServiceAppointmentId(int serviceAppId);
	List<ServiceAppointment> getServiceApplointmentListBetweenDatesByContractId( String from, String to,int contractid);

}
