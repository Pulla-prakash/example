package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.vcare.beans.SocialNetworks;
import com.vcare.repository.SocialNetworkRepository;

@Service
public class SocialNetworkServiceImpl implements SocialNetworkService{
	
	@Autowired
	SocialNetworkRepository socialNetworkRepository;
	@Override
	public List<SocialNetworks> getAllNetworks() {
		return socialNetworkRepository.findAll();
	}
	@Override
	public SocialNetworks addNetwork(SocialNetworks network) {
		return socialNetworkRepository.save(network);
	}
	@Override
	public void updateNetwork(SocialNetworks network) {
		socialNetworkRepository.save(network);
	}
	@Override
	public void deleteNetworkById(int networkId) {
		socialNetworkRepository.deleteById(networkId);
	}
	@Override
	public SocialNetworks getNetworkById(int networkId) {
		return socialNetworkRepository.findById(networkId).get();
	}
	@Override
	public void addService(MultipartFile file, SocialNetworks network) {
		socialNetworkRepository.save(network);
	}
	@Override
	public List<SocialNetworks> getAllSocialNetwork() {
		return socialNetworkRepository.getAllActiveNetworks();
	}
}
