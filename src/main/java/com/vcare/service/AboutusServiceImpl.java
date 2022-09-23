package com.vcare.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vcare.beans.Aboutus;
import com.vcare.repository.AboutusRepository;

@Service
public class AboutusServiceImpl implements AboutusService {
	
	@Autowired
	AboutusRepository aboutusRepository;

	@Override
	public List<Aboutus> getAllAbouts() {
		return aboutusRepository.findAll();
	}
	@Override
	public Aboutus addAbouts(Aboutus about) {
		return aboutusRepository.save(about);
	}
	@Override
	public void updateAboutus(Aboutus about) {
		aboutusRepository.save(about);
	}
	@Override
	public void deleteAboutusById(int id) {
		aboutusRepository.deleteById(id);
	}
	@Override
	public Aboutus getAboutusById(int id) {
		return aboutusRepository.findById(id).get();
	}
	@Override
	public List<Aboutus> getAllAboutus() {
		return aboutusRepository.getAllAbout();
	}
}
