package com.vcare.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.vcare.beans.Ambulance;
@Repository
public interface AmbulanceRepository extends JpaRepository<Ambulance, Integer> {
	@Query("select al from Ambulance al where al.hospitalbranch.hospitalBranchId=?1")
	List<Ambulance> getambbyBranchList( int hospitalBranchId);

	@Query(value = "Select * from ambulance where hospital_branch_id =?1 and lower(is_active)=lower('y')", nativeQuery = true)
    List<Ambulance> ambulanceDetails(int hospitalBranchId);
	
}
