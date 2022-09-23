package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.vcare.beans.LabTest;
import com.vcare.repository.LabTestRepository;

@Service
public class LabTestServiceImpl implements LabTestService{
	@Autowired 
	LabTestRepository labTestRepository;
	@Override
	public List<LabTest> getAllLabTest() {
		return labTestRepository.findAll();
	}
	@Override
	public LabTest addLabTest(LabTest labTest) {
		return labTestRepository.save(labTest);
	}
	@Override
	public void updateLabTest(LabTest labTest) {
		labTestRepository.save(labTest);
	}
	@Override
	public void deleteLabTest(int testId) {
		labTestRepository.deleteById(testId);
	}
	@Override
	public LabTest getLabTestById(int testId) {
		return labTestRepository.getById(testId);
	}
	@Override
	public List<LabTest> findLabTestByHospitalId(int hospitalBranchId) {
		return labTestRepository.findLabTestByHospitalId(hospitalBranchId);
	}
	@Override
	public List<LabTest> findLabTestByLabId(int labid) {
		return labTestRepository.findLabTestByLabId(labid);
	}
	@Override
	public List<LabTest> getAllActiveLabTest() {
		return labTestRepository.getAllActiveLabTest();
	}
}