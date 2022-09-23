package com.vcare.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.vcare.beans.Aboutus;
import com.vcare.beans.Admin;
import com.vcare.beans.Appointment;
import com.vcare.beans.Doctor;
import com.vcare.beans.Hospital;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.Insurance;
import com.vcare.beans.LabTest;
import com.vcare.beans.News;
import com.vcare.beans.SocialNetworks;
import com.vcare.beans.popularOffers;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.DoctorRepository;
import com.vcare.repository.HospitalBranchRepository;
import com.vcare.repository.LabTestRepository;
import com.vcare.repository.ServiceRepository;
import com.vcare.service.AboutusService;
import com.vcare.service.AdminService;
import com.vcare.service.AppointmentService;
import com.vcare.service.DoctorService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.HospitalService;
import com.vcare.service.InsuranceService;
import com.vcare.service.LabTestService;
import com.vcare.service.NewsService;
import com.vcare.service.PatientsService;
import com.vcare.service.SocialNetworkService;
import com.vcare.service.popularOfferService;
import com.vcare.utils.VcareUtilies;

@Controller
@RequestMapping("/")
public class AdminController {
	@Autowired
	private AdminService adminService;
	@Autowired
	LabTestRepository labTestRepository;
	@Autowired
	LabTestService labTestService;
	@Autowired
	private NewsService newsservice;
	@Autowired
	DoctorService doctorservice;
	@Autowired
	ServiceRepository serviceRespository;
	@Autowired
	DoctorService doctorService;
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	AppointmentRepository appointmentRepository;
	@Autowired
	PatientsService patientsService;
	@Autowired
	InsuranceService insuranceService;
	@Autowired
	HospitalService hospitalService;
	@Autowired
	AboutusService aboutusService;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	popularOfferService offerService;
	@Autowired
	SocialNetworkService socialNetworkService;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	HospitalBranchRepository branchRepository;
	static Logger log = Logger.getLogger(AdminController.class.getName());
	@GetMapping("/")
	public String viewHomePages(Model model, News news,
			@RequestParam(name = "serviceList", required = false) List<String> serviceList,
			@RequestParam(name = "servList", required = false) List<String> servList, final HttpServletRequest request,
			final HttpServletResponse response, HttpSession session) {
		List<News> newslists = newsservice.getLatestNews();
		model.addAttribute("newsLists", newslists);
		List<String> serviceNames = serviceRespository.findDistinctService(serviceList);
		model.addAttribute("serviceList", serviceNames);
		log.debug("Hello this is a debug message");
		log.debug("Hello this is an info message");
		log.debug("++++++++++");
		List<Doctor> docList = doctorService.getAllDoctor();
		model.addAttribute("docList", docList);
		log.debug("===========INDEX==============");
		log.debug("NEWS :" + newslists);
		log.debug("DOCTORS :" + docList);
		log.debug("SERVICES :" + serviceNames);
		session = request.getSession();
		if (session.getAttribute("indexmsg") == "save") {
			model.addAttribute("indexmsg", "You have already registered, please SignIn");
			session.setAttribute("indexmsg", "");
		} else if (session.getAttribute("indexmsg") == "logininvalidcaptcha") {
			model.addAttribute("indexmsg", "Captcha you entered is wrong");
			session.setAttribute("indexmsg", "");
		} else if (session.getAttribute("indexmsg") == "logininvalidpasswordorusername") {
			model.addAttribute("indexmsg", "You have entered wrong credentials, please SignIn or Register");
			session.setAttribute("indexmsg", "");
		} else if (session.getAttribute("indexmsg") == "updatepassword") {
			model.addAttribute("indexmsg", "Your password updated successfully");
			session.setAttribute("indexmsg", "");
		}
		List<Aboutus> listAbout = aboutusService.getAllAbouts();
		model.addAttribute("aboutlist", listAbout);
		log.debug("about:::::" + listAbout);
		List<HospitalBranch> hospBranchList =branchRepository.hospitalBranches();
		log.debug("inside getAllHospitals this will get the all hospitalsBranch:::");
		model.addAttribute("hospitalBranchList", hospBranchList);
		StringBuffer obj = new StringBuffer();
		int i = 1;
		for (HospitalBranch a : hospBranchList) {
			obj.append("[\"" + a.getHospitalBranchName() + "\"," + a.getLatitude() + "," + a.getLongitude() + "," + i
					+ "],");
			i++;
		}
		model.addAttribute("branches", obj.toString());
		List<SocialNetworks> networkList = socialNetworkService.getAllNetworks();
		model.addAttribute("networkList", networkList);
		try {
			List<News> newslist = newslists.subList(0, 2);
			log.debug("fdgyuhjkvgchfyuikjhgfyuil" + newslist);
			model.addAttribute("newsList", newslist);
		} catch (IndexOutOfBoundsException e) {
			log.debug("vgqedvgdhvedhvEYUDGHebdhjB4534567456");
			model.addAttribute("newsList", null);
		}
		List<Hospital> hospList = hospitalService.getAllHospitals();
		model.addAttribute("hospitalsList", hospList);
		List<Insurance> list = insuranceService.getAllInsurance();
		model.addAttribute("insurancelist", list);
		List<LabTest> distincttestList = (List<LabTest>) labTestRepository.findDistinctTest();
		model.addAttribute("disTestList", distincttestList);
		List<popularOffers> listoffer = offerService.getAllOffers();
		model.addAttribute("listoffers", listoffer);
		log.debug("Hospital:::::" + hospList);
		final StringBuffer url1 = request.getRequestURL();
		HttpSession session1 = request.getSession();
		session1.setAttribute("url", url1);
		return "index";
	}
	@GetMapping("/adminlogin")
	public String adminLogin(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("adminindexmsg") == "logininvalidcaptcha") {
			model.addAttribute("adminindexmsg", "Captcha you entered is wrong");
			session.setAttribute("adminindexmsg", "");
		} else if (session.getAttribute("adminindexmsg") == "save") {
			model.addAttribute("adminindexmsg", "You have already registered, please SignIn");
			session.setAttribute("adminindexmsg", "");
		} else if (session.getAttribute("adminindexmsg") == "logininvalidpasswordorusername") {
			model.addAttribute("adminindexmsg", "You have entered wrong credentials, please SignIn or Register");
			session.setAttribute("adminindexmsg", "");
		}
		return "adminloginpage";
	}
	@PostMapping("/saveAdmin")
	public String saveAdmin(Model model, @ModelAttribute("admin") Admin admin, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!admin.getCaptchaAdmin().equals(admin.getUserCaptcha())) {
			log.debug("if condition working::::;");
			session.setAttribute("adminindexmsg", "logininvalidcaptcha");
			return "redirect:/adminlogin";
		}
		log.debug("ADMIN : " + admin);
		log.debug("Registered");
		String strEncPassword = VcareUtilies.getEncryptSecurePassword(admin.getPassword(), "vcare");
		admin.setPassword(strEncPassword);
		session.setAttribute("adminuniqexception", "adminsave");
		adminService.saveAdmin(admin);
		model.addAttribute("warning", "you have successfully registered, please login");
		return "adminloginpage";

	}
	@GetMapping("/checkCredentials")
	public String checkCredentials(Model model, Admin admin, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!admin.getCaptchaAdmin().equals(admin.getUserCaptcha())) {
			session.setAttribute("adminindexmsg", "logininvalidcaptcha");
			return "redirect:/adminlogin";
		}
		String str = VcareUtilies.getEncryptSecurePassword(admin.getPassword(), "vcare");
		Admin adminOne = adminService.getAdmin(admin.getEmail(), str);
		model.addAttribute("admin", adminOne);
		log.debug("LOGIN CREDENTIALS " + adminOne);
		if (adminOne != null) {
			session.setAttribute("AdminId", adminOne.getAdminId());
			log.debug(adminOne.getName() + " Successfully login");
			return "admindashboard";
		} else {
			session.setAttribute("adminindexmsg", "logininvalidpasswordorusername");
			log.debug("Invalid credentials");
			return "redirect:/adminlogin";
		}
	}
	@RequestMapping(value = "/forgotPassword", method = RequestMethod.GET)
	public ModelAndView UserforgotPasswordPage(Admin admin) {
		log.debug("hello:::::" + admin.getEmail());
		log.debug("entered into user/controller::::forgot paswword method");
		ModelAndView mav = new ModelAndView("forgotPassword");
		mav.addObject("admin", admin);
		return mav;
	}
	@PostMapping(value = "/validateEmailId")
	public String checkMailId(Model model, Admin admin) {
		log.debug("entered into user/controller::::check EmailId existing or not");
		log.debug("UI given mail Id:" + admin.getEmail());
		log.debug("UI given mail Id:" + admin.getName());
		Admin adminObj = adminService.findByMail(admin.getEmail());
		if (adminObj != null) {
			String s1 = "";
			model.addAttribute("message", s1);
			log.debug("UI given mail Id:" + adminObj.getEmail());
			model.addAttribute("admin", admin);
			return "resetPassword";
		} else {
			log.debug("Invalid Mail");
			String s1 = "Email-Id Not Exists";
			model.addAttribute("message", s1);
			model.addAttribute("admin", new Admin());
			return "forgotPassword";
		}
	}
	@RequestMapping(value = "/updateClientPassword", method = RequestMethod.POST)
	public ModelAndView updateUserPassword(Model model, @ModelAttribute("admin") Admin adminObj) {
		Admin objAdmin = adminService.findByMail(adminObj.getEmail());
		log.debug("inside updateClientPassword after update id is:::" + adminObj.getAdminId());
		String strEncPassword = VcareUtilies.getEncryptSecurePassword(adminObj.getPassword(), "vcare");
		adminObj.setPassword(strEncPassword);
		objAdmin.setPassword(adminObj.getPassword());
		log.debug(adminObj.getPassword());
		log.debug("in update method pUser:: name " + objAdmin.getName());
		adminService.UpdateAdmin(objAdmin);
		// pUser.setUpdatedDate(LocalDateTime.now());
		log.debug("password is updated sucessfully");
		ModelAndView mav = new ModelAndView("indexlogin");
		log.debug("login page is displayed");
		model.addAttribute("admin", objAdmin);
		return mav;
	}
	@GetMapping("/Appointment")
	public String bookAppointment(Model model, final HttpServletRequest request,
			@RequestParam(name = "BranchId", required = false, defaultValue = "0") int branchId,
			@RequestParam(name = "DoctorId", required = false) String doctorId,
			@RequestParam(name = "from", required = false) String from,
			@RequestParam(name = "to", required = false) String to) {
		List<HospitalBranch> BranchId = branchRepository.hospitalBranches();
		model.addAttribute("Dropbranch", BranchId);
		if (branchId == 0) {
			model.addAttribute("HbDrop", BranchId.get(0));
		}
		if (branchId != 0) {
			log.debug("branchId" + branchId);
			List<Doctor> doctorList = doctorRepository.branchDoctors(Integer.toString(branchId));
			model.addAttribute("Dropdoctor", doctorList);
			model.addAttribute("HbDrop", hospitalBranchService.getHospitalbranchId(branchId));
			if (to != null && to != "") {
				List<Appointment> dappointments = appointmentRepository
						.applointmentListBetweenDatesByDoctorId(Integer.parseInt(doctorId), from, to);
				model.addAttribute("appointmentlist", dappointments);
				if (dappointments.size() == 0) {
					model.addAttribute("adminmsg", "No appointments");
				}
			}
			return "adminappointmentform";
		}
		return "adminappointmentform";
	}
	@GetMapping("/payments")
	public String payments(Model model, Appointment objAppointment) {
		List<Appointment> appointment = appointmentService.getAllAppointment();
		model.addAttribute("appointment", appointment);
		return "totaldoctorappointments";
	}
}
