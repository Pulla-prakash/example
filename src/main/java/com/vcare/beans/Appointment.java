package com.vcare.beans;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/*
* Description: This POJO class is for doctor Appointment.
* It stores all the appointment details of patient 
* Author: Ajith Netha
*/
@Entity
@Table(name = "appointment")
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long appointmentId;
	private static int appointmentValidity = 4;
	private String patientPurpose;
	private String slot;
	private String endSlot;
	private String link;
	private int hospitalBranchId;
	private String consultantType;
	private String paymentStatus;
	private String date;
	private String labTestId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patientId")
	private Patients patient;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contractId")
	private ContractEmployee  contractEmployees;
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doctorId" )
	public Doctor doctor;
	@OneToMany(mappedBy = "appointment")
	private Set<Prescription> prescription = new HashSet<Prescription>();
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "labId")
	private Laboratory laboratory;
	@Column(columnDefinition = "character(1) DEFAULT 'Y'::bpchar")
	private char isactive = 'Y';
	private String createdBy;
	private String updateBy;
	public String getLabTestId() {
		return labTestId;
	}
	public void setLabTestId(String labTestId) {
		this.labTestId = labTestId;
	}
	public ContractEmployee getContractEmployees() {
		return contractEmployees;
	}
	public void setContractEmployees(ContractEmployee contractEmployees) {
		this.contractEmployees = contractEmployees;
	}
	public Laboratory getLaboratory() {
		return laboratory;
	}
	public void setLaboratory(Laboratory laboratory) {
		this.laboratory = laboratory;
	}
	public long getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(long appointmentId) {
		this.appointmentId = appointmentId;
	}
	public int getHospitalBranchId() {
		return hospitalBranchId;
	}
	public void setHospitalBranchId(int hospitalBranchId) {
		this.hospitalBranchId = hospitalBranchId;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getConsultantType() {
		return consultantType;
	}
	public void setConsultantType(String consultantType) {
		this.consultantType = consultantType;
	}
	public static int getAppointmentValidity() {
		return appointmentValidity;
	}
	public static void setAppointmentValidity(int appointmentValidity) {
		Appointment.appointmentValidity = appointmentValidity;
	}
	public String getPatientPurpose() {
		return patientPurpose;
	}
	public void setPatientPurpose(String patientPurpose) {
		this.patientPurpose = patientPurpose;
	}
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public Patients getPatient() {
		return patient;
	}
	public void setPatient(Patients patient) {
		this.patient = patient;
	}
	public char getIsactive() {
		return isactive;
	}
	public void setIsactive(char isactive) {
		this.isactive = isactive;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public Set<Prescription> getPrescription() {
		return prescription;
	}
	public void setPrescription(Set<Prescription> prescription) {
		this.prescription = prescription;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getEndSlot() {
		return endSlot;
	}
	public void setEndSlot(String endSlot) {
		this.endSlot = endSlot;
	}
}