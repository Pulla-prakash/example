package com.vcare.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/*Descrption:This is the POJO class for LabAppointment.
This table stores all the lab tests booked.
Author:NAYANI*/
@Entity
@Table(name="lab_appointments")
public class LabTestAppointment {
	
	@Id
	@GeneratedValue
	private int labappId;
	private int patientId;
	private int labId;
	private char isActive ='Y';
	private String testId;
	public int getLabappId() {
		return labappId;
	}
	public void setLabappId(int labappId) {
		this.labappId = labappId;
	}
	public int getPatientId() {
		return patientId;
	}
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}
	public int getLabId() {
		return labId;
	}
	public void setLabId(int labId) {
		this.labId = labId;
	}
	public char getIsActive() {
		return isActive;
	}
	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}
	public String getTestId() {
		return testId;
	}
	public void setTestId(String testId) {
		this.testId = testId;
	}
}
