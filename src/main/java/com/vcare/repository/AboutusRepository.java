package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.vcare.beans.Aboutus;

@Repository
public interface AboutusRepository extends JpaRepository<Aboutus,Integer> {
	
	@Query(value="select * from aboutus where is_active ='y' or is_active ='Y'",nativeQuery=true)
	List<Aboutus> getAllAbout();

}
