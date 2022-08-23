package com.vcare.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vcare.beans.popularOffers;

@Repository
public interface popularOfferRepository  extends JpaRepository<popularOffers, Integer>{
	

}
