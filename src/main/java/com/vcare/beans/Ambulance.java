package com.vcare.beans;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/*
Description:This class is a pojo class for Ambulance table.
Author:Abhilash.
*/
@Entity
@Table(name="Ambulance")
public class Ambulance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ambulanceId;
	private String ambulanceType;
	private String vechileType;
	private String vechileNumber;
	private double charges;
	private char isActive ='Y';
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="hospitalBranchId")
	private HospitalBranch hospitalbranch;

	public int getAmbulanceId() {
		return ambulanceId;
	}
	public void setAmbulanceId(int ambulanceId) {
		this.ambulanceId = ambulanceId;
	}
	public HospitalBranch getHospitalbranch() {
		return hospitalbranch;
	}
	public void setHospitalbranch(HospitalBranch hospitalbranch) {
		this.hospitalbranch = hospitalbranch;
	}
	public String getAmbulanceType() {
		return ambulanceType;
	}

	public void setAmbulanceType(String ambulanceType) {
		this.ambulanceType = ambulanceType;
	}
	public String getVechileType() {
		return vechileType;
	}
	public void setVechileType(String vechileType) {
		this.vechileType = vechileType;
	}
	
	public String getVechileNumber() {
		return vechileNumber;
	}
	public void setVechileNumber(String vechileNumber) {
		this.vechileNumber = vechileNumber;
	}
	public double getCharges() {
		return charges;
	}
	public void setCharges(double charges) {
		this.charges = charges;
	}
	public char getIsActive() {
		return isActive;
	}
	public void setIsActive(char isActive) {
		this.isActive = isActive;
	}
}
