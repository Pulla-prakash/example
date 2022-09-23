package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.Insurance;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Integer> {

	@Query(value="select * from insurance where isactive ='y' or isactive ='Y'",nativeQuery=true)
	List<Insurance> getAllActiveInsurance();
	
	
}
