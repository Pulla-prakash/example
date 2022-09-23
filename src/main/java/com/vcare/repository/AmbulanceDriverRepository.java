package com.vcare.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.vcare.beans.AmbulanceDriverAssosiation;


public interface AmbulanceDriverRepository extends JpaRepository<AmbulanceDriverAssosiation, Integer>  {
	
	@Query("select al from AmbulanceDriverAssosiation al where al.hospitalbranch.hospitalBranchId=?1")
	  List<AmbulanceDriverAssosiation>ambdriverList(int hbrnachId);
	@Query("select al.ambulancetype from AmbulanceDriverAssosiation al where al.ambulancetype=?1")
	String findByAmbulanceIdDuplicate(String doctorMailId);
	
	@Query(value="Select * from ambulance_driver_assosiation where hospital_branch_id =?1 and lower(isactive)=lower('y')",nativeQuery=true)
	List<AmbulanceDriverAssosiation>ambulaceDriverActiveList(int hbrnachId);
	
	
}
