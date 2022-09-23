package com.vcare.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.vcare.beans.AmbulanceDriverAssosiation;
import com.vcare.beans.BookingAmbulance;
import com.vcare.beans.HospitalBranch;
import com.vcare.service.AmbulanceDriverService;
import com.vcare.service.BookingAmbulanceService;
import com.vcare.service.HospitalBranchService;

@Controller
@RequestMapping("/bookingambulance")
public class BookingAmbulanceController {
	@Autowired
	BookingAmbulanceService bookingAmbService;
	@Autowired
	AmbulanceDriverService ambulanceDriverService;
	@Autowired
	HospitalBranchService hospitalBranchService;

	static Logger log=Logger.getLogger(BookingAmbulanceController.class.getClass());
	
	@GetMapping("/BookingList/{hospitalbranchId}")
	public String getAllAmbulanceBookings(Model model, BookingAmbulance BookingAmbulance, @PathVariable("hospitalbranchId") int hospitalbranchId) {
		List<BookingAmbulance> Bookinglist = bookingAmbService.getAllActiveAmbulanceBookings();
		HospitalBranch hosptlbrnc = hospitalBranchService.getHospitalbranchId(hospitalbranchId);
		model.addAttribute("hosptlbrnc", hosptlbrnc.getHospitalBranchId());
		model.addAttribute("Bookingambulancelist", Bookinglist);
		return "BookingAmblist";

	}
	@RequestMapping(value = ("/bookingform/{branchid}/{ambid}/{empid}"), method = RequestMethod.GET)
	public String showNewAmbulanceBookingForm(Model model, @PathVariable("ambid") int ambid, @PathVariable("branchid") int branchid,
			BookingAmbulance BookingAmbulance, @PathVariable("empid") int empid) {
		log.debug("::::::::::::::" + ambid);
		AmbulanceDriverAssosiation ambulancedriverob = ambulanceDriverService.getById(ambid);
		HospitalBranch hospitalbranch = hospitalBranchService.getHospitalbranchId(branchid);
		model.addAttribute("hbranchId", hospitalbranch.getHospitalBranchId());
		log.debug("this is bookinga amabuybiwjx" + ambulancedriverob.getAmbulancetype());
		ambulancedriverob.setStatus("booked");
		ambulanceDriverService.addAmbulancedriver(ambulancedriverob);
		model.addAttribute("drivername", ambulancedriverob.getDriverId());
		model.addAttribute("ambulancetype", ambulancedriverob.getAmbulancetype());
		model.addAttribute("empid", empid);
		log.debug("this is bookinga amabuybiwjx" + ambulancedriverob.getAmbulancetype());
		model.addAttribute("BookingAmbulance", BookingAmbulance);
		return "bookingambulance";
	}
	@PostMapping("/savebookings/{bid}/{eid}")
	public String insertNewbookinAmbulance(Model model, @PathVariable("bid") int bid, @PathVariable("eid") int eid,
			BookingAmbulance BookingAmbulance) {
		log.debug("this mthod ");
		HospitalBranch hospitalbranch = hospitalBranchService.getHospitalbranchId(bid);
		model.addAttribute("hbranchId", hospitalbranch.getHospitalBranchId());
		model.addAttribute("BookingAmbulance", BookingAmbulance);
		bookingAmbService.addBookings(BookingAmbulance);
		List<AmbulanceDriverAssosiation> ambdriverlist = ambulanceDriverService.getAllAambulancedriver();
		model.addAttribute("ambdriverlist", ambdriverlist);
		model.addAttribute("empid", eid);
		return "ambulanceselection";
	}
	@GetMapping("/deleteBookingAmbulance/{bookingAmbulanceId}")
	public String deleteBookingAmbulance(Model model, @PathVariable int bookingAmbulanceId) {
		BookingAmbulance bookingambulance = bookingAmbService.getById(bookingAmbulanceId);
		bookingambulance.setIsactive('N');
		bookingAmbService.addBookings(bookingambulance);
		return "redirect:/bookingambulance/BookingList";
	}
	@GetMapping("/editbookingAmbulance/{bookingambulanceId}")
	public String EditBookingAmbulanceById(Model model, @PathVariable("bookingambulanceId") int bookingambulanceId, BookingAmbulance BookingAmbulance) {
		log.debug("::::::::::::" + BookingAmbulance.getBookingId());
		BookingAmbulance BookingAmbulance2 = bookingAmbService.getById(bookingambulanceId);
		log.debug("::::::::::::" + BookingAmbulance2.getToAddress());
		model.addAttribute("BookingAmbulance", BookingAmbulance);
		model.addAttribute("ambulancetype", BookingAmbulance2.getAmbulancetype());
		model.addAttribute("drivername", BookingAmbulance2.getDriverName());
		model.addAttribute("BookingAmbulance", BookingAmbulance2);
		log.debug("this is bookinga amabuybiwjx");
		log.debug("inside getHospitalbranchId id is:::" + bookingAmbService.getById(bookingambulanceId));
		return "bookingambulance";
	}
	@GetMapping("/statuschange/{bookingId}")
	public String statusByid(Model model, @PathVariable("bookingId") int bookingId) {
		BookingAmbulance objBookingAmbulance = bookingAmbService.getById(bookingId);
		objBookingAmbulance.setIsactive('n');
		log.debug("''''''''" + objBookingAmbulance.getAddressFrom());
		model.addAttribute("BookingAmbulance", objBookingAmbulance);
		bookingAmbService.Updatebooking(objBookingAmbulance);
		return "redirect:/bookingambulance/BookingList";
	}
}