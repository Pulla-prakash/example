package com.vcare.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.vcare.beans.Ambulance;
import com.vcare.beans.AmbulanceDriverAssosiation;
import com.vcare.beans.Department;
import com.vcare.beans.Employees;
import com.vcare.beans.HospitalBranch;
import com.vcare.repository.AmbulanceDriverRepository;
import com.vcare.repository.AmbulanceRepository;
import com.vcare.repository.DepartmentRepository;
import com.vcare.repository.EmployeesRepository;
import com.vcare.service.AmbulanceDriverService;
import com.vcare.service.AmbulanceService;
import com.vcare.service.EmployeesService;
import com.vcare.service.HospitalBranchService;

@Controller
@RequestMapping("/AmbulanceDriverController")
public class AmbulanceDriverController {

	@Autowired
	AmbulanceDriverService ambulanceDriverService;
	@Autowired
	AmbulanceService ambulanceservice;
	@Autowired
	EmployeesService employeeService;
	@Autowired
	AmbulanceDriverRepository ambdriversrepository;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	DepartmentRepository departmentRepository;
	@Autowired
	EmployeesRepository employeesRepository;
	@Autowired
	AmbulanceRepository ambulancerepo;
	
	static Logger log= Logger.getLogger(AmbulanceDriverController.class.getClass());

