package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.Appointment;
import com.vcare.beans.ServiceAppointment;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment,Integer> {
	
	
	@Query(value = "select al from ServiceAppointment al where  date between ?1 and ?2 ORDER BY date DESC, slot DESC")
    List<ServiceAppointment> serviceapplointmentListBetweenDates( String from, String to);
	@Query(value = "select al from ServiceAppointment al where al.contractEmployees.id=?3 and date between ?1 and ?2 ORDER BY date DESC, slot DESC")
    List<ServiceAppointment> serviceApplointmentListBetweenDatesByContractId( String from, String to,int contractid);

}
