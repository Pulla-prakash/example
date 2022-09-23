
package com.vcare.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.vcare.beans.Doctor;
import com.vcare.beans.DoctorAvailability;
import com.vcare.beans.HospitalBranch;
import com.vcare.repository.DoctorAvailabilityRepository;
import com.vcare.repository.HospitalBranchRepository;
import com.vcare.service.DoctorAvailabilityService;
import com.vcare.service.DoctorService;
import com.vcare.service.HospitalBranchService;

@Controller
@RequestMapping("/DoctorAvailability")
public class DoctorAvailabilityController {
	@Autowired
	DoctorAvailabilityService doctorAvailabilityService;
	@Autowired
	DoctorService docService;
	@Autowired
	DoctorAvailabilityRepository availabilityRepository;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	HospitalBranchRepository branchRepository;
static Logger log=Logger.getLogger(DoctorAvailabilityController.class.getClass());
	
	@GetMapping(value = "/availabilityList/{doctorId}")
    public String getAllDoctorAvailability(Model model,@PathVariable("doctorId") int doctorId,HttpSession session) {
        List<DoctorAvailability> availabilityList = availabilityRepository.availableDoctor(doctorId);
        if(session.getAttribute("DocId")==null) {
        	return "redirect:/";
        }
        model.addAttribute("availabilityList", availabilityList);
        model.addAttribute("doctorid", doctorId);
        return "availabilitylist";
    }
	@RequestMapping(value = "/addAvailability/{doctorId}", method = RequestMethod.GET)
    public String newAvailability(Model model, @PathVariable("doctorId") int doctorId,HttpSession session, DoctorAvailability doctorAvailability) {
		if(session.getAttribute("DocId")==null) {
			return "redirect:/";	
		}
        Doctor doc = docService.GetDocotorById(doctorId);
        log.debug("get Doctor name"+doc.getDoctorName());
        List<HospitalBranch> branchList = new ArrayList<>();
        List<HospitalBranch> hbList = branchRepository.hospitalBranches();
        String dbId = doc.getHospitalBranchId();
        if (dbId == null) {
            session.setAttribute("drname",doc.getDoctorName());
            model.addAttribute("nodocbranches","You are not working in any Hospital, please update your profile    ");
            model.addAttribute("objdoctor", doc);
            return "dashboard";
        }
        else {
            String[] arrOfStr = dbId.split(",");
            if (arrOfStr.length == 0) {
                HospitalBranch hd = hospitalBranchService.getHospitalbranchId(Integer.parseInt(dbId));
                branchList.add(hd);
            } else {
                for (String a : arrOfStr) {
                    for (HospitalBranch hb : hbList) {
                        if (hb.getHospitalBranchId() == Integer.parseInt(a)) {
                            branchList.add(hb);
                        }
                    }
                }
            }
        }
        model.addAttribute("doctorAvailability", doctorAvailability);
        model.addAttribute("branchList", branchList);
        model.addAttribute("doc", doc.getDoctorId());
        model.addAttribute("doctorid", doctorId);
        return "doctoravailability";
    }
	@RequestMapping(value = "/saveAvailability/{doctorId}", method = RequestMethod.POST)
    public String addDoctorAvailability(Model model, @PathVariable("doctorId") int doctorId, DoctorAvailability doctorAvailability,
            HttpSession session, @RequestParam(name = "hospitalBranch", required = false) int hospitalBranchId) {
        Doctor doc = docService.GetDocotorById(doctorId);
        model.addAttribute("availabilityList", doctorAvailability);
        model.addAttribute("doc", doc.getDoctorName());
        model.addAttribute("branches", 1);
        List<DoctorAvailability> docAvailList = doctorAvailabilityService.getAllDoctorAvailability();
        log.debug("doctorId"+docAvailList.size());
        if (docAvailList.size() == 0) {
            doctorAvailabilityService.addDoctorAvailability(doctorAvailability);
            return "redirect:/DoctorAvailability/availabilityList/" + doc.getDoctorId();
        }
        ArrayList<Integer> a = new ArrayList<>();
        for (DoctorAvailability docavail : docAvailList) {
            a.add(docavail.getDoctor().getDoctorId());
            a.add(docavail.getHospitalBranch().getHospitalBranchId());
        }
        if (doctorAvailability.getAvailabilityId() != 0) {
            doctorAvailabilityService.addDoctorAvailability(doctorAvailability);
            session.setAttribute("docAvailExists", "Updated Sucessfully");
        } else {
            if (a.contains(hospitalBranchId) && a.contains(doctorAvailability.getDoctor().getDoctorId())) {
                HospitalBranch hb = hospitalBranchService.getHospitalbranchId(hospitalBranchId);
                session.setAttribute("docAvailExists",
                        "Record already exists in " + hb.getHospitalBranchName() + ", please update!");
            } else {
                doctorAvailabilityService.addDoctorAvailability(doctorAvailability);
                session.setAttribute("docAvailExists", "");
            }
        }
        model.addAttribute("branchId", doc.getHospitalBranchId());
        return "redirect:/DoctorAvailability/availabilityList/" + doc.getDoctorId();
    }
	@GetMapping("/editAvailability/{availabilityId}")
	public String getAvailabilityById(Model model, @PathVariable("availabilityId") int availabilityId,DoctorAvailability doctorAvailability) {
		doctorAvailability=doctorAvailabilityService.getDoctorAvailabilityId(availabilityId);
		int docId=doctorAvailability.getDoctor().getDoctorId();
		doctorAvailability.getDoctor().getHospitalBranchId();
		List<HospitalBranch >branchList=new  ArrayList<>();
		List<HospitalBranch > hbList=branchRepository.hospitalBranches();
		Doctor doc=docService.GetDocotorById(docId);
		String dbId=doc.getHospitalBranchId();
		String[] arrOfStr = dbId.split(",");
		log.debug("arrOfStr="+arrOfStr);
		for (String a : arrOfStr)
				for(HospitalBranch hb:hbList) {
			if(hb.getHospitalBranchId()==Integer.parseInt(a)) {
				branchList.add(hb);
			}
		}
		 doctorAvailability.setStartTimings(doctorAvailability.getStartTimings());
		 doctorAvailability.setEndTimings(doctorAvailability.getEndTimings());
		model.addAttribute("branchList", branchList);
		model.addAttribute("doctorAvailability", doctorAvailability);
		model.addAttribute("doctorid", docId);
		return "doctoravailability";
	}
}
