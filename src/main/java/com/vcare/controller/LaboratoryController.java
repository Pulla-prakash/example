package com.vcare.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.vcare.beans.Hospital;
import com.vcare.beans.Laboratory;
import com.vcare.repository.LaboratoryRepository;
import com.vcare.service.HospitalService;
import com.vcare.service.LaboratoryService;
import com.vcare.utils.MailUtil;
import com.vcare.utils.VcareUtilies;

@Controller
@RequestMapping("/laboratory")
public class LaboratoryController {
	@Autowired(required=true)
	LaboratoryService laboratoryService;
	@Autowired
	LaboratoryRepository laboratoryRepository;
	@Autowired
	HospitalService hospitalService;
	@Autowired
	JavaMailSender mailSender;
	@GetMapping("/laboratoryList")
	public String getLaboratoryById(Model model) {
		List<Laboratory> labList=laboratoryService.getAllActiveLaboratory();
		model.addAttribute("labList", labList);
		return "laboratoryList";
	}
	@RequestMapping(value="/addLab", method= RequestMethod.GET)
	public String addLaboratory(Model model,Laboratory laboratory) {
	List<Hospital> hosp=hospitalService.getAllHospitals();
		model.addAttribute("lab", laboratory);
		model.addAttribute("hospital", hosp.get(0).getHospitalId());
		return "laboratoryForm";
	}
	@RequestMapping(value="/saveLab",method=RequestMethod.POST)
	public String saveLaboratory(Model model,Laboratory laboratory) {
		List<Laboratory> list = laboratoryService.getAllActiveLaboratory();
		model.addAttribute("patientlist", list);
		String labObj1 = laboratoryService.validateduplicate(laboratory.getEmail());
		if (labObj1 == null) {
			MailUtil.sendSimpleEmail(mailSender, laboratory.getEmail(),"Login creadentials here : "
	                +"Email&password"+" "+"UserMailId="+laboratory.getEmail()+"&password="+laboratory.getPassword(),"login credentials");
			model.addAttribute("laboratory", laboratory);
			String strEncPassword = VcareUtilies.getEncryptSecurePassword(laboratory.getPassword(), "vcare");
			laboratory.setPassword(strEncPassword);
			laboratoryService.addLaboratory(laboratory);
			return "redirect:/laboratory/laboratoryList";
					}
		return "redirect:/";
		
		}
	@RequestMapping(value="/editLab/{laboratoryId}",method=RequestMethod.GET)
	public String editLaboratoryByLabId(Model model,@PathVariable("laboratoryId") int laboratoryId,Laboratory laboratory) {
		laboratory=laboratoryService.getLaboratoryById(laboratoryId);
		model.addAttribute("hospital",hospitalService.getAllHospitals());
		model.addAttribute("laboratory", laboratory);
		model.addAttribute("labId", laboratoryId);
		return "laboratoryForm";
	}
	@RequestMapping(value="/deleteLab/{laboratoryId}",method=RequestMethod.GET)
	public String deleteLaboratoryByLabId(Model model,@PathVariable("laboratoryId") int laboratoryId ) {
		laboratoryService.deleteLaboratory(laboratoryId);
		model.addAttribute("labList", laboratoryService.getAllActiveLaboratory());
		return "redirect:/laboratory/laboratoryList";
	}
	@Value("${app.name}")
	String applicationName;
	@GetMapping("/labvalid")
	public String LaboratoryValidation(Model model, @ModelAttribute("laboratory") Laboratory laboratory, @RequestParam("givenCaptcha") String givenCaptcha,@RequestParam("userCaptcha") String userCaptcha,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!givenCaptcha.equals(userCaptcha)) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
		String str = VcareUtilies.getEncryptSecurePassword(laboratory.getPassword(), applicationName);
		Laboratory lab=laboratoryRepository.findByMailIdIgnoreCaseAndPassword(laboratory.getEmail(), str);
		if (lab != null) {
			session.setAttribute("labId", lab.getLabId());
			model.addAttribute("labId", lab.getLabId());
			return "laboratorydashboard";
		} else {
			session.setAttribute("indexmsg","logininvalidpasswordorusername");
			return "redirect:/";
		}
	}
}
