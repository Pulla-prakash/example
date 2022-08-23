package com.vcare.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vcare.beans.Aboutus;
import com.vcare.beans.ContractEmployees;
import com.vcare.beans.popularOffers;
import com.vcare.service.popularOfferService;

@Controller
public class popularoffercontroller {
	
	
	@Autowired
	
	popularOfferService offerService;
	
	
	

	@RequestMapping(value = ("/addpopularoffer"), method = RequestMethod.GET)
	public String newAbout(Model model, @ModelAttribute(value = "objoffers") popularOffers  popularoffers) {
		
		model.addAttribute("popularoffers", popularoffers);

		return "popularofferform";

	}
	
	

	@RequestMapping(value = "/saveoffer", method = RequestMethod.POST)
	public String addDepartment(Model model, @ModelAttribute(value = "popularoffers")popularOffers  popularoffers,@RequestParam("file") MultipartFile file) throws IOException {
		//aboutus.setIsActive('Y');
		model.addAttribute("popularoffers", popularoffers);
		popularoffers.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
		offerService.addpopularOffers(popularoffers);
		List<popularOffers> offerList = offerService.getAllOffers();
		model.addAttribute("offerlist", offerList);
		return "redirect:/offeerlist";
	}
	
	
	@GetMapping("/offeerlist")
	public String getAllAbouts(Model model, popularOffers  popularoffers) {

		List<popularOffers> listoffer = offerService.getAllOffers();

	//	List<popularOffers> list = new ArrayList<>();
//		for (Aboutus about : listAbout) {
//			if (about.getIsActive() == 'Y' || about.getIsActive() == 'y') {
//				list.add(about);
//			}
//		}

		model.addAttribute("offerlist", listoffer);
		return "popularofferlist";
	}

	

	/*
	 * @GetMapping("/editAbout/{id}") public String getById(Model
	 * model, @PathVariable("id") int id) {
	 * 
	 * Aboutus objSecAbout = offerService.getAboutusById(id);
	 * log.info("Edit of department in ...");
	 * log.info("inside getHospitalbranchId id is:::" +
	 * aboutusService.getAboutusById(id)); model.addAttribute("aboutObj",
	 * objSecAbout);
	 * 
	 * return "aboutform";
	 * 
	 * }
	 * 
	 * @GetMapping("/deleteAbout/{id}") public String deleteService(Model
	 * model, @PathVariable int id) { log.info("inside deleteHospitalBranch id:::" +
	 * id);
	 * 
	 * Aboutus inactive = aboutusService.getAboutusById(id);
	 * inactive.setIsActive('N'); aboutusService.updateAboutus(inactive);
	 * List<Aboutus> aboutList = aboutusService.getAllAbouts();
	 * model.addAttribute("aboutList", aboutList);
	 * 
	 * return "redirect:/about";
	 * 
	 * }
	 */

}
