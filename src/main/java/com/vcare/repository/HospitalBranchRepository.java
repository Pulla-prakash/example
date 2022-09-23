package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.vcare.beans.Hospital;
import com.vcare.beans.HospitalBranch;


@Repository
public interface HospitalBranchRepository extends JpaRepository<HospitalBranch, Integer> {

	@Query("select h from HospitalBranch h where h.hospitals =?1")
	List<HospitalBranch> getBranchList(Hospital hospitalId);

	@Query("select h from HospitalBranch h where h.hospitals.hospitalId =?1")
	List<HospitalBranch> getBranchListByHospitalId(int hospitalId);
	
	@Query(value = "select * from hospitalbranch where lower(isactive)=lower('y')", nativeQuery = true)
	List<HospitalBranch> hospitalBranches();
	@Query(value="select * from hospitalbranch where hospital_branch_id in (select hospital_branch_id from services where lower(is_active)=lower('Y') and lower(service_name)=lower(?1))",nativeQuery=true)
    List<HospitalBranch> getHospitalBranchesByServiceName(String e);
}
