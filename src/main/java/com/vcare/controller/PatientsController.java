package com.vcare.controller;

import java.time.LocalDate;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.vcare.beans.Appointment;
import com.vcare.beans.ContractService;
import com.vcare.beans.Doctor;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.Patients;
import com.vcare.repository.ContractEmployeeRepository;
import com.vcare.repository.ContractServiceRepository;
import com.vcare.repository.PatientsRepository;
import com.vcare.repository.ServiceRepository;
import com.vcare.service.AppointmentService;
import com.vcare.service.DoctorService;
import com.vcare.service.EmailSenderService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.HospitalService;
import com.vcare.service.PatientsService;
import com.vcare.service.ServiceService;
import com.vcare.utils.MailUtil;
import com.vcare.utils.VcareUtilies;

@Controller
@RequestMapping("/patient")
public class PatientsController {
	@Autowired
	ContractEmployeeRepository contractEmployeeRepository;
	@Autowired
	ContractServiceRepository contractServiceRepository;
	@Autowired
	PatientsService patientService;
	@Autowired
	ServiceService serviceservice;
	@Autowired
	ServiceRepository serviceRespository;
	@Autowired
	HospitalService hospitalService;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	DoctorService doctorService;
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	PatientsRepository patientsRepository;
	@Autowired
	EmailSenderService emailSenderService;
	@Autowired
	JavaMailSender MailSender;
	static Logger log = Logger.getLogger(PatientsController.class.getClass());
	@GetMapping("/patientsList")
	public String patientsList(Model model) {
		List<Patients> list = patientService.getAllActivePatients();
		model.addAttribute("patientObj", list);
		return "patientslist";
	}
	@GetMapping("/signup")
	public String viewPatientRegistrationForm(Model model) {
		Patients patientObj = new Patients();
		model.addAttribute("objPatient", patientObj);
		return "patientregistration";
	}
	@Value("${app.name}")
	String applicationName;
	@GetMapping(value ="/saveuser")
	public String savePatientRegistration(Model model, @ModelAttribute(value = "patientObj") Patients patientObj,HttpServletRequest request) {
		patientObj.setCreated(LocalDate.now());
		log.debug(patientObj.getPatientMailId());
		HttpSession session=request.getSession();
		List<Patients> list = patientService.getAllActivePatients();
		model.addAttribute("patientlist", list);
		String patientObj1 = patientService.validateduplicate(patientObj.getPatientMailId());
		if (patientObj1 == null) {
			model.addAttribute("patientObj", patientObj);
			String strEncPassword = VcareUtilies.getEncryptSecurePassword(patientObj.getPatientPassword(), applicationName);
			patientObj.setPatientPassword(strEncPassword);
			patientService.addNewPatient(patientObj);
			session.setAttribute("pId", patientObj.getPatientId());
			return "patientdashboard";
		}
		session.setAttribute("indexmsg","save");
		return "redirect:/";
	}
	@GetMapping("/patientprofile/{patientId}")
	public String viewPatientProfile(Model model, @PathVariable("patientId") int patientId, Patients patients,HttpSession session) {
		if(session.getAttribute("pId")==null) {
			return "redirect:/";
		}
		Patients patient = patientService.getPatientById(patientId);
		model.addAttribute("patientObj", patient);
		model.addAttribute("patientId", patientId);
		return "patientprofile";
	}
	@PostMapping("/patientupload")
	public String updatePatientProfile(Model model, Patients patientObj) {
		model.addAttribute("patientObj", patientObj);
		patientService.updatePatient(patientObj);
		model.addAttribute("patientmsg",patientObj.getFirstName()+", Your profile is updated successfully");
		return "patientdashboard";
	}
	@GetMapping(value = "/signin")
	public String patientSignin(Model model, @ModelAttribute(value = "objPatient") Patients patientObj) {
		model.addAttribute("patientObj", patientObj);
		return "PatientLogin";
	}
	@GetMapping("/loginvalid")
	public String patientLoginValidation(Model model, @ModelAttribute(value = "patientObj") Patients patientObj,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!patientObj.getCaptcha().equals(patientObj.getUserCaptcha())) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
		model.addAttribute("warninig", "invalid credentials..please try to login again...!");
		String str = VcareUtilies.getEncryptSecurePassword(patientObj.getPatientPassword(), "vcare");
		Patients signinObj = patientService.getPatient(patientObj.getPatientMailId(), str);
		model.addAttribute("patientObj", signinObj);
		if (signinObj != null) {
			session.setAttribute("name", signinObj.getFirstName());
			session.setAttribute("pId", signinObj.getPatientId());
			model.addAttribute("patientmsg","Hello "+signinObj.getFirstName()+", you have successfully logged in " );
			return "patientdashboard";
		}
		session.setAttribute("indexmsg","logininvalidpasswordorusername");
		return "redirect:/";
	}
	@GetMapping("/Paloginvalid/{contractId}")
	public String loginValidations(Model model, @ModelAttribute(value = "patientObj") Patients patientObj,@PathVariable("contractId") int contractId,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!patientObj.getCaptcha().equals(patientObj.getUserCaptcha())) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
		model.addAttribute("warninig", "invalid credentials..please try to login again...!");
		String str = VcareUtilies.getEncryptSecurePassword(patientObj.getPatientPassword(), "vcare");
		Patients signinObj = patientService.getPatient(patientObj.getPatientMailId(), str);
		model.addAttribute("patientObj", signinObj);
		if (signinObj != null) {
			 List<ContractService> offers=contractServiceRepository.contractServList(contractId);	
			 model.addAttribute("offer", offers);
			session.setAttribute("name", signinObj.getFirstName());
			session.setAttribute("pId", signinObj.getPatientId());
			model.addAttribute("patientmsg","Hello "+signinObj.getFirstName()+", you have successfully logged in " );
		   log.debug("this is id for patient"+signinObj.getPatientId());
			return "popularservices";
		}
		session.setAttribute("indexmsg","logininvalidpasswordorusername");
		return "redirect:/";
	}
	@GetMapping("/hospitalBranchList/{hospitalBranchId}")
	public String getBranchPatients(Model model, @PathVariable("hospitalBranchId") int hospitalBranchId,
			HospitalBranch hospitalBranch) {
		log.debug("inside getAllHospitals this will get the all hospitalsBranch:::");
		List<Patients> patients = patientService.getBranchPatients(hospitalBranch);
		model.addAttribute("patientObj", patients);
		return "patientslist";
	}
	@GetMapping("/logout")
	public String patientLogout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("pId", null);
		log.debug("logout done" + session.getAttributeNames());
		return "redirect:/";
	}
	@RequestMapping(value = "/for", method = RequestMethod.GET)
	public ModelAndView UserforgotPasswordPage(Patients patient) {
		log.debug("entered into user/controller::::forgot paswword method");
		ModelAndView mav = new ModelAndView("for");
		mav.addObject("patientObj", patient);
		return mav;
	}
	@PostMapping(value = "/valid")
	public String checkMailId(Model model, Patients patient) {
		log.debug("entered into user/controller::::check EmailId existing or not");
		log.debug("UI given mail Id:" + patient.getPatientMailId());
		Patients objpatient = patientService.findByMail(patient.getPatientMailId());
		if (objpatient != null) {
			String s1 = "";
			model.addAttribute("message", s1);
			log.debug("UI given mail Id:" + objpatient.getPatientMailId());
			model.addAttribute("patientObj", patient);
			return "resetdata";
		} else {
			log.debug("Invalid Mail");
			String s1 = "Email-Id Not Exists";
			model.addAttribute("message", s1);
			model.addAttribute("patientObj", new Patients());
			return "for";
		}
	}
	@RequestMapping(value = "/updatepassword", method = RequestMethod.POST)
	public ModelAndView updateUserPassword(Model model, @ModelAttribute("objdoctor") Patients patient) {
		Patients objPatient = patientService.findByMail(patient.getPatientMailId());
		log.debug("inside updateClientPassword after update id is:::" + patient.getPatientMailId());
		String strEncPassword = VcareUtilies.getEncryptSecurePassword(patient.getPatientPassword(), "vcare");
		patient.setPatientPassword(strEncPassword);
		objPatient.setPatientPassword(patient.getPatientPassword());
		log.debug("in update method pUser:: name " + objPatient.getFirstName());
		patientService.updatePatient(objPatient);
		ModelAndView mav = new ModelAndView("index");
		model.addAttribute("objdoctor", objPatient);
		model.addAttribute("message","Password changed successfully");
		return mav;
	}
	@RequestMapping("/verification")
	public String validatemail(Model model, Patients patientObj, final HttpServletRequest request,
			final HttpServletResponse response,HttpSession session ) throws MessagingException{
		if (!patientObj.getCaptcha().equals(patientObj.getUserCaptcha())) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
		final StringBuffer uri = request.getRequestURL();
		log.debug(":::::::::::::"+session.getAttribute("url"));
		log.debug(":::::::::::::::::"+uri);
		log.debug(":::::::::::::::::"+patientObj.getPatientMailId());
		model.addAttribute("Emailid",patientObj.getPatientMailId());
		SimpleMailMessage mailMessage = new SimpleMailMessage(); 
		log.debug(":::::::::::::::::2"+patientObj.getPatientMailId());
        mailMessage.setTo(patientObj.getPatientMailId());
        log.debug("::::::::::::::::: 3"+patientObj.getPatientMailId());
        mailMessage.setSubject("Complete Registration!");
        log.debug("::::::::::::::::: 4"+patientObj.getPatientMailId());
        mailMessage.setFrom("ajithnetha@gmail.com");
        log.debug("::::::::::::::::: 5"+patientObj.getPatientMailId());
        mailMessage.setText("To confirm your account, please click here : "
                +session.getAttribute("url")+"/saveuser?patientMailId="+patientObj.getPatientMailId()+"&patientPassword="+patientObj.getPatientPassword());
        log.debug("::::::::::::::::: 6"+session.getAttribute("url")+"saveuser?patientMailId="+patientObj.getPatientMailId()+"&patientPassword="+patientObj.getPatientPassword());
        MailUtil.sendSimpleEmail(MailSender,patientObj.getPatientMailId(),"To confirm your account, please click here : "
                +session.getAttribute("url")+"saveuser?patientMailId="+patientObj.getPatientMailId()+"&patientPassword="+patientObj.getPatientPassword(),"Mailid Verification");
     
        log.debug("::::::::::::::::: 7"+patientObj.getPatientMailId());
        model.addAttribute("Emailid",patientObj.getPatientMailId());
        return "successfulRegistration";
	}
	@PostMapping("/patientupload/{hospitalBranchId}/{doctorId}/{employeeId}")
	public String uploadPatient(Model model, Patients patientObj,@RequestParam("slot") String slot,@PathVariable("employeeId") int employeeId,@RequestParam("consultantType") String consultantType,@RequestParam("date") String date,
	@PathVariable("doctorId") int doctorId,Appointment appointment,@PathVariable("hospitalBranchId") int hospitalBranchId) {
	model.addAttribute("patientObj", patientObj);
	patientService.addNewPatient(patientObj);
	List<Patients> patients=patientsRepository.findPatientforAppointment(patientObj.getPatientMailId(), patientObj.getFirstName(),patientObj.getPatientMobile());
	patientService.updatePatient(patientObj);
	Doctor doctor=doctorService.GetDocotorById(doctorId);
	model.addAttribute("patientObj",patients.get(0));
	model.addAttribute("consultantType",consultantType);
	model.addAttribute("doctor",doctor);
	model.addAttribute("hbid",hospitalBranchId);
	model.addAttribute("slot",slot);
	model.addAttribute("date",date);
	model.addAttribute("employeeId", employeeId);
	if(patients.get(0).getPatientId()!=0) {
	model.addAttribute("appointment",appointment);
	return "offlineapppointmentform";
	}
	return "patientdashboard";
	}
 }