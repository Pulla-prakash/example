package com.vcare.service;

import java.util.List;
import com.vcare.beans.LabTest;

public interface LabTestService {
	List<LabTest> getAllLabTest();
	List<LabTest> getAllActiveLabTest();
	LabTest addLabTest(LabTest labTest);
	void updateLabTest(LabTest labTest);
	void deleteLabTest(int testId);
	LabTest getLabTestById(int testId);
	List<LabTest> findLabTestByHospitalId(int hospitalBranchId);
	List<LabTest> findLabTestByLabId(int labid);
}
