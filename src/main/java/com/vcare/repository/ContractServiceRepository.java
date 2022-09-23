package com.vcare.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.BookingAmbulance;
import com.vcare.beans.ContractService;

@Repository
public interface ContractServiceRepository extends JpaRepository<ContractService,Integer> {
	
	  @Query("select Al from ContractService Al WHERE Al.Offers.id=?1")
	  List<ContractService> contractServList(int id);

	  @Query(value="select * from contractservice where isactive ='y' or isactive ='Y'",nativeQuery=true)
		List<ContractService> getAllActivecontractservices();


}
