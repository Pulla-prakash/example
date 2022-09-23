package com.vcare.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vcare.beans.Appointment;
import com.vcare.beans.ContractService;
import com.vcare.beans.Doctor;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.Patients;
import com.vcare.beans.Payment;
import com.vcare.beans.ServiceAppointment;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.DoctorRepository;
import com.vcare.repository.PaymentRepository;
import com.vcare.repository.ServiceRepository;
import com.vcare.service.AppointmentService;
import com.vcare.service.ContractEmployeesService;
import com.vcare.service.ContractServicesService;
import com.vcare.service.DoctorService;
import com.vcare.service.EmailSenderService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.PatientsService;
import com.vcare.service.ServiceAppointmentService;

@RequestMapping("/appointment")
@Controller
public class AppointmentController {
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	ServiceAppointmentService serviceAppointmentService;
	@Autowired
	DoctorService doctorService;
	@Autowired
	PatientsService patientsService;
	@Autowired
	ContractEmployeesService contractEmployeesService;
	@Autowired
	ContractServicesService contractServicesService;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	AppointmentRepository appointmentRepository;
	@Autowired
	EmailSenderService emailSenderService;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	PaymentRepository paymentRepository;
	@Autowired
	DoctorRepository doctorRepository;
	static Logger log = Logger.getLogger(AppointmentController.class.getClass());

	@GetMapping(value = "/appointmentlist")
	public String getAppointmentList(Model model) {
		List<Appointment> appointmentList = appointmentService.getAllActiveAppointments();
		log.debug("===========Appointment List=============");
		log.debug("Appointments : " + appointmentList);
		model.addAttribute("appointmentlist", appointmentList);
		return "patientAppointmentList";
	}

	@GetMapping(value = "/appointmentlist/{doctorId}")
	public String getAppointmentListByDoctorid(Model model, @PathVariable("doctorId") int doctorId,
			HttpServletRequest request) {
		List<Appointment> appointment = appointmentService.getAllAppointment();
		HttpSession session = request.getSession();
		if (session.getAttribute("DocId") == null) {
			return "redirect:/";
		}
		log.debug("===========Appointment List=============");
		log.debug("Appointment : " + appointment);
		List<Appointment> appointmentList = appointmentService.getAllActiveAppointmentsByDoctorId(doctorId);
		if (appointmentList.size() == 0) {
			model.addAttribute("nodata", "No Appointments for today");
		}
		session.setAttribute("drname", doctorService.GetDocotorById(doctorId).getDoctorName());
		model.addAttribute("from", LocalDate.now());
		model.addAttribute("to", LocalDate.now());
		model.addAttribute("appList", appointmentList);
		model.addAttribute("doctorid", doctorId);
		model.addAttribute("meetingLink", "");
		return "patientAppointmentList";
	}
	@GetMapping(value = "/serviceappointmentlist/{contractEmployeeId}")
	public String specialServiceAppointmentList(Model model, @PathVariable("contractEmployeeId") int contractEmployeeId,
			HttpServletRequest request) {
		List<ServiceAppointment> list = serviceAppointmentService.getServiceApplointmentListBetweenDatesByContractId(
				LocalDate.now().toString(), LocalDate.now().toString(), contractEmployeeId);
		log.debug("===========Appointment List=============");
		log.debug("Appointment : " + list);
		if (list.size() == 0) {
			model.addAttribute("nodata", "No Appointments for today");
		}
		model.addAttribute("from", LocalDate.now());
		model.addAttribute("to", LocalDate.now());
		model.addAttribute("appList", list);
		model.addAttribute("contractempid", contractEmployeeId);
		return "specialServiceAppointmentList";

	}
	@GetMapping(value = "/insert/{doctorId}/{patientId}/{from}/{to}")
	public String meetingLinkForAppointment(Model model, @PathVariable long pid, @PathVariable("doctorId") int doctorid,
			@PathVariable("patientId") int patientid, @PathVariable("from") String from,
			@PathVariable("to") String to) {
		Appointment appointment = appointmentService.getAppointmentById(pid);
		log.debug("appointment" + appointment);
		model.addAttribute("doctorid", doctorid);
		model.addAttribute("patientid", patientid);
		model.addAttribute("from", from);
		model.addAttribute("to", to);
		model.addAttribute("pid", appointment.getAppointmentId());
		model.addAttribute("appointment", appointment);
		return "doctorConsultantLink";
	}
	@GetMapping(value = "/deleteappointment/{doctorId}/{appointmentId}/{from}/{to}")
	public String deleteAppointmentById(@PathVariable("doctorId") int doctorid,
			@PathVariable("appointmentId") long appointmentId, @PathVariable("from") String from,
			@PathVariable("to") String to) {
		Appointment inactive = appointmentService.getAppointmentById(appointmentId);
		inactive.setIsactive('N');
		appointmentService.updateAppointment(inactive);
		return "redirect:/appointment/getallappointments/" + doctorid + "?from=" + from + "&to=" + to;
	}

