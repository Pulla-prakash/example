package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.vcare.beans.LabTestAppointment;
import com.vcare.beans.Laboratory;

@Repository
public interface LabTestAppointmentRepository extends JpaRepository<LabTestAppointment,Integer> {
	@Query(value="select * from LabTestAppointment where is_active ='y' or is_active ='Y'",nativeQuery=true)
	List<LabTestAppointment> getAllActiveLabTestAppointment();
}
