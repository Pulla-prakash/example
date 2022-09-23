package com.vcare.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.vcare.beans.Hospital;
@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

	@Query(value = "select al from Hospital al where lower(isactive)=lower('y')")
	List<Hospital> getActiveHospitalList();
}