	@RequestMapping(value = "/addappointment/{hospitalBranchId}/{patientId}/{doctorId}/{date}/{slot}", method = RequestMethod.GET)
	public String appointmentPaymentSuccessfull(Model model, @PathVariable("slot") String slot,
			@PathVariable("date") String date, @PathVariable("patientId") int patientId,
			@PathVariable("doctorId") int doctorId, @PathVariable("hospitalBranchId") int hospitalBranchId,
			HttpServletRequest request, Appointment appointment, HttpSession session) {
		if (session.getAttribute("appid") != null) {
			appointment.setAppointmentId((long) session.getAttribute("appid"));
			session.setAttribute("appid", null);
		}
		String dates = date;
		String slots = slot;
		model.addAttribute("slot", slots);
		model.addAttribute("date", dates);
		Doctor doctor = doctorService.GetDocotorById(doctorId);
		HospitalBranch hospitalBranch = hospitalBranchService.getHospitalbranchId(hospitalBranchId);
		Patients patient = patientsService.getPatientById(patientId);
		log.debug("============Add Appointment============");
		log.debug("AddAppointment : " + patient);
		model.addAttribute("hospitalBranch", hospitalBranch);
		model.addAttribute("patient", patient);
		model.addAttribute("doctor", doctor);
		model.addAttribute("appointment", appointment);
		model.addAttribute("slot", slot);
		model.addAttribute("endslot", LocalTime.parse(slot).plusMinutes(30));
		model.addAttribute("date", date);
		model.addAttribute("consultantType", "");
		model.addAttribute("appmsg", "Payment Successfully done!, Please select consultant type");
		return "appointmentform";
	}

	@RequestMapping(value = "/saveappointment/{doctorId}", method = RequestMethod.POST)
	public String saveAppointment(Model model, @PathVariable("doctorId") int doctorId, Appointment appointment,
			Payment payment) {
		appointment.setPaymentStatus("paid");
		appointmentService.updateAppointment(appointment);
		payment.setDate(LocalDateTime.now().toString());
		payment.setDoctor_id(appointment.getDoctor().getDoctorId());
		payment.setPatient_id(appointment.getPatient().getPatientId());
		paymentRepository.save(payment);
		log.debug("Inserting :" + appointment);
		log.debug("=========Patient appointment Inserted=======");
		return "redirect:/appointment/appointmentbooked/" + appointment.getPatient().getPatientId() + "/" + doctorId + "/"
				+ appointment.getAppointmentId();
	}

