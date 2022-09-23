package com.vcare.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.vcare.beans.Department;
import com.vcare.beans.HospitalBranch;
import com.vcare.repository.HospitalBranchRepository;
import com.vcare.repository.HospitalRepository;
import com.vcare.service.DepartmentService;
import com.vcare.service.DepartmentServiceImpl;
import com.vcare.service.HospitalBranchService;

@Controller
@RequestMapping("/Department")
public class DepartmentController {

	@Autowired
	DepartmentService departmentService;
	@Autowired
	DepartmentServiceImpl departmentServiceImpl;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	HospitalRepository hospitalRepository;
	@Autowired
	HospitalBranchRepository branchRepository;

	static Logger log = Logger.getLogger(DepartmentController.class.getClass());

	@GetMapping("/departmentlist/{hospitalbranchId}")
	public String getAllDepartments(Model model, @PathVariable("hospitalbranchId") int hospitalbranchId, Department objDepart,HttpServletRequest request) {
		List<Department> depList = departmentServiceImpl.getAllDepartment(hospitalbranchId);
		log.debug("list of all departments");
		HttpSession session=request.getSession();
		if(session.getAttribute("Depadd")=="Depadd") {
			model.addAttribute("adminmsg",session.getAttribute("Depname")+ "\t Department \t Added");
			session.setAttribute("Depadd", "");
			session.setAttribute("Depname", "");
		}
		if(session.getAttribute("Depup")=="Depup") {
			model.addAttribute("adminmsg",session.getAttribute("Depname")+"\t Department \tUpdated" );
			session.setAttribute("Depup", "");
			session.setAttribute("Depname", "");
		}
		if(session.getAttribute("Depdelete")=="Depdelete") {
			model.addAttribute("adminmsg", "\t Successfully deleted");
		}
		model.addAttribute("departmentList", depList);
		HospitalBranch objBranch = hospitalBranchService.getHospitalbranchId(hospitalbranchId);
		model.addAttribute("branch", objBranch.getHospitalBranchId());
		model.addAttribute("objName", objDepart.getDepartmentName());
		model.addAttribute("objId", objDepart.getDepartmentId());
		return "departmentlist";
	}

	@RequestMapping(value = "/addDepartment", method = RequestMethod.GET)
	public String addDepartments(Model model, @ModelAttribute(value = "departmentObj") Department objDepartment) {
		log.debug("department form ");
		model.addAttribute("departmentObj", objDepartment);
		Map<Integer, String> BranchMap = dropDownBranch();
		model.addAttribute("BranchMap", BranchMap);
		model.addAttribute("branchId", BranchMap.keySet());
		model.addAttribute("branch", branchRepository.hospitalBranches());
		return "departmentform";
	}

	@RequestMapping(value = "/saveDepartment", method = RequestMethod.POST)
	public String saveDepartment(Model model, @ModelAttribute(value = "departmentObj") Department objDepartment,HttpServletRequest request) {
		log.debug("inside deleteDepartment id:::" + objDepartment.getDepartmentId());
		log.debug("inside deleteDepartment name:::" + objDepartment.getDepartmentName());
		log.debug("inside deleteDepartment name:::" + objDepartment.getDescription());
		model.addAttribute("departmentObj", objDepartment);
		departmentService.updateDepartment(objDepartment);
		HttpSession session=request.getSession();
		session.setAttribute("Depadd", "Depadd");
		session.setAttribute("Depname", objDepartment.getDepartmentName());
		if(objDepartment.getDepartmentId()!=0) {
			session.setAttribute("Depup", "Depup");
			session.setAttribute("Depname", objDepartment.getDepartmentName());
		}
		return "redirect:/Department/departmentlist/" + objDepartment.getHospitalBranch().getHospitalBranchId();
	}

	@GetMapping("/editDepartment/{departmentId}")
	public String editDepartmentById(Model model, @PathVariable("departmentId") int departmentId) {
		Department objSecDepartment = departmentService.getDepartmentById(departmentId);
		log.debug("Edit of department in ...");
		model.addAttribute("departmentObj", objSecDepartment);
		Map<Integer, String> BranchMap = dropDownBranch();
		model.addAttribute("BranchMap", BranchMap);
		model.addAttribute("branchId", BranchMap.keySet());
		model.addAttribute("branch", branchRepository.hospitalBranches());
		return "departmentform";
	}
	@GetMapping("/deleteDepartment/{id}")
	public String deleteDepartment(Model model, @PathVariable int id,HttpServletRequest request) {
		log.debug("inside deleteHospitalBranch id:::" + id);
		Department inactive = departmentService.getDepartmentById(id);
		inactive.setIsactive('N');
		departmentService.updateDepartment(inactive);
		HttpSession session=request.getSession();
		session.setAttribute("Depdelete", "Depdelete");
		return "redirect:/Department/departmentlist/"+inactive.getHospitalBranch().getHospitalBranchId();
	}
	public Map<Integer, String> dropDownBranch() {
		Map<Integer, String> BranchMap = new HashMap<Integer, String>();
		List<HospitalBranch> branchList = branchRepository.hospitalBranches();
		for (HospitalBranch branchobj : branchList) {
			BranchMap.put(branchobj.getHospitalBranchId(), branchobj.getHospitalBranchName());
		}
		return BranchMap;
	}
}
