package com.vcare.beans;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/*Descrption:This is the POJO class for MedicalHistory.
Author:NAYANI*/
@Entity
@Table(name = "MedicalHistory")
public class MedicalHistory {
	
	@Id
	@GeneratedValue
	int medicalId;
	String historyPath;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patientId")
	public Patients patient;
	String imageName;
	public int getMedicalId() {
		return medicalId;
	}
	public void setMedicalId(int medicalId) {
		this.medicalId = medicalId;
	}
	public Patients getPatient() {
		return patient;
	}
	public void setPatient(Patients patient) {
		this.patient = patient;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public String getHistoryPath() {
		return historyPath;
	}
	public void setHistoryPath(String historyPath) {
		this.historyPath = historyPath;
	}
}