	@GetMapping("/AmbulanceDriverList/{hospitalbranchId}")
	public String getAllAmbdriver(Model model, @PathVariable("hospitalbranchId") String hospitalbranchId, HttpServletRequest request) {
		List<AmbulanceDriverAssosiation> AmbdriverList = ambdriversrepository.ambulaceDriverActiveList(Integer.parseInt(hospitalbranchId));
		HttpSession session = request.getSession();
		if (session.getAttribute("Driadd") == "Driadd") {
			model.addAttribute("adminmsg", session.getAttribute("Driname") + "\tAmbulance \t Added");
			session.setAttribute("Driadd", "");
		}
		if (session.getAttribute("Driup") == "Driup") {
			model.addAttribute("adminmsg", session.getAttribute("Driname") + "\t Ambulance \t updated");
			session.setAttribute("Driup", "");
			session.setAttribute("Driname", "");
		}
		if (session.getAttribute("Dridelete") == "Dridelete") {
			model.addAttribute("adminmsg", "\t Successfully deleted");
			session.setAttribute("Dridelete", "");
		}
		model.addAttribute("ambdriverlist", AmbdriverList);
		return "ambulancedriverlist";
	}
	@GetMapping("/AddAmbulanceDriver/{hospitalbranchId}")
	public String showNewdAmbulanceDriverForm(Model model, AmbulanceDriverAssosiation ambulancedriverobj,
			@PathVariable("hospitalbranchId") int hospitalbranchId, HospitalBranch hospitalBranch, HttpServletRequest request) {
		HospitalBranch hospitalbranch = hospitalBranchService.getHospitalbranchId(hospitalbranchId);
		log.debug("Hospital Branch Id" + hospitalbranchId);
		model.addAttribute("HbranchId", hospitalbranch.getHospitalBranchId());
		log.debug("branchid::::::::::::::::" + hospitalBranch.getHospitalBranchEmailId());
		log.debug("this method is implemented");
		List<Ambulance> AmbList = ambulancerepo.getambbyBranchList(hospitalbranchId);
		model.addAttribute("ambulanceList", AmbList);
		Department deptObj = departmentRepository.dept(hospitalbranchId);
		List<Employees> empdriverlist = employeesRepository.empDriverList(deptObj.getDepartmentId());
		model.addAttribute("empdriverlist", empdriverlist);
		return "ambulancedriverform";
	}
	@PostMapping("/saveAmbulanceDriver")
	public String saveAmbulanceDriver(Model model, AmbulanceDriverAssosiation ambulancedriverobj,
			HospitalBranch hospitalBranch, HttpServletRequest request) {
		ambulancedriverobj.setStatus("available");
		log.debug("save method2");
		HttpSession session = request.getSession();
		session.setAttribute("Driname", ambulancedriverobj.getAmbulancetype());
		session.setAttribute("brnchid", ambulancedriverobj.getHospitalbranch().getHospitalBranchId());
		if (ambulancedriverobj.getAmbdriversId() != 0) {
			session.setAttribute("Driup", "Driup");
			session.setAttribute("Driname", ambulancedriverobj.getAmbulancetype());
		} else {
			session.setAttribute("Driadd", "Driadd");
		}
		ambulanceDriverService.addAmbulancedriver(ambulancedriverobj);
		return "redirect:/AmbulanceDriverController/AmbulanceDriverList/" + ambulancedriverobj.getHospitalbranch().getHospitalBranchId();
	}
	@GetMapping("/deleteAmbulancedriver/{ambulanceDriverId}")
	public String deleteAmbulanceDriver(Model model, @PathVariable("ambulanceDriverId") int ambulanceDriverId,
			HttpServletRequest request) {
		AmbulanceDriverAssosiation inactive = ambulanceDriverService.getAmbdriverId(ambulanceDriverId);
		inactive.setIsactive('N');
		HttpSession session = request.getSession();
		session.setAttribute("Dridelete", "Dridelete");
		ambulanceDriverService.addAmbulancedriver(inactive);
		return "redirect:/AmbulanceDriverController/AmbulanceDriverList/" + inactive.getHospitalbranch().getHospitalBranchId();
	}
	@GetMapping("/editAmbulancedriver/{ambulanceDriverId}")
	public String getAmbulancedriverById(Model model, @PathVariable("ambulanceDriverId") int ambulanceDriverId) {
		AmbulanceDriverAssosiation ambulancedriverob = ambulanceDriverService.getById(ambulanceDriverId);
		model.addAttribute("ambulancedriverobj", ambulancedriverob);
		int brnchid = ambulancedriverob.getHospitalbranch().getHospitalBranchId();
		HospitalBranch hospitalbranch = hospitalBranchService.getHospitalbranchId(brnchid);
		log.debug("fhjefbwegjhw" + brnchid);
		model.addAttribute("HbranchId", hospitalbranch.getHospitalBranchId());
		log.debug("this method is implemented");
		List<Ambulance> AmbList = ambulancerepo.getambbyBranchList(brnchid);
		model.addAttribute("ambulanceList", AmbList);
		Department deptObj = departmentRepository.dept(brnchid);
		List<Employees> empdriverlist = employeesRepository.empDriverList(deptObj.getDepartmentId());
		model.addAttribute("empdriverlist", empdriverlist);
		return "ambulancedriverform";
	}
	@GetMapping("/selectambulancedriver/{hbranchId}/{empid}")
	public String selectAmbulancedriver(Model model, AmbulanceDriverAssosiation ambulancedriverob,
			@PathVariable("hbranchId") String hbranchId, @PathVariable("empid") int empid) {
		List<AmbulanceDriverAssosiation> AmbdriverList = ambdriversrepository
				.ambdriverList(Integer.parseInt(hbranchId));
		model.addAttribute("ambdriverlist", AmbdriverList);
		model.addAttribute("hbranchId", hbranchId);
		model.addAttribute("employee", empid);
		return "ambulanceselection";
	}
	@GetMapping("/changestatus/{id}/{ambdriversId}")
	public String changestatusById(Model model, @PathVariable("ambdriversId") int ambdriversId, @PathVariable("id") int id) {
		AmbulanceDriverAssosiation ambulancedriverob = ambulanceDriverService.getById(ambdriversId);
		HospitalBranch hospitalbranch = hospitalBranchService.getHospitalbranchId(id);
		model.addAttribute("hbranchId", hospitalbranch.getHospitalBranchId());
		ambulancedriverob.setStatus("available");
		ambulanceDriverService.addAmbulancedriver(ambulancedriverob);
		model.addAttribute("HbranchId", hospitalbranch.getHospitalBranchId());
		List<AmbulanceDriverAssosiation> ambdriverlist = ambulanceDriverService.getAllAambulancedriver();
		model.addAttribute("ambdriverlist", ambdriverlist);
		return "ambulanceselection";
	}
}
