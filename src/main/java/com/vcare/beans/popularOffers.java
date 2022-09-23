package com.vcare.beans;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
/*
Description:This class is a pojo class for popularOffers table.
Author: Abhilash.
*/
@Entity
@Table(name = "popularOffer")
public class popularOffers {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String name;
	@Lob
	private String image;
	private char Isactive = 'Y';
	@OneToMany(mappedBy = ("popularOffers"), cascade = CascadeType.ALL)
	private List<ContractEmployee> contractemployees;
	@OneToMany(mappedBy = ("Offers"), cascade = CascadeType.ALL)
	private List<ContractService> contractServices;
	
	public List<ContractService> getContractServices() {
		return contractServices;
	}
	public void setContractServices(List<ContractService> contractServices) {
		this.contractServices = contractServices;
	}
	public List<ContractEmployee> getContractemployees() {
		return contractemployees;
	}
	public void setContractemployees(List<ContractEmployee> contractemployees) {
		this.contractemployees = contractemployees;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public char getIsactive() {
		return Isactive;
	}
	public void setIsactive(char isactive) {
		Isactive = isactive;
	}
}
