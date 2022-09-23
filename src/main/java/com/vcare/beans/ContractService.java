package com.vcare.beans;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/*
Description:This class is a pojo class for ContractService table.
Author: Ram.
*/
@Entity
@Table(name="contractservice")
public class ContractService {
	
	@Id
	@GeneratedValue
	private int conserviceId;
	private String serviceName;
	private String price;
	private char Isactive = 'Y';
	@OneToMany(mappedBy = "conServices")
	private Set<ContractEmployee> contractEmployees = new HashSet<ContractEmployee>();
	@OneToMany(mappedBy = "contractServices")
	private Set<ServiceAppointment> serAppointments = new HashSet<ServiceAppointment>();
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "offerId")
    public popularOffers Offers;

	public popularOffers getOffers() {
		return Offers;
	}
	public void setOffers(popularOffers offers) {
		Offers = offers;
	}
	public int getConserviceId() {
		return conserviceId;
	}
	public void setConserviceId(int conserviceId) {
		this.conserviceId = conserviceId;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Set<ContractEmployee> getContractEmployees() {
		return contractEmployees;
	}
	public void setContractEmployees(Set<ContractEmployee> contractEmployees) {
		this.contractEmployees = contractEmployees;
	}
	public Set<ServiceAppointment> getSerAppointments() {
		return serAppointments;
	}
	public void setSerAppointments(Set<ServiceAppointment> serAppointments) {
		this.serAppointments = serAppointments;
	}
	public char getIsactive() {
		return Isactive;
	}
	public void setIsactive(char isactive) {
		Isactive = isactive;
	}
}
