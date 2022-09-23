package com.vcare.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vcare.beans.Appointment;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.LabTest;
import com.vcare.beans.Patients;
import com.vcare.beans.Prescription;
import com.vcare.service.AppointmentService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.LabTestService;
import com.vcare.service.PatientsService;
import com.vcare.service.PrescriptionService;
import com.vcare.service.PrescriptionServiceImp;

@Controller
@RequestMapping("/Prescription")
public class PrescriptionController {

	@Autowired
	PrescriptionService prescriptionService;
	@Autowired
	PatientsService patientService;
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	PrescriptionServiceImp prescriptionServiceImpl;
	@Autowired
	LabTestService labTestService;

	static Logger log = Logger.getLogger(PrescriptionController.class.getClass());

	@GetMapping("/prescriptionlist/{doctorId}")
	public String prescriptionlist(Model model, @PathVariable("doctorId") int doctorId, HttpServletRequest request) {

		List<Prescription> prescription = prescriptionServiceImpl.getAllPrescriptions(doctorId);
		HttpSession session = request.getSession();
		if(session.getAttribute("DocId")==null) {
			return "redirect:/";
		}
		if (session.getAttribute("presupload") == "prescriptionUpload") {
			model.addAttribute("presupload", "Prescription uploaded successfully");
			session.setAttribute("presupload", "");
		}
		model.addAttribute("prescription", prescription);
		return "prescriptionlist";
	}

	@RequestMapping(value = "/uploadprescription/{appointmentId}", method = RequestMethod.GET)
	public String uploadPrescription(Model model, @PathVariable("appointmentId") long appointmentId,@ModelAttribute(value = "prescriptionobj") Prescription prescription) {Appointment appointment = appointmentService.getAppointmentById(appointmentId);
		List<Prescription> prescriptions = prescriptionService.getAllPrescription();
		for (Prescription obj : prescriptions) {
			log.debug("hello" + obj.getAppointment().getAppointmentId());
			if (obj.getAppointment().getAppointmentId() == appointmentId) {
				log.debug("appointment ---ID:::" + appointment.getAppointmentId());
				log.debug("appointment:::" + appointment.getAppointmentId());
				log.debug("appointment:::" + obj.getPrescription());
				model.addAttribute("name", appointment.getPatient().getFirstName());
				model.addAttribute("lastname", appointment.getPatient().getLastName());
				model.addAttribute("age", appointment.getPatient().getPatientAge());
				model.addAttribute("address", appointment.getPatient().getPatientAddress());
				model.addAttribute("mobile", appointment.getPatient().getPatientMobile());
				model.addAttribute("initial", appointment.getDoctor().getDoctorName());
				model.addAttribute("Prescription", obj.getPresctiption());
				model.addAttribute("PrescriptionId", obj.getPrescriptionId());
				return "patientprescriptionview";
			}
		}
		HospitalBranch hb = hospitalBranchService.getHospitalbranchId(appointment.getHospitalBranchId());
		List<LabTest> testlist = labTestService.findLabTestByHospitalId(hb.getHospitalBranchId());
		model.addAttribute("hositalBranch", hb);
		model.addAttribute("appointment", appointment);
		model.addAttribute("prescriptionobj", prescription);
		model.addAttribute("testlist", testlist);
		model.addAttribute("prescription", "null");
		return "prescriptionform";
	}

	@RequestMapping(value = "/saveprescription", method = RequestMethod.POST)
	public String addPrescription(Model model, @ModelAttribute(value = "prescriptionobj") Prescription pres,HttpServletRequest request) {
		Appointment a = appointmentService.getAppointmentById(pres.getAppointment().getAppointmentId());
		a.setLabTestId(pres.getLabTestId());
		pres.setCreated(LocalDate.now());
		log.debug(pres.getPrescriptionId());
		model.addAttribute("prescriptionobj", pres);
		prescriptionService.addPrescription(pres);
		List<Appointment> appointment = appointmentService.getAllAppointment();
		model.addAttribute("appointment", appointment);
		List<Prescription> prescriptionlist = prescriptionService.getAllPrescription();
		model.addAttribute("prescription", prescriptionlist);
		HttpSession session = request.getSession();
		session.setAttribute("presupload", "prescriptionUpload");
		return "redirect:/acceptpres/" + pres.getAppointment().getPatient().getPatientId();
	}

	@GetMapping("/acceptprescription/{id}")
	public String getHospitalById(Model model, @PathVariable("id") int Id, Prescription appointment) {
		Prescription prescriptionlist = prescriptionService.getPrescriptionById(Id);
		log.debug("inside getHospitalbranchId id is:::" + prescriptionlist.getPrescriptionId());
		model.addAttribute("hospitalBranchList", prescriptionlist);
		return "prescriptionform";
	}

