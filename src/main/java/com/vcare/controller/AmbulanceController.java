package com.vcare.controller;

import java.util.List;
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
import com.vcare.beans.Ambulance;
import com.vcare.beans.HospitalBranch;
import com.vcare.service.AmbulanceImplementation;
import com.vcare.service.AmbulanceService;
import com.vcare.service.HospitalBranchService;

@Controller
@RequestMapping("/Ambulance")
public class AmbulanceController {

	@Autowired
	AmbulanceService ambulaceservice;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	AmbulanceImplementation ambulanceImplementation;
	static Logger log = Logger.getLogger(AmbulanceController.class.getClass());

	@GetMapping("/AmbulanceList/{hospitalbranchId}")
	public String getAllAmbulance(Model model, @PathVariable("hospitalbranchId") int hospitalbranchId, HttpServletRequest request) {
		List<Ambulance> Lists = ambulaceservice.hospitalAmbulanceDetails(hospitalbranchId);
		log.debug("============Ambulance List======================");
		HttpSession session = request.getSession();
		if (session.getAttribute("Ambadd") == "Ambadd") {
			model.addAttribute("adminmsg", session.getAttribute("Ambname") + "\t Ambulance\t Added");
			session.setAttribute("Ambadd", "");
			session.setAttribute("Anbname", "");
		}
		if (session.getAttribute("Ambup") == "Ambup") {
			model.addAttribute("adminmsg", session.getAttribute("Ambname") + "\t Ambulance \t Updated");
			session.setAttribute("Ambup", "");
			session.setAttribute("Anbname", "");
		}
		if (session.getAttribute("Ambdelete") == "Ambdelete") {
			model.addAttribute("adminmsg", "Ambulance\t Successfully\t Deleted");
			session.setAttribute("Ambdelete", "");
		}
		model.addAttribute("hbid", hospitalbranchId);
		model.addAttribute("ambulanceList", Lists);
		return "ambulanceList";
	}

	@GetMapping("/addAmbulance/{branchId}")
	public String showNewAmbulanceForm(Model model, Ambulance ambulance, @PathVariable("branchId") int branchId) {
		log.debug("============Ambulance Form======================");
		HospitalBranch hospitalbranch = hospitalBranchService.getHospitalbranchId(branchId);
		model.addAttribute("branchId", hospitalbranch.getHospitalBranchId());
		model.addAttribute("ambulanceobj", ambulance);
		return "ambulanceForm";
	}
	@RequestMapping(value = "/saveAmbulance/{hbrnchId}", method = RequestMethod.POST)
	public String addAmbulance(Model model, Ambulance ambulance, @PathVariable("hbrnchId") int hbrnchId,
			HttpServletRequest request) {
		log.debug("============ Save Ambulance Method ======================");
		model.addAttribute("ambulanceobj", ambulance);
		model.addAttribute("branchId", hbrnchId);
		ambulaceservice.addAmbulance(ambulance);
		HttpSession session = request.getSession();
		session.setAttribute("Ambname", ambulance.getAmbulanceType());
		if (ambulance.getAmbulanceId() != 0) {
			session.setAttribute("Ambup", "Ambup");
			session.setAttribute("Ambname", ambulance.getAmbulanceType());
		} else {
			session.setAttribute("Ambadd", "Ambadd");
		}
		return "redirect:/Ambulance/AmbulanceList/" + ambulance.getHospitalbranch().getHospitalBranchId();
	}
	@GetMapping("deleteAmbulance/{ambulanceid}")
	public String deleteAmbulance(Model model, @PathVariable("ambulanceid") int ambulanceId,
			HttpServletRequest request) {
		log.debug("============ Delete Ambulance Method ======================");
		Ambulance as = ambulaceservice.getById(ambulanceId);
		as.setIsActive('N');
		HttpSession session = request.getSession();
		session.setAttribute("Ambdelete", "Ambdelete");
		return "redirect:/Ambulance/AmbulanceList/" + as.getHospitalbranch().getHospitalBranchId();
	}
	@GetMapping("editAmbulance/{ambulanceid}")
	public String editAmbulanceById(Model model, @PathVariable("ambulanceid") int ambulanceId) {
		log.debug("============ Edit Ambulance Method ======================");
		Ambulance ambulance = ambulaceservice.getById(ambulanceId);
		model.addAttribute("ambulanceobj", ambulance);
		log.debug("inside getHospitalbranchId id is:::" + ambulaceservice.getById(ambulanceId));
		model.addAttribute("branchId", ambulance.getHospitalbranch().getHospitalBranchId());
		ambulaceservice.UpdateAmbulance(ambulance);
		return "ambulanceForm";
	}
}
