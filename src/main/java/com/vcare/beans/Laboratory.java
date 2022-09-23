package com.vcare.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/*Descrption:This is the POJO class for Laboratory.
This table stores all the laboratory details.
Author:NAYANI*/
@Entity
@Table(name="laboratory")
public class Laboratory {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)	
	private int labId;
	private String labName;
	private String address;
	private String email;
	private String password;
	private char isActive='Y';
	@OneToMany(mappedBy = ("laboratory"), cascade = CascadeType.ALL)
    private List<LabTest> labTest;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "hospitalBranchId")
	private Hospital hospital;
	@OneToMany(mappedBy = "laboratory")
	private List<Appointment> appointment;
	public Hospital getHospital() {
		return hospital;
	}
	public void setHospital(Hospital hospital) { 
		this.hospital = hospital;
	}
	public List<Appointment> getAppointment() {
		return appointment;
	}
	public void setAppointment(List<Appointment> appointment) {
		this.appointment = appointment;
	}
	public int getLabId() {
		return labId;
	}
	public void setLabId(int labId) {
		this.labId = labId;
	}
	public String getLabName() {
		return labName;
	}
	public void setLabName(String labName) {
		this.labName = labName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public char getIsActive() {
		return isActive;
	}
	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}
	public List<LabTest> getLabTest() {
		return labTest;
	}
	public void setLabTest(List<LabTest> labTest) {
		this.labTest = labTest;
	}
}