	@GetMapping("/deleteprescription/{id}")
	public String deleteHospitalBranch(Model model, @PathVariable int id) {
		log.debug("inside deleteHospitalBranch id:::" + id);
		Prescription inactive = prescriptionService.getPrescriptionById(id);
		inactive.setIsactive('N');
		prescriptionService.updatePrescription(inactive);
		List<Prescription> appointmentList = prescriptionService.getAllPrescription();
		model.addAttribute("hospitalBranchList", appointmentList);
		return "prescriptionlist";

	}

	@GetMapping("/editprescription/{prescriptionid}")
	public String editPrescriptionById(Model model, @PathVariable("prescriptionid") int prescriptionId) {
		Prescription prescription = prescriptionService.getPrescriptionById(prescriptionId);
		log.debug("inside getHospitalbranchId id is:::" + prescriptionService.getPrescriptionById(prescriptionId));
		Appointment appointments = appointmentService
				.getAppointmentById(prescription.getAppointment().getAppointmentId());
		HospitalBranch hospitalBranch = hospitalBranchService
				.getHospitalbranchId(prescription.getAppointment().getHospitalBranchId());
		List<LabTest> selectedlist = new ArrayList<>();
		log.debug("testlistIF===================" + selectedlist.size());
		if (prescription.getLabTestId() != null) {
			String[] s = prescription.getLabTestId().split(",");
			for (String a : s) {
				LabTest test = labTestService.getLabTestById(Integer.parseInt(a));
				selectedlist.add(test);
			}
			model.addAttribute("selectedlist", selectedlist);
		} else {
			model.addAttribute("selectedlist", "");
		} 
		// all list of labtests        
		List<LabTest> labtestlist = labTestService.findLabTestByHospitalId(hospitalBranch.getHospitalBranchId());
		List<LabTest> testlistobj = new ArrayList<>();
		for (LabTest lb : labtestlist) {
			if (selectedlist.contains(lb)) {
				continue;
			} else {
				testlistobj.add(lb);
			}
		}
		model.addAttribute("hositalBranch", hospitalBranch);
		model.addAttribute("appointment", appointments);
		model.addAttribute("prescriptionobj", prescription);
		model.addAttribute("selectedlist", selectedlist);
		model.addAttribute("labtestlist", testlistobj);
		model.addAttribute("prescription", "notnull");
		return "prescriptionform";

	}

	@GetMapping("/prescriptionlistbypatient/{patientId}")
	public String prescriptionListByPatientId(Model model, @PathVariable("patientId") int patientId, HttpSession session) {
		if (session.getAttribute("pId") == null) {
			return "redirect:/";
		}
		List<Prescription> prescription = prescriptionService.getByPid(patientId);
		model.addAttribute("prescription", prescription);
		return "prescriptionlistbypatient";
	}

	@GetMapping("/acceptpres/{pid}")
	public String getHospital(Model model, @PathVariable("pid") int pid, Prescription prescription) {
		Patients patient = patientService.getPatientById(pid);
		log.debug("inside getHospitalbranchId id is:::" + patientService.getPatientById(pid));
		model.addAttribute("name", patient.getFirstName());
		model.addAttribute("lastname", patient.getLastName());
		model.addAttribute("age", patient.getPatientAge());
		model.addAttribute("address", patient.getPatientAddress());
		model.addAttribute("mobile", patient.getPatientMobile());
		List<Prescription> prescriptions = prescriptionService.getAllPrescription();
		log.debug("hello thiss is a prescription" + prescriptionService.getAllPrescription());
		for (Prescription obj : prescriptions) {
			if (obj.appointment.getPatient().getPatientId() == pid) {
				model.addAttribute("initial", obj.appointment.getDoctor().getDoctorName());
				model.addAttribute("Prescription", obj.getPrescription());
				model.addAttribute("prescriptionId", obj.getPrescriptionId());
				return "patientprescriptionview";
			}
		}
		return "patientprescriptionview";
	}
    @GetMapping("/viewPrescription/{pescriptionId}")
	public String getPreccriptionId(Model model, @PathVariable("pescriptionId") int pescriptionId, Prescription prescription) {
		Prescription prescriptions = prescriptionService.getByPesid(pescriptionId);
		log.debug("hello thiss is a prescription" + prescriptionService.getAllPrescription());
		model.addAttribute("initial", prescriptions.getAppointment().getDoctor().getDoctorName());
		model.addAttribute("name", prescriptions.getAppointment().getPatient().getFirstName());
		model.addAttribute("age", prescriptions.getAppointment().getPatient().getPatientAge());
		model.addAttribute("mobile", prescriptions.getAppointment().getPatient().getPatientMobile());
		model.addAttribute("address", prescriptions.getAppointment().getPatient().getPatientAddress());
		model.addAttribute("Prescription", prescriptions.getPrescription());
		model.addAttribute("prescriptionId", prescriptions.getPrescriptionId());
		log.debug("prescription id"+prescriptions.getPrescriptionId());
		return "patientprescriptionview";

	}
}
