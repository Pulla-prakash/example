package com.vcare.beans;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/*
Description:This class is a pojo class for Aboutus table.
Author: Abhilash.
*/
@Entity
@Table(name="serviceappointment")
public class ServiceAppointment {
	@Id
	@GeneratedValue
	private int serviceAppId;
	private String purpose;
	private String date;
	private String slot;
	private String paymentstatus;
	private String serviceappmntstatus;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "conServiceId")
	public ContractService contractServices;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "patientId")
	public Patients patients;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ContractId")
    public ContractEmployee contractEmployees;
	
	public String getServiceappmntstatus() {
		return serviceappmntstatus;
	}
	public void setServiceappmntstatus(String serviceappmntstatus) {
		this.serviceappmntstatus = serviceappmntstatus;
	}
	public ContractService getContractServices() {
		return contractServices;
	}
	public void setContractServices(ContractService contractServices) {
		this.contractServices = contractServices;
	}
	public int getServiceAppId() {
		return serviceAppId;
	}
	public void setServiceAppId(int serviceAppId) {
		this.serviceAppId = serviceAppId;
	}
	public String getPurpose() {
		return purpose;
	}
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	public String getPaymentstatus() {
		return paymentstatus;
	}
	public void setPaymentstatus(String paymentstatus) {
		this.paymentstatus = paymentstatus;
	}
	public ContractEmployee getContractEmployees() {
		return contractEmployees;
	}
	public void setContractEmployees(ContractEmployee contractEmployees) {
		this.contractEmployees = contractEmployees;
	}
	public Patients getPatients() {
		return patients;
	}
	public void setPatients(Patients patients) {
		this.patients = patients;
	}
}
