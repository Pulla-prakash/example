package com.vcare.controller;

import java.io.IOException;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.vcare.beans.popularOffers;
import com.vcare.repository.popularOfferRepository;
import com.vcare.service.popularOfferService;

@Controller
@RequestMapping("/popularoffer")
public class popularoffercontroller {
	
	@Autowired
	popularOfferService offerService;
	@Autowired
	popularOfferRepository popularOfferRepository;
	
	static Logger log=Logger.getLogger(popularoffercontroller.class.getClass());

	@RequestMapping(value = ("/addpopularoffer"), method = RequestMethod.GET)
	public String addServices(Model model, @ModelAttribute(value = "objoffers") popularOffers  popularoffers) {
		model.addAttribute("popularoffers", popularoffers);
		return "popularofferform";
	}
	@RequestMapping(value = "/saveoffer", method = RequestMethod.POST)
	public String saveServices(Model model, @ModelAttribute(value = "popularoffers")popularOffers  popularoffers,@RequestParam("file") MultipartFile file) throws IOException {
		model.addAttribute("popularoffers", popularoffers);
		popularoffers.setImage(Base64.getEncoder().encodeToString(file.getBytes()));
		offerService.addpopularOffers(popularoffers);
		List<popularOffers> offerList = offerService.getAllOffers();
		model.addAttribute("offerlist", offerList);
		return "redirect:/popularoffer/offerlist";
	}
	@GetMapping("/offerlist")
	public String getAllServices(Model model, popularOffers  popularoffers) {
		List<popularOffers> listoffer =  offerService.getAllPopularOffer();
		listoffer.forEach(a->log.debug(a.getIsactive()));
		model.addAttribute("offerlist", listoffer);
		return "popularofferlist";
	}
	 @GetMapping("/editoffer/{offerid}") 
	 public String editServiceById(Model model, @PathVariable("offerid") int id) {
	      popularOffers  objoffers = offerService.getoOfferById(id);
	      offerService.getoOfferById(id);
	       model.addAttribute("popularoffers", objoffers);
	       return "popularofferform";
	       }
	       @GetMapping("/deleteoffer/{offerid}")
	       public String deleteService(Model model, @PathVariable("offerid") int id) {
	       popularOffers objofferservice = offerService.getoOfferById(id);
	      objofferservice.setIsactive('N');
	      offerService.addpopularOffers(objofferservice);
	        List<popularOffers> listoffer = offerService.getAllOffers();
	        model.addAttribute("offerlist", listoffer);
	       return "redirect:/popularoffer/offeerlist";
	       }
}
