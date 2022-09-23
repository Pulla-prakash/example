package com.vcare.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.vcare.beans.Appointment;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.LabTest;
import com.vcare.beans.Laboratory;
import com.vcare.beans.Patients;
import com.vcare.repository.HospitalBranchRepository;
import com.vcare.repository.LabTestRepository;
import com.vcare.service.AppointmentService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.LabTestService;
import com.vcare.service.LaboratoryService;
import com.vcare.service.PatientsService;

@Controller
@RequestMapping("/labTest")
public class LabTestController {
	@Autowired 
	LabTestService labTestService; 
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired(required=true)
	LaboratoryService laboratoryService;
	@Autowired
	HospitalBranchRepository hospitalBranchRepository;
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	LabTestRepository labTestRepository;
	@Autowired
	PatientsService patientsService;
	static Logger log = Logger.getLogger(AdminController.class.getName());
	@GetMapping("/testList")
	public String getLaboraoryById(Model model,Laboratory lab) {
		List<LabTest> testList=labTestService.getAllActiveLabTest();
		model.addAttribute("testList", testList);
		model.addAttribute("labId", lab.getLabId());
		return "testList";
	}
	@RequestMapping(value="/addTest/{laboratoryId}", method= RequestMethod.GET)
	public String addLaboratory(Model model,@PathVariable("laboratoryId") int laboratoryId,LabTest labTest,HttpServletRequest request) {
		Laboratory lab=laboratoryService.getLaboratoryById(laboratoryId);
		HttpSession session=request.getSession();
		List<HospitalBranch> hblist=hospitalBranchRepository.getBranchListByHospitalId(lab.getHospital().getHospitalId());
		model.addAttribute("labTest", labTest);
		model.addAttribute("hospId", lab.getHospital().getHospitalId());
		model.addAttribute("hblist", hblist);
		model.addAttribute("labId", laboratoryId);
		session.setAttribute("labIds", laboratoryId);
		return "labTestForm";
	}
	@RequestMapping(value="/saveTest",method=RequestMethod.POST)
	public String saveLabTest(Model model,LabTest labTest) {
		labTestService.addLabTest(labTest);
		model.addAttribute("labTest", labTest);
		return "redirect:/testList";
	}
	@RequestMapping(value="/editTest/{testId}",method=RequestMethod.GET)
	public String editLabTest(Model model,@PathVariable("testId") int testId,LabTest labTest) {
		labTest=labTestService.getLabTestById(testId);
		List<HospitalBranch> hblist=hospitalBranchRepository.getBranchListByHospitalId(labTest.getLaboratory().getHospital().getHospitalId());
		model.addAttribute("hblist", hblist);
		model.addAttribute("labId", labTest.getLaboratory().getLabId());
		model.addAttribute("labTest", labTest);
		return "labTestForm";
	}
	@RequestMapping(value="/deleteTest/{testId}",method=RequestMethod.GET)
	public String deleteLabTest(Model model,@PathVariable("testId") int testId,LabTest labTest) {
		labTestService.deleteLabTest(testId);
		model.addAttribute("labList", labTestService.getAllActiveLabTest());
		return "redirect:/testList";
	}
	@RequestMapping(value="/disTest",method=RequestMethod.GET)
	public String viewDistinctTest(Model model,LabTest labTest) {
	List<LabTest> distincttestList = (List<LabTest>) labTestRepository.findDistinctTest();
	log.debug(distincttestList.size());
		model.addAttribute("disTestList", distincttestList);
	return "allLabTest.html";
	}
	@RequestMapping(value="/disTest/{patientId}",method=RequestMethod.GET)
	public String viewTests(Model model,@PathVariable("patientId") int patientId,LabTest labTest,HttpServletRequest request) {
		HttpSession session= request.getSession();
		log.debug(session.getId());
		List<LabTest> distincttestList = (List<LabTest>) labTestRepository.findDistinctTest();
		log.debug(distincttestList.size());
		session.setAttribute("pid", patientId);
		model.addAttribute("disTestList", distincttestList);
		log.debug("this is an labId"+labTest.getTestId());
		return "alllabtest.html";
	}
	@RequestMapping(value="/testInfo/{testId}", method=RequestMethod.GET)
	public String viewTestPage(Model model,@PathVariable("testId") int testId) {
		LabTest test = labTestService.getLabTestById(testId);
		String str = test.getHospitalBranchId();
		String[] arrhbid = str.split(",");
		List<HospitalBranch> branchId=new ArrayList();
		for(String a:arrhbid) {
			HospitalBranch hospitalBranch=hospitalBranchService.getHospitalbranchId(Integer.parseInt(a));
			branchId.add(hospitalBranch);
		}
		log.debug(branchId);
	model.addAttribute("hbid", branchId);
		model.addAttribute("test", labTestService.getLabTestById(testId));
		return "testPage";
	}
	@RequestMapping(value="/bookTest/{patientId}/{TestId}")
	public String bookTest(Model model,@PathVariable("patientId") int patientId,@PathVariable("TestId") int testId) {
		LabTest objLab=labTestService.getLabTestById(testId);
	model.addAttribute("labData",objLab.getLaboratory().getLabName());
		return "testex";
	}
	@RequestMapping(value="/bookLabTest/{appointmentId}/{labTestId}", method=RequestMethod.GET)
	public String bookLabTest(Model model,@PathVariable("appointmentId") int appointmentId,@PathVariable("labTestId") String labTestId) {
		String[] labId=labTestId.split(",");
		log.debug(labId[0]);
		//to store the total tests required from a patient by LabTestId
		List<LabTest> labtestlist=new ArrayList<>();
		Set<Integer> uniqeLaboratories=new HashSet<>();
		for(String labtestId:labId) {
			LabTest labtestobj=labTestService.getLabTestById(Integer.parseInt(labtestId));
			//labtests
			labtestlist.add(labtestobj);
			//laboratory ids to get laboratories
			uniqeLaboratories.add(labtestobj.getLaboratory().getLabId());
		}
		//to store test available in laboratories
		List<Laboratory> availableLaboratories=new ArrayList<>();
		for(Integer laboratoryid:uniqeLaboratories) {
			List<LabTest> testobj=labTestService.findLabTestByLabId(laboratoryid);
			int count=0;
			for(LabTest t:testobj) {
				if(labtestlist.contains(t)) 
					count++;
			}
			if(count==labtestlist.size()) 
				availableLaboratories.add(laboratoryService.getLaboratoryById(laboratoryid));
		}
		if(availableLaboratories.size()==0) 
			model.addAttribute("nodata","No Laboratories for the given tests");
		Appointment appointment=appointmentService.getAppointmentById(appointmentId);
		model.addAttribute("labtestlist",labtestlist);
		model.addAttribute("appointment",appointment);
		model.addAttribute("availablelaboratories",availableLaboratories);
		return"labtestlistbyappointment";
	}
	@GetMapping("/laboratory/{appointmentId}/{laboratoryId}")
	public String laboratorybyid(Model model,@PathVariable("appointmentId") int appointmentId, @PathVariable("laboratoryId") int laboratoryId,Laboratory laboratoryobj, HttpServletRequest request) {
		laboratoryobj=laboratoryService.getLaboratoryById(laboratoryId);
		List<LabTest> testobj=labTestService.findLabTestByLabId(laboratoryId);
		Appointment appointment=appointmentService.getAppointmentById(appointmentId);
		model.addAttribute("laboratory",laboratoryobj);
		model.addAttribute("labtestlist",testobj);
		model.addAttribute("appointment",appointment);
		return"laboratorybooking";
	}
	@GetMapping("/booktests")
	public String bookingtests(Model model,@RequestParam("appointmentId") int appointmentId,@RequestParam("laboratoryId") int laboratoryId, @RequestParam("testId") String testId,String[] testid, HttpServletRequest request,HttpSession session) {
		log.debug("+labId+="+laboratoryId);
		testid=testId.split(",");
		List<LabTest> labtestlist=new ArrayList<>();
		double amount=0;
		for(String s:testid) {
			LabTest lb=labTestService.getLabTestById(Integer.parseInt(s));
			labtestlist.add(lb);
			amount=amount+lb.getPrice();
		}
		int patientId= (int) session.getAttribute("pId");
		Patients patientObj=patientsService.getPatientById(patientId);
		Laboratory laboratory=laboratoryService.getLaboratoryById(laboratoryId);
		Appointment appointment=appointmentService.getAppointmentById(appointmentId);
		HospitalBranch hospitalBranch=hospitalBranchService.getHospitalbranchId(appointment.getHospitalBranchId());
		model.addAttribute("hospital",hospitalBranch);
		model.addAttribute("appointment",appointment);
		model.addAttribute("patient",patientObj);
		model.addAttribute("labtestlist",labtestlist);
		model.addAttribute("totalprice",amount);
		model.addAttribute("laboratory",laboratory);
		return"testbookingform";
	}
	@RequestMapping(value="/updateAppointment/{appointmentId}",method=RequestMethod.GET)
	public String updateAppointmentforLaboratory(Model model,@PathVariable("appointmentId") int appointmentId,@RequestParam("laboratoryId") int laboratoryId) {
		Appointment app=appointmentService.getAppointmentById(appointmentId);
		Laboratory lab=laboratoryService.getLaboratoryById(laboratoryId);
		app.setLaboratory(lab);
		appointmentService.addAppointment(app);
		log.debug(app.getLaboratory().getLabId());
		log.debug("app.getLaboratory().getLabId"+laboratoryId);
		String[] testid=app.getLabTestId().split(",");
		List<LabTest> labtestlist=new ArrayList<>();
		double amount=0;
		for(String s:testid) {
			LabTest lb=labTestService.getLabTestById(Integer.parseInt(s));
			labtestlist.add(lb);
			amount=amount+lb.getPrice();
		}
		Patients patientObj=patientsService.getPatientById(app.getPatient().getPatientId());
		Laboratory laboratory=laboratoryService.getLaboratoryById(laboratoryId);
		Appointment appointment=appointmentService.getAppointmentById(app.getAppointmentId());
		HospitalBranch hospitalBranch=hospitalBranchService.getHospitalbranchId(appointment.getHospitalBranchId());
		model.addAttribute("hospital",hospitalBranch);
		model.addAttribute("appointment",appointment);
		model.addAttribute("patient",patientObj);
		model.addAttribute("labtestlist",labtestlist);
		model.addAttribute("totalprice",amount);
		model.addAttribute("laboratory",laboratory);
		return "testbookingform";
	}
}
