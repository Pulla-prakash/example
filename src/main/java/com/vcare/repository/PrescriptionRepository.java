package com.vcare.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.vcare.beans.Prescription;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {
	
	@Query(value = "SELECT *	FROM public.prescription p where p. appointment_id in  \r\n"
			+ "			(select appointment_id from appointment  where patient_id=:pid) order by prescription_id desc;", nativeQuery = true)
	
	List<Prescription> getByPid(@Param("pid") int pid);
	
	
	@Query(value="select al from Prescription al where al.isactive ='y' or isactive ='Y' and al.appointment.doctor.doctorId=?1")
	List<Prescription> getAllActivePrescription(int doctorId);

	
	
	
}
