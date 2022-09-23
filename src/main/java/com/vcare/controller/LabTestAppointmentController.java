package com.vcare.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.vcare.beans.LabTest;
import com.vcare.beans.LabTestAppointment;
import com.vcare.beans.Laboratory;
import com.vcare.beans.Patients;
import com.vcare.repository.LabTestAppointmentRepository;
import com.vcare.repository.LabTestRepository;
import com.vcare.service.LabTestAppointmentService;
import com.vcare.service.LaboratoryService;
import com.vcare.service.PatientsService;

@Controller
@RequestMapping("/labTestAppointment")
public class LabTestAppointmentController {
	@Autowired
	LabTestAppointmentService labTestAppointmentService;
	@Autowired
	LabTestAppointmentRepository labAppointmentRepository;
	@Autowired
	LabTestRepository labTestRepository;
	@Autowired
	LaboratoryService laboratoryService;
	@Autowired
	PatientsService patientsService;
	
	static Logger log = Logger.getLogger(LabTestAppointmentController.class.getName());
	
	@GetMapping("/labTestAppointmentList")
	public String labTestAppointmentList(Model model, LabTestAppointment labAppointment) {
		List<LabTestAppointment> listLabApp = labTestAppointmentService.getAllActiveLabAppointment();
		model.addAttribute("aboutlist", listLabApp);
		return "labtestappointmentlist";
	}
	@GetMapping("/saveLabTestAppointment")
	public String saveLabTestAppointment(Model model,@RequestParam("testId") String testNames){
		String[] test=testNames.split(",");
		Set<Laboratory> uniqeLabApp=new HashSet<>();
		Set<LabTest> uniqeLabTest=new HashSet<>();
		StringBuffer testids=new StringBuffer();
		for(String testname:test) {
			log.debug(testname);
			List<LabTest> labTest=labTestRepository.findByTestName(testname);
			uniqeLabTest.addAll(labTest);
			log.debug(labTest.size());
			for(LabTest Lt:labTest) {
				Laboratory Laboratory=laboratoryService.getLaboratoryById(Lt.getLaboratory().getLabId());
				uniqeLabApp.add(Laboratory);
			}
		}
		uniqeLabTest.forEach(n->{
			testids.append(n.getTestId()+",") ;
		});
		log.debug(testids);
		model.addAttribute("uniqeLabApp",uniqeLabApp);
		model.addAttribute("uniqeLabTest",uniqeLabTest);
		model.addAttribute("testids",testids);
		return"labtestdetails";
	}
	@GetMapping("/labpay/{patientId}/{laboratoryId}/{testid}")
	public String payLabs(Model model,@PathVariable("patientId") int patientId,@PathVariable("laboratoryId") int laboratoryId,@PathVariable("testid") String testids,HttpSession session,LabTestAppointment labAppointment) {
		Laboratory objLab =laboratoryService.getLaboratoryById(laboratoryId);
		  String[] test=testids.split(",");
		  log.debug("test name"+test[0]);
		  Set<LabTest> uniqeLabTest=new HashSet<>();
		  for(String testid:test) {
		LabTest labTest=labTestRepository.getById(Integer.parseInt(testid));
		  uniqeLabTest.add(labTest);
		  }
		Double total=0.0;
		  for(LabTest lb:uniqeLabTest){
			  total=total+lb.getPrice();
		  }
		model.addAttribute("uniqeLabTest",uniqeLabTest);
		model.addAttribute("labAppointment",labAppointment);
		int pid= (int) session.getAttribute("pId");
		Patients patientObj=patientsService.getPatientById(pid);
		model.addAttribute("laboratory", objLab);
		model.addAttribute("patient",patientObj);
		model.addAttribute("total",total);
		return "labtestpayment";
	}
	@PostMapping("/savelabappointment")
	public String saveLabTestAppointment(Model model,@RequestParam("patientId") int patientId,@RequestParam("testId") String testIds,HttpSession session,LabTestAppointment labapp) {
		int pid= (int) session.getAttribute("patientId");
		Patients patientObj=patientsService.getPatientById(pid);
		model.addAttribute("patient",patientObj);
		labTestAppointmentService.addLabAppointment(labapp);
		Laboratory objLab =laboratoryService.getLaboratoryById(labapp.getLabId());
		 String[] test=testIds.split(",");
		  log.debug("test name"+test[0]);
		  Set<LabTest> uniqeLabTest=new HashSet<>();
		  for(String testId:test) {
		LabTest labTest=labTestRepository.getById(Integer.parseInt(testId));
		  uniqeLabTest.add(labTest);
		  }
		Double total=0.0;
		  for(LabTest lb:uniqeLabTest){
			  total=total+lb.getPrice();
		  }
		model.addAttribute("uniqeLabTest",uniqeLabTest);
		model.addAttribute("total",total);
		model.addAttribute("laboratory",objLab);
		model.addAttribute("labapp",labapp);
		return "labtestpayment";
	}
}
