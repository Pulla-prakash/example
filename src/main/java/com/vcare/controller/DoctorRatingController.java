package com.vcare.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.vcare.beans.Doctor;
import com.vcare.beans.Patients;
import com.vcare.beans.Rating;
import com.vcare.repository.DoctorRatingRepository;
import com.vcare.service.DoctorImplementation;
import com.vcare.service.DoctorRatingService;
import com.vcare.service.PatientsService;

@Controller
public class DoctorRatingController {

	@Autowired
	DoctorRatingService doctorRatingService;
	@Autowired
	PatientsService patientsService;
	@Autowired
	DoctorRatingRepository doctorRatingRepository;
	@Autowired
	DoctorImplementation doctorImplementation;

	static Logger log = Logger.getLogger(DoctorRatingController.class.getClass());

	@GetMapping("/drating/{patientId}/{doctorId}")
	public String doctorRating(Model model, @PathVariable("patientId") int patientId, @PathVariable("doctorId") int doctorId, Rating drating) {
		Patients patient = patientsService.getPatientById(patientId);
		List<Rating> ratingList = doctorRatingService.getRatingByDoctorId(doctorId);
		model.addAttribute("patientname", patient.getFirstName());
		model.addAttribute("pi", patient);
		model.addAttribute("pid", patientId);
		model.addAttribute("id", doctorId);
		model.addAttribute("rating", ratingList);
		model.addAttribute("drating", drating);
		return "doctorRating";
	}
	@PostMapping(value = "/savedoctorratings/{pid}/{id}")
	public String saveDoctorRating(Model model, @PathVariable("pid") int pid, @PathVariable("id") int id,
			Rating doctorRating) {
		doctorRating.setCreated(LocalDate.now());
		log.info("This is save method");
		doctorRatingService.addDoctorRating(doctorRating);
		return "redirect:/Viewdoctors/" + pid + "/" + id;

	}
	@GetMapping(value = "dratinglist")
	public String doctorRatingList(Model model) {
		List<Rating> doctorlist = doctorRatingService.GetAllDoctorRating();
		model.addAttribute("doctorlist", doctorlist);
		return "doctorratinglist";
	}

	@GetMapping(value = "/adddoctorrating")
	public String addDoctorRating(Model model, Rating doctorRating) {
		model.addAttribute("doctorlist", doctorRating);
		return "doctorrating";
	}
}
