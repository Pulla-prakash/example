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

@Entity
@Table(name = "popularOffer")
public class popularOffers {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	
	private int id;
	private String name;
	@Lob
	private String image;
	
	@OneToMany(mappedBy = ("popularOffers"), cascade = CascadeType.ALL)
	private List<ContractEmployees> contractemployees;
	
	
	public List<ContractEmployees> getContractemployees() {
		return contractemployees;
	}
	public void setContractemployees(List<ContractEmployees> contractemployees) {
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
	
	
	
	
	
	

}
