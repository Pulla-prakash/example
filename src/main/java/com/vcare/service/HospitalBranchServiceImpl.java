package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vcare.beans.Hospital;
import com.vcare.beans.HospitalBranch;
import com.vcare.repository.HospitalBranchRepository;

@Service
public class HospitalBranchServiceImpl implements HospitalBranchService {
	@Autowired
	HospitalBranchRepository hospitalBranchRepository;
	@Override
	public HospitalBranch addHospitalbranch(HospitalBranch hosps) {
		return hospitalBranchRepository.save(hosps);
	}
	@Override
	public HospitalBranch getHospitalbranchId(int HospitalbranchId) {
		return hospitalBranchRepository.getById(HospitalbranchId);
	}
	@Override
	public void updateHospitalbranch(HospitalBranch hosps) {
		hospitalBranchRepository.save(hosps);
	}
	@Override
	public void deleteHospitalbranchById(int HospitalbranchId) {
		hospitalBranchRepository.deleteById(HospitalbranchId);
	}
	@Override
    public List<HospitalBranch> getHospitalBranchesByServiceName(String ServiceName) {
        return hospitalBranchRepository.getHospitalBranchesByServiceName(ServiceName);
    }
	@Override
	public List<HospitalBranch> getBranchList(Hospital hospitalId) {
		return hospitalBranchRepository.getBranchList(hospitalId);
	}
}
