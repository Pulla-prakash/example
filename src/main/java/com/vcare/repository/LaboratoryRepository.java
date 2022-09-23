package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.ContractEmployee;
import com.vcare.beans.Doctor;
import com.vcare.beans.Laboratory;

@Repository
public interface LaboratoryRepository extends JpaRepository<Laboratory,Integer> {

	@Query("select al.email from Laboratory al where al.email=?1")
	String findByLaboratoryEmail(String email);
	@Query("select al from Laboratory al where al.email=?1 and al.password=?2")
	Laboratory findByMailIdIgnoreCaseAndPassword(String email, String password);
	@Query(value="select * from Laboratory where is_active ='y' or is_active ='Y'",nativeQuery=true)
	List<Laboratory> getAllActiveLaboratory();
	
}
