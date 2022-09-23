package com.vcare.service;

import java.util.List;
import com.vcare.beans.Prescription;

public interface PrescriptionService {
	
	List<Prescription> getAllPrescription();
	Prescription getPrescriptionById(int prescriptionId);
	Prescription addPrescription(Prescription prescription);
	void updatePrescription(Prescription prescription);
	void deletePrescriptionById(int prescriptionId);
	List<Prescription> getByPid(int pid);
	Prescription getByPesid(int pid);
	List<Prescription> getAllPrescriptions(int docotorId);
}