	@RequestMapping(value = "/appointmentbooked/{patientId}/{doctorId}/{appointmentId}", method = RequestMethod.GET)
	public String patientAppointmentBooked(Model model, @PathVariable("patientId") int patientId,
			@PathVariable("doctorId") int doctorId, @PathVariable("appointmentId") int appointmentId,
			Appointment appointmentObj, HospitalBranch hBranchEntry) {
		model.addAttribute("appmsg", "Your Appointment is Booked");
		Doctor doctor = doctorService.GetDocotorById(doctorId);
		Appointment appointment = appointmentService.getAppointmentById(appointmentId);
		Patients patient = patientsService.getPatientById(patientId);
		HospitalBranch hb = hospitalBranchService.getHospitalbranchId(appointment.getHospitalBranchId());
		log.debug("============Add Appointment============");
		log.debug("AddAppointment : " + patient);
		model.addAttribute("patient", patient);
		model.addAttribute("endslot", appointment.getEndSlot());
		model.addAttribute("date", appointment.getDate());
		model.addAttribute("hospitalBranch", hb);
		model.addAttribute("doctor", doctor);
		model.addAttribute("appointment", appointment);
		model.addAttribute("date", appointment.getDate());
		model.addAttribute("slot", appointment.getSlot());
		model.addAttribute("consult", appointment.getConsultantType());
		return "appointmentform";
	}
	@RequestMapping(value = "/offlinesaveappointment/{doctorId}/{employeeId}", method = RequestMethod.POST)
	public String saveOfflineAppointment(Model model, @PathVariable("doctorId") int doctorId,
			@PathVariable("employeeId") int employeeId, Appointment appointment) {
		appointment.setIsactive('y');
		appointmentService.updateAppointment(appointment);
		log.debug("adding new offline appointment :" + appointment);
		log.debug("=========Patient appointment added=======");
		return "redirect:/appointment/offlineappointmentbooked/" + appointment.getPatient().getPatientId() + "/" + doctorId + "/"
				+ appointment.getAppointmentId() + "/" + employeeId;
	}
	@RequestMapping(value = "/offlineappointmentbooked/{patientId}/{doctorId}/{appointmentId}/{employeeId}", method = RequestMethod.GET)
	public String offlinePatientAppointmentBooked(Model model, @PathVariable("patientId") int patientId,
			@PathVariable("doctorId") int doctorId, @PathVariable("appointmentId") int appointmentId,
			@PathVariable("employeeId") int employeeId, Appointment appointmentObj, HospitalBranch HospitalBranch) {
		model.addAttribute("appmsg", "Your Appointment is Booked");
		Doctor doctor = doctorService.GetDocotorById(doctorId);
		model.addAttribute("did", doctor.getDoctorId());
		Patients patient = patientsService.getPatientById(patientId);
		log.debug("============Add Appointment============");
		Appointment appointment = appointmentService.getAppointmentById(appointmentId);
		log.debug("Offline patient : " + patient);
		log.debug("appointment : " + appointment);
		model.addAttribute("patient", patient);
		model.addAttribute("employeeId", employeeId);
		model.addAttribute("hospitalBranchName",
				hospitalBranchService.getHospitalbranchId(appointment.getHospitalBranchId()));
		model.addAttribute("hospitalBranchAddress", HospitalBranch.getHospitalBranchLocation());
		model.addAttribute("doctor", doctor);
		model.addAttribute("date", appointment.getDate());
		model.addAttribute("slot", appointment.getSlot());
		model.addAttribute("consult", appointment.getConsultantType());
		model.addAttribute("appointment", appointment);
		return "offlineAppointmentBooked";
	}
	@GetMapping("/rejectappointment/{appointmentId}")
	public String deletePatientAppointmentById(Model model, @PathVariable long appointmentId) {
		log.debug("=============Reject Appointment===============");
		log.debug("Rejected Appointment :" + appointmentService.getAppointmentById(appointmentId));
		appointmentService.deleteAppointmentById(appointmentId);
		return "redirect:/appointment/appointmentlist";
	}
	@PostMapping("/linksave/{appointmentId}")
	public String link(Model model, @PathVariable long appointmentId, Appointment appointment, Patients patientObj,
			final HttpServletRequest request, final HttpServletResponse response, HttpSession session,
			@RequestParam(name = "doctorid") int doctorid, @RequestParam(name = "from") String from,
			@RequestParam(name = "to") String to) {
		Appointment appointments = appointmentService.getAppointmentById(appointmentId);
		appointments.setLink(appointment.getLink());
		appointmentService.updateAppointment(appointments);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(appointments.getPatient().getPatientMailId());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("choppariabhilash@gmail.com");
		mailMessage.setText(
				"To confirm your account, please click here : " + "saveuser?patientMailId=" + appointments.getLink());
		emailSenderService.sendEmail(mailMessage);
		log.debug("meeting link = " + mailMessage);
		return "redirect:/appointment/getallappointments/" + doctorid + "?from=" + from + "&to=" + to;
	}
	@GetMapping("bookAppointment/{patientId}")
	public String bookAppointment(Model model, Appointment appointment, @PathVariable("patientId") int patientId,
			@RequestParam(name = "serviceList", required = false) List<String> serviceList, HttpSession session) {
		if (session.getAttribute("pId") == null) {
			log.debug("patient id expired");
			return "redirect:/";
		}
		Patients patient = patientsService.getPatientById(patientId);
		if (patient.getFirstName() == "" || patient.getPatientMailId() == "" || patient.getPatientAge() == 0
				|| patient.getPatientMobile() == 0) {
			model.addAttribute("pprofile", "First Complete your Profile");
			model.addAttribute("patientObj", patient);
			model.addAttribute("patientId", patientId);
			model.addAttribute("patientmsg", "Please, complete your profile to Book an Appointment");
			log.debug("patinet = "+patient);
			return "patientProfile";
		}
		List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
		model.addAttribute("serviceNames", serviceNames);
		model.addAttribute("patient", patient);
		model.addAttribute("appointment", appointment);
		log.debug("service Names  = "+serviceNames);
		return "patientAppointmentForm";
	}
	@RequestMapping("/selectedService")
	public String afterSelectingService(Model model, Appointment appointment, final HttpServletRequest request,
			HttpSession session, @RequestParam(name = "serviceList", required = false) List<String> serviceList) {
		String getQry = request.getParameter("getQry");
		String serviceName = request.getParameter("serviceName");
		if (getQry != null && getQry.equals("selectedService") && serviceName != null && serviceName != "") {
			List<HospitalBranch> hospitalBranchList = hospitalBranchService
					.getHospitalBranchesByServiceName(serviceName);
			Patients patient = patientsService.getPatientById(appointment.getPatient().getPatientId());
			List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
			model.addAttribute("serviceNames", serviceNames);
			model.addAttribute("appointment", appointment);
			if (hospitalBranchList.size() != 0)
				model.addAttribute("hblist", hospitalBranchList);
			else
				model.addAttribute("hblist", "");
			model.addAttribute("serviceName", serviceName);
			model.addAttribute("patient", patient);
			model.addAttribute("hospitalBranchId", "0");
			log.debug("hospitalBranchList ="+hospitalBranchList);
		}
		return "patientAppointmentForm";
	}
	@RequestMapping("/selectedBranches")
	public String afterSelectingBranches(Model model, Appointment appointment, final HttpServletRequest request,
			HttpSession session, @RequestParam(name = "serviceList", required = false) List<String> serviceList,
			@RequestParam("serviceName") String serviceName) {
		String getQry = request.getParameter("getQry");
		String branchId = request.getParameter("branchId");
		if (getQry != null && getQry.equals("selectedService") && branchId != null && branchId != "") {
			List<HospitalBranch> hospitalBranchList = hospitalBranchService
					.getHospitalBranchesByServiceName(serviceName);
			Patients patient = patientsService.getPatientById(appointment.getPatient().getPatientId());
			List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
			HospitalBranch hospitalBranch = hospitalBranchService.getHospitalbranchId(Integer.parseInt(branchId));
			List<Doctor> doctorNames = doctorRepository.findByDoctorBranchId(branchId, serviceName);
			model.addAttribute("doctorNames", doctorNames);
			model.addAttribute("branchName", hospitalBranch.getHospitalBranchName());
			model.addAttribute("serviceNames", serviceNames);
			model.addAttribute("appointment", appointment);
			model.addAttribute("hblist", hospitalBranchList);
			model.addAttribute("serviceName", serviceName);
			model.addAttribute("patient", patient);
			model.addAttribute("hospitalBranchId", hospitalBranch.getHospitalBranchId());
			log.debug("doctorNames = "+doctorNames);
		}
		return "patientAppointmentForm";
	}

