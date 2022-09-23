package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.Appointment;
import com.vcare.beans.BookingAmbulance;
import com.vcare.beans.ContractEmployee;
@Repository
public interface BookingAmbulanceRepository extends JpaRepository<BookingAmbulance,Integer>{
	
	@Query(value="select * from booking_ambulance where isactive ='y' or isactive ='Y'",nativeQuery=true)
	List<BookingAmbulance> getAllActiveAmbulanceBookings();



}
