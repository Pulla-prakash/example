package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.Hospital;
import com.vcare.beans.LabTest;
import com.vcare.beans.LabTestAppointment;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest,Integer> {

//	@Query("select testName, al from LabTest al ")
//	List<Object> findDistinctTest();

	@Query(value="SELECT distinct  test_name,* FROM public.lab_test;",nativeQuery=true)
	List<LabTest> findDistinctTest();
	
	@Query("select h from LabTest h where h.hospitalBranchId like %?1%")
	List<LabTest> findLabTestByHospitalId(int hospitalBranchId);
	
	@Query("select al from LabTest al where al.laboratory.labId=?1")
	List<LabTest> findLabTestByLabId(int labid);
	
	@Query("select h from LabTest h where h.testName like %?1%")
	List<LabTest> findByTestName(String testName);
	
	@Query(value="select * from LabTest where is_active ='y' or is_active ='Y'",nativeQuery=true)
	List<LabTest> getAllActiveLabTest();

}