	@RequestMapping("/selectedDoctors")
	public String selectAppointmentSlot(Model model, Appointment appointment, final HttpServletRequest request,
			HttpSession session, @RequestParam(name = "serviceList", required = false) List<String> serviceList,
			@RequestParam(name = "serviceName", required = false) String serviceName,
			@RequestParam(name = "hospitalBranchId", required = false) int branchId) {
		try {
			String getQry = request.getParameter("getQry");
			String doctorId = request.getParameter("doctorId");
			String hospitalBranchId = request.getParameter("hospitalBranchId");
			Doctor doctor = doctorService.GetDocotorById(Integer.parseInt(doctorId));
			model.addAttribute("doctor", doctor);
			log.debug("doctor ="+doctor);
			if (getQry != null && getQry.equals("selectedDoctors") && doctorId != null && doctorId != "") {
				return "redirect:/Viewdoctors/" + Integer.parseInt(hospitalBranchId) + "/"
						+ appointment.getPatient().getPatientId() + "/" + Integer.parseInt(doctorId);
			}
		} catch (Exception e) {
			log.error(e);
		}
			List<HospitalBranch> hospitalBranchList=hospitalBranchService.getHospitalBranchesByServiceName(serviceName);
			int pid = appointment.getPatient().getPatientId();
			Patients patient = patientsService.getPatientById(pid);
			List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
			HospitalBranch hospitalBranch = hospitalBranchService.getHospitalbranchId(branchId);
			String hbid = Integer.toString(branchId);
			List<Doctor> doctorNames = doctorRepository.findByDoctorBranchId(hbid,serviceName);
			model.addAttribute("doctorNames", doctorNames);
			model.addAttribute("branchName", hospitalBranch.getHospitalBranchName());
			model.addAttribute("serviceNames", serviceNames);
			model.addAttribute("appointment", appointment);
			model.addAttribute("hblist", hospitalBranchList);
			model.addAttribute("serviceName", serviceName);
			model.addAttribute("patient", patient);
			return "patientAppointmentForm";
	}
	@RequestMapping(value = "patientappointments/{patientId}", method = RequestMethod.GET)
	public String patientAppointments(Model model, @PathVariable("patientId") int patientId, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (session.getAttribute("pId") == null) {
			return "redirect:/";
		}
		Patients patientobj = patientsService.getPatientById(patientId);
		List<Appointment> allAppointments = appointmentService.getAllAppointment();
		String from = request.getParameter("from");
		String to = request.getParameter("to");
		if (from != null && to != null) {
			List<Appointment> Appointments = appointmentRepository
					.applointmentListBetweenDatesByPatientId(patientobj.getPatientId(), from, to);
			List<Appointment> patientAppointments = appointmentService.getPatientAppointment(Appointments);
			if (patientAppointments.size() == 0)
				model.addAttribute("nodata", "No Appointments Found");
			model.addAttribute("to", to);
			model.addAttribute("from", from);
			model.addAttribute("patientappointments", patientAppointments);
			model.addAttribute("patient", patientobj);
			log.debug("patientAppointments = "+patientAppointments);
			return "patientAppointments";
		} else {
			List<Appointment> Appointments = allAppointments.stream()
					.filter(a -> a.getPatient().getPatientId() == patientobj.getPatientId())
					.collect(Collectors.toList());
			List<Appointment> patientAppointments = appointmentService.getPatientAppointment(Appointments);
			if (patientAppointments.size() == 0)
				model.addAttribute("nodata", "No Appointments Found");
			model.addAttribute("patientappointments", patientAppointments);
			model.addAttribute("patient", patientobj);
			log.debug("patientAppointments = "+patientAppointments);
		}
		return "patientAppointments";
	}
	@RequestMapping(value = "patientappointmentview/{appointmentId}", method = RequestMethod.GET)
	public String patientAppointmentView(Model model, @PathVariable("appointmentId") int appointmentId) {
		Appointment appointment = appointmentService.getAppointmentById(appointmentId);
		model.addAttribute("hospital", hospitalBranchService.getHospitalbranchId(appointment.getHospitalBranchId()));
		model.addAttribute("appointment", appointment);
		log.debug("appointment = "+appointment);
		return "patientAppointmentView";
	}
	@RequestMapping(value = "patientjoinmeet/{appointmentId}", method = RequestMethod.GET)
	public String patientJoinMeet(Model model, @PathVariable("appointmentId") int appointmentId) {
		Appointment appointment=appointmentService.getAppointmentById(appointmentId);
		LocalDate enddate = LocalDate.parse(appointment.getDate());
		LocalTime slot = LocalTime.parse(appointment.getSlot());
		log.debug("patient appointment= "+appointment);
		if (enddate.equals(LocalDate.now())) {
			if (LocalTime.now().isAfter(slot) && LocalTime.now().isBefore(slot.plusMinutes(10))) {
				return "redirect:" + appointmentService.getAppointmentById(appointmentId).getLink();
			}
		}
		return "redirect:/patientappointments/"
				+ appointmentService.getAppointmentById(appointmentId).getPatient().getPatientId();
	}
	@RequestMapping(value = "appointmentpayment", method = RequestMethod.GET)
	public String appointmentPayment(Model model, HttpServletRequest request) {
		String date = request.getParameter("date");
		String slot = request.getParameter("slot");
		String hospitalBranchId = request.getParameter("hbid");
		String patientId = request.getParameter("patientId");
		String doctorId = request.getParameter("doctorId");
		HospitalBranch hospitalBranch = hospitalBranchService.getHospitalbranchId(Integer.parseInt(hospitalBranchId));
		Patients patient = patientsService.getPatientById(Integer.parseInt(patientId));
		Doctor doctor = doctorService.GetDocotorById(Integer.parseInt(doctorId));
		model.addAttribute("hospital", hospitalBranch);
		model.addAttribute("patient", patient);
		model.addAttribute("doctor", doctor);
		model.addAttribute("slot", slot);
		model.addAttribute("date", date);
		log.debug("appoitment Payment details= "+hospitalBranch.getHospitalBranchName()+" "+patient.getPatientMailId()+" "+doctor.doctorName+slot+" "+date);
		return "appointmentpayment";
	}
	@RequestMapping(value = "appointmentService", method = RequestMethod.GET)
	public String appointmentForServices(Model model, HttpServletRequest request) {
		String date = request.getParameter("date");
		String slot = request.getParameter("slot");
		String patientId = request.getParameter("patientId");
		String contractServiceId = request.getParameter("conserviceId");
		Patients patient = patientsService.getPatientById(Integer.parseInt(patientId));
		ContractService contractServices = contractServicesService.getByConServiceId(Integer.parseInt(contractServiceId));
		model.addAttribute("patient", patient);
		model.addAttribute("patientId", patient.getPatientId());
		model.addAttribute("slot", slot);
		model.addAttribute("date", date);
		model.addAttribute("contract", contractServices);
		model.addAttribute("contractserviceId", contractServices.getConserviceId());
		model.addAttribute("servicename", contractServices.getServiceName());
		log.debug("Hospital Service appointment = "+contractServices.getServiceName()+" "+patient.getPatientMailId()+" "+date+" "+slot);
		return "appointmentservices";
	}
}
