package com.vcare.service;

import java.util.List;
import com.vcare.beans.popularOffers;

public interface popularOfferService {
	
	List<popularOffers> getAllOffers();
	popularOffers addpopularOffers(popularOffers popularoffers);
	void updateOffers(popularOffers popularoffers);
	void deletePopularofferId(int id);
	popularOffers getoOfferById(int id);
	List<popularOffers> getAllPopularOffer();
}
