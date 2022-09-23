package com.vcare.controller;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.vcare.beans.Aboutus;
import com.vcare.service.AboutusService;

@Controller
@RequestMapping("/Aboutus")
public class AboutusController {

	@Autowired
	AboutusService aboutusService;

	static Logger log = Logger.getLogger(AboutusController.class.getClass());

	@GetMapping("/about")
	public String getAllAboutus(Model model, Aboutus aboutus) {
		List<Aboutus> listAbout = aboutusService.getAllAboutus();
		model.addAttribute("aboutlist", listAbout);
		return "aboutlist";
	}
	@RequestMapping(value = ("/addabout"), method = RequestMethod.GET)
	public String aboutusAddingForm(Model model, @ModelAttribute(value = "aboutObj") Aboutus aboutus) {
		log.debug("department form ");
		model.addAttribute("departmentObj", aboutus);
		return "aboutform";
	}
	@RequestMapping(value = "/saveAbout", method = RequestMethod.POST)
	public String saveAbout(Model model, @ModelAttribute(value = "aboutObj") Aboutus aboutus) {
		aboutusService.updateAboutus(aboutus);
		model.addAttribute("aboutObj", aboutus);
		return "redirect:/Aboutus/about";
	}
	@GetMapping("/editAbout/{aboutId}")
	public String editAboutById(Model model, @PathVariable("aboutId") int aboutId) {
		Aboutus objSecAbout = aboutusService.getAboutusById(aboutId);
		log.debug("Edit of department in ...");
		log.debug("inside getHospitalbranchId id is:::" + aboutusService.getAboutusById(aboutId));
		model.addAttribute("aboutObj", objSecAbout);
		return "aboutform";
	}
	@GetMapping("/deleteAbout/{aboutId}")
	public String deleteAboutus(Model model, @PathVariable("aboutId") int aboutId) {
		log.debug("inside deleteHospitalBranch id:::" + aboutId);
		Aboutus inactive = aboutusService.getAboutusById(aboutId);
		inactive.setIsActive('N');
		aboutusService.updateAboutus(inactive);
		return "redirect:/Aboutus/about";
	}
}
