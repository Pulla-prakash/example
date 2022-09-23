package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.popularOffers;

@Repository
public interface popularOfferRepository  extends JpaRepository<popularOffers, Integer>{
	
	@Query(value="select * from popular_offer where isactive ='y' or isactive ='Y'",nativeQuery=true)
	List<popularOffers> getAllActiveoffer();
}
