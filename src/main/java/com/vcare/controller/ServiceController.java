package com.vcare.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.springframework.web.bind.annotation.RequestParam;
import com.vcare.beans.Doctor;
import com.vcare.beans.Hospital;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.News;
import com.vcare.beans.Rating;
import com.vcare.beans.Services;
import com.vcare.repository.DoctorRatingRepository;
import com.vcare.repository.HospitalBranchRepository;
import com.vcare.repository.ServiceRepository;
import com.vcare.service.DoctorRatingService;
import com.vcare.service.DoctorService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.HospitalService;
import com.vcare.service.NewsService;
import com.vcare.service.ServiceService;
import com.vcare.service.ServiceServiceImpl;

@Controller
@RequestMapping("/page")
public class ServiceController {
	@Autowired
	ServiceService serviceServices;
	@Autowired
	ServiceRepository serviceRrepository;
	@Autowired
	NewsService newsService;
	@Autowired
	ServiceServiceImpl serviceServiceImpl;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	DoctorService doctorService;
	@Autowired
	HospitalService hospitalService;
	@Autowired
	DoctorRatingRepository doctorRatingRepository;
	@Autowired
	DoctorRatingService doctorRatingService;
	@Autowired
	HospitalBranchRepository branchRepository;
	static Logger log = Logger.getLogger(ServiceController.class.getClass());
	@GetMapping("/service/{serviceId}")
	public String list(Model model,@PathVariable("serviceId")int serviceId) {
	List<Services> serviceList = serviceServiceImpl.servicesByBranch(serviceId);
	HospitalBranch objBrnach=hospitalBranchService.getHospitalbranchId(serviceId);
	model.addAttribute("branch", objBrnach.getHospitalBranchId());
	model.addAttribute("serviceList", serviceList);
	return "servicelist";
	}
	@RequestMapping(value = "/serPage/{branchId}", method = RequestMethod.GET)
	public String getServiceList(Model model, @PathVariable("branchId") int branchId,
			@ModelAttribute(value = "serviceList") Services objThServ,HttpServletRequest request) {
		HospitalBranch branch=hospitalBranchService.getHospitalbranchId(branchId);
		model.addAttribute("HospitalbranchId", branch.getHospitalBranchId());
		log.debug("Branch Id:::::::"+branch.getHospitalBranchId());
		List<Services> objService = serviceRrepository.findservicesbyHospitalBranch(branchId);
		HttpSession session=request.getSession();
		if(session.getAttribute("seradd")=="seradd") {
			model.addAttribute("adminmsg",session.getAttribute("sername")+"\t Service \t Added");
			session.setAttribute("seradd", "");
			session.setAttribute("sername", "");
		}
		if(session.getAttribute("serup")=="serup") {
			model.addAttribute("adminmsg",session.getAttribute("sername")+"\t Service \tUpdated");
			session.setAttribute("serup", "");
			session.setAttribute("sername", "");
		}
		if(session.getAttribute("serdelete")=="serdelete") {
			model.addAttribute("adminmsg","\tSuccessfully deletede");
			session.setAttribute("serdelete","");
		}
		model.addAttribute("serviceList", objService);
		return "servicelist";
	}
	@RequestMapping(value = "/addHospitalBranch/{branchId}", method = RequestMethod.GET)
	public String serviceForm(Model model, @PathVariable("branchId") int branchId, Services objService) {
		try {
			model.addAttribute("serviceObj", objService);
			log.debug("service id::" + objService.getServiceId());
			log.debug("Branch id::" + branchId);
			HospitalBranch hospBObj = hospitalBranchService.getHospitalbranchId(branchId);
			model.addAttribute("hospBObj", hospBObj);
			HospitalBranch objBranch = hospitalBranchService.getHospitalbranchId(branchId);
			model.addAttribute("HospitalbranchId", hospBObj.getHospitalBranchId());
			model.addAttribute("name", objBranch.getHospitalBranchName());
			log.debug("BranchName ::" + objBranch.getHospitalBranchName());
			return "serviceform";
		} catch (Exception ex) {
		}
		log.debug("service id::" + objService.getServiceId());
		model.addAttribute("serviceObj", objService);
		return "serviceform";
	}
	@RequestMapping(value = "/saveForm/{branchId}", method = RequestMethod.POST)
	public String addService(Model model, Services objServices,@PathVariable("branchId")int branchId,HttpServletRequest request) {
		HospitalBranch branchObj=hospitalBranchService.getHospitalbranchId(branchId);
		model.addAttribute("ObjId", branchObj.getHospitalBranchId());
		log.debug("Service id:::" + objServices.getServiceId());
		HttpSession session =request.getSession();
		session.setAttribute("sername", objServices.getServiceName());
		if(objServices.getServiceId()!=null) {
			session.setAttribute("serup","serup");
			session.setAttribute("sername", objServices.getServiceName());
		}else 
			session.setAttribute("seradd", "seradd");
		serviceServices.UpdateServices(objServices);
		return "redirect:/page/serPage/"+branchObj.getHospitalBranchId();
	}
	@GetMapping("/editService/{serviceId}")
	public String getById(Model model, @PathVariable("serviceId") int serviceId) {
		Services objSecService = serviceServices.getById(serviceId);
		log.debug("inside getHospitalbranchId id is:::" + objSecService.getServiceId());
		model.addAttribute("serviceObj", objSecService);
		model.addAttribute("HospitalbranchId", objSecService.getHospitalbranch().getHospitalBranchId());
		log.debug("inside editHospitalBranch id:::" + objSecService.getHospitalbranch().getHospitalBranchId());
		return "serviceform";
	}
	@GetMapping("/deleteService/{serviceId}")
	public String deleteService(Model model, @PathVariable("serviceId") int serviceId,HttpServletRequest request) {
		Services inactive = serviceServices.getById(serviceId);
		inactive.setIsActive('N');
		HttpSession session =request.getSession();
		session.setAttribute("serdelete","serdelete");
		serviceServices.UpdateServices(inactive);
		return "redirect:/page/serpage/"+inactive.getHospitalbranch().getHospitalBranchId();
	}
	@GetMapping("/dynamicservices/{serviceName}")
	public String dynamicServices(Model model, @PathVariable String serviceName,
			@RequestParam(name = "serviceList", required = false) List<String> serviceList, Services serviceObj) {
		List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
		for (String s : serviceNames) {
			if (serviceName.equalsIgnoreCase(s)) {
				// creating branches for services
				List<HospitalBranch> hospObj = branchRepository.hospitalBranches();
				// model.addAttribute("hospitalBranchList", hospObj);
				model.addAttribute("serviceList", serviceName);
				// Description of service
				List<Services> sameServices = serviceRepository.findService(serviceName);
				model.addAttribute("description", sameServices.get(0).getDescription());
				log.debug("DESCRIPTION BLOCK");
				Set<String> dhbname = new HashSet();
				Set<String> bloc = new HashSet();
				for (int i = 0; i < hospObj.size(); ++i) {
					log.debug("1ST FOR LOOP BLOCK");
					for (int j = 0; j < sameServices.size(); j++) {
						log.debug("2ND FOR LOOP BLOCK");
						log.debug(sameServices.get(j).getServiceName());
						log.debug(sameServices.get(j).hospitalbranch.getHospitalBranchName());
						if (sameServices.get(j).getServiceName().equalsIgnoreCase(serviceName)) {
							String str = sameServices.get(j).getHospitalbranch().getHospitalBranchName();
							dhbname.add(str);
							log.debug("IF BLOCK");
						}
					}
				}
				log.debug(dhbname);
				model.addAttribute("dhbname", dhbname);
				model.addAttribute("bloc", bloc);
				return "servicedynamic";
			}
		}
		return "redirect:/vcare";
	}
	@GetMapping("/indexdoctors/{doctorId}")
	public String indexDoctors(Model model, @PathVariable("doctorId") int doctorId) {
		List<Doctor> doctorList = doctorService.getAllDoctor();
		log.debug(doctorList);
		model.addAttribute("doctorList", doctorList);
		Doctor doc = doctorService.GetDocotorById(doctorId);
		log.debug(doc.getDoctorId());
		if (doctorId == doc.getDoctorId()) {
			model.addAttribute("doc", doctorService.GetDocotorById(doctorId));
			log.debug(doc);
		}
		double average;
		long totalrating;
		// Getting Average rating for doctor
		if (doctorRatingRepository.sumOfRating(doc.getDoctorId()) == null) {
			totalrating = 0;
			average = 0.0;
			model.addAttribute("average", average);
		} else {
			totalrating = doctorRatingRepository.sumOfRating(doc.getDoctorId());// Sum of ratings
			List<Rating> pcount = doctorRatingService.GetAllDoctorRating();// totol entries
			long count = pcount.size(); // no.of patinets given rating
			average = (float) totalrating / count;
			model.addAttribute("average", average);
		}
		// getting no.of persons according to rating
		Long fivecount = doctorRatingRepository.fivecount(doc.getDoctorId());
		model.addAttribute("fivecount", fivecount);
		Long fourcount = doctorRatingRepository.fourcount(doc.getDoctorId());
		model.addAttribute("fourcount", fourcount);
		Long threecount = doctorRatingRepository.threecount(doc.getDoctorId());
		model.addAttribute("threecount", threecount);
		Long twocount = doctorRatingRepository.twocount(doc.getDoctorId());
		model.addAttribute("twocount", twocount);
		Long onecount = doctorRatingRepository.onecount(doc.getDoctorId());
		model.addAttribute("onecount", onecount);
		return "doctorscreen";
	}
	@GetMapping("/indexdoctorscreen/{doctorId}")
	public String indexDoctorScreen(Model model, @PathVariable("doctorId") int doctorId) {
		Doctor doc = doctorService.GetDocotorById(doctorId);
		if (doctorId == doc.getDoctorId()) {
			model.addAttribute("doc", doctorService.GetDocotorById(doctorId));
			log.debug(doc);
		}
		List<Rating> ratingList = doctorRatingRepository.docrating(doctorId);
		model.addAttribute("rating", ratingList);
		return "doctorscreen";
	}
	@GetMapping("/servicehospitalPage/{HospitalbranchId}")
	public String viewHospitalPage(Model model,@PathVariable("HospitalbranchId") int HospitalbranchId){
	List<HospitalBranch> hospBranchList = branchRepository.hospitalBranches();
	model.addAttribute("hospitalBranchList", hospBranchList);
	List<News> newslists = newsService.getLatestNews();
	model.addAttribute("newsLists", newslists);
	HospitalBranch objHospitalBranch = hospitalBranchService.getHospitalbranchId(HospitalbranchId);
	model.addAttribute("objHospitalBranch", objHospitalBranch); 
	return "hospitalpage";
	}
	@GetMapping("/viewAllServices")
    public String viewHomeServices(Model model) {
        List<Hospital> hospList = hospitalService.getAllHospitals();
        model.addAttribute("hospitalsList", hospList);
        List<Services> servicelist = serviceServices.getAllActiveServices();
        model.addAttribute("servicelist", servicelist);
        return "indexservices";
    }
}
