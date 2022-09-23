package com.vcare.controller;

import java.io.IOException;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vcare.beans.Admin;
import com.vcare.beans.Insurance;
import com.vcare.service.AdminService;
import com.vcare.service.InsuranceService;

@Controller
@RequestMapping("/Insurance")
public class InsuranceController<ModelAndView> {

	@Autowired
	InsuranceService insuranceService;
	@Autowired
	private AdminService adminService;
	
	static Logger log=Logger.getLogger(InsuranceController.class.getClass());

	@RequestMapping(value = "addInsurance/{adminid}", method = RequestMethod.GET)
	public String newInsurance(Model model, @ModelAttribute(value = "insurance") Insurance insurance,
			@PathVariable("adminid") int adminid) {
		Admin adminobj = adminService.getadminById(adminid);
		int a = adminobj.getAdminId();
		String name = adminobj.getName();
		model.addAttribute("admin", a);
		model.addAttribute("adminname", name);

		model.addAttribute("insurance", insurance);
		return "insuranceform";
	}
	@PostMapping("/saveInsurance/{adminid}")
	public String saveInsurance(Model model, @RequestParam("file") MultipartFile file,
			Insurance insurance, @PathVariable("adminid")int adminid,HttpServletRequest request) throws IOException {
		Admin adminobj = adminService.getadminById(adminid);
		model.addAttribute("admin", adminobj);
		insurance.setCreatedBy(adminobj.getAdminId());
		insurance.setInsurencepic(Base64.getEncoder().encodeToString(file.getBytes()));
		log.info(insurance.getInsuranceName());
		HttpSession session=request.getSession();
		if(insurance.getInsuranceId()!=0) {
			session.setAttribute("inmsg", "inmsg");
			session.setAttribute("name", insurance.getInsuranceName());
		}else {
			session.setAttribute("adminList", "List");
			session.setAttribute("InName", insurance.getInsuranceName());
		}
		insuranceService.saveInsurance(insurance);
		return "redirect:/Insurance/insurancelist/{adminid}";
	}
	@RequestMapping("/insurancelist/{adminid}")
	public String insuranceList(Model model, Insurance insurance, @PathVariable("adminid") int adminid,HttpServletRequest request) {
		Admin adminobj = adminService.getadminById(adminid);
		model.addAttribute("admin", adminobj);
		List<Insurance> list = insuranceService.getAllActiveInsurance();
		model.addAttribute("insurancelist", list);
		HttpSession session=request.getSession();
		if(session.getAttribute("adminList")=="List") {
			model.addAttribute("adminmsg", session.getAttribute("InName")+"Insurance Added");
			session.setAttribute("adminList", "");
			session.setAttribute("InName", "");
		}
		if(session.getAttribute("inmsg")=="inmsg") {
			model.addAttribute("adminmsg",session.getAttribute("name")+"updated");
			session.setAttribute("name", "");
			session.setAttribute("inmsg", "");
		}
		if(session.getAttribute("indelete")=="indelete") {
			model.addAttribute("adminmsg", "Successfully deleted");
			session.setAttribute("indelete", "");
		}
		return "insurancelist";
	}

	@RequestMapping("deleteInsurance/{adminid}/{insuranceid}")
	public String deleteInsurance(Model model, @PathVariable int insuranceid, @PathVariable int adminid, Insurance insurance,HttpServletRequest request) {
		Admin adminobj = adminService.getadminById(adminid);
		model.addAttribute("admin", adminobj);
		insuranceService.deleteInsuranceById(insuranceid);
		HttpSession session =request.getSession();
		session.setAttribute("indelete", "indelete");
		return "redirect:/Insurance/insurancelist/{adminid}";
	}

	@GetMapping("/editInsurance/{adminid}/{insuranceid}")
	public String editInsuranceById(Model model, @PathVariable("adminid") int adminId, @PathVariable("insuranceid") int insuranceid) {
		Insurance insurance = insuranceService.getInsuranceById(insuranceid);
		model.addAttribute("insurance", insurance);
		model.addAttribute("insuranceId", insuranceid);
		Admin adminobj = adminService.getadminById(adminId);
		model.addAttribute("admin", adminobj);
		int a = adminobj.getAdminId();
		String name = adminobj.getName();
		model.addAttribute("admin", a);
		model.addAttribute("adminname", name);
		log.info("inside getinsuranceById id is:::" + insurance.getInsuranceId());
		return "insuranceform";
	}
	@GetMapping("/insurance/{insuranceid}")
	public String viewinsuranceById(Model model, @PathVariable("insuranceid") int insuranceid) {
		Insurance insurance = insuranceService.getInsuranceById(insuranceid);
		model.addAttribute("insurancename", insurance.getInsuranceName());
		model.addAttribute("description", insurance.getInsuranceDescription());
		model.addAttribute("image", insurance.getInsurencepic());
		model.addAttribute("termscondition", insurance.getTermsconditions());
		return "insurancescreens";
	}
}
