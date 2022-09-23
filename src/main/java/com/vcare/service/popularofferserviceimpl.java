package com.vcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vcare.beans.popularOffers;
import com.vcare.repository.popularOfferRepository;

@Service
public class popularofferserviceimpl implements popularOfferService {
	
	
	@Autowired
	popularOfferRepository offerRepository;

	@Override
	public List<popularOffers> getAllOffers() {
		return offerRepository.findAll();
	}
	@Override
	public popularOffers addpopularOffers(popularOffers popularoffers) {
		return offerRepository.save(popularoffers);
	}
	@Override
	public void updateOffers(popularOffers popularoffers) {
		offerRepository.save(popularoffers);	
	}
	@Override
	public void deletePopularofferId(int id) {
		offerRepository.deleteById(id);
	}
	@Override
	public popularOffers getoOfferById(int id) {
		return offerRepository.getById(id);
	}
	@Override
	public List<popularOffers> getAllPopularOffer() {
		return  offerRepository.getAllActiveoffer();
	}
}
