package com.vcare.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vcare.beans.ContractEmployee;
import com.vcare.beans.ContractService;
import com.vcare.beans.Patients;
import com.vcare.beans.ServiceAppointment;
import com.vcare.repository.ServiceAppointmentRepository;
import com.vcare.service.ContractEmployeesService;
import com.vcare.service.ContractServicesService;
import com.vcare.service.EmailSenderService;
import com.vcare.service.PatientsService;
import com.vcare.service.ServiceAppointmentService;

@Controller
@RequestMapping("/ServiceAppoinement")
public class ServiceAppointmentController {
	
	@Autowired
	EmailSenderService emailSenderService;
	@Autowired
	ServiceAppointmentService serviceAppointmentService;
	@Autowired
	PatientsService patientsService;
	@Autowired
	ContractEmployeesService contractEmployeesService;
	@Autowired
	ServiceAppointmentRepository serviceAppointmentRepository;
	@Autowired
	ContractServicesService contractServicesService;
	
	static Logger log = Logger.getLogger(ServiceAppointmentController.class.getClass());

	 
	@RequestMapping(value = "saveservice/{pid}/{csid}/{date}/{slot}" ,method = RequestMethod.POST)
	public String saveSpecialService(Model model,@PathVariable("pid") int pid,@PathVariable("csid") int csid,@PathVariable("slot") String slot,@PathVariable("date") String date, ServiceAppointment serviceAppointment,HttpServletRequest request) {
		serviceAppointment.setDate(date);
		serviceAppointment.setSlot(slot);
		serviceAppointment.setPaymentstatus("paid");
		serviceAppointment.setServiceappmntstatus("hold");
		serviceAppointmentService.addServiceAppointment(serviceAppointment);
		String Date = request.getParameter("date");
		String Slot = request.getParameter("slot");
		String Pid = request.getParameter("patientId");
		String Csid = request.getParameter("csid");
		log.debug("==============save service appointment method ==============");
		Patients p=patientsService.getPatientById(pid);
		ContractService contractServices =contractServicesService.getByConServiceId(csid);
		model.addAttribute("patient",p);
		model.addAttribute("patientId",p.getPatientId());
		model.addAttribute("slot",slot);
		model.addAttribute("date",date);
		model.addAttribute("contract",contractServices);
		model.addAttribute("contractserviceId",contractServices.getConserviceId());	
		model.addAttribute("servicename",contractServices.getServiceName());
		model.addAttribute("servicemessage", "AppointmentBooked");
		return"appointmentservices";
	}
	 @GetMapping("/viewserviceppointments/{comempid}")
	 public String viewServiceappoinments(Model model,ContractEmployee contractEmployees,@PathVariable("comempid") int comempid,HttpServletRequest request ) {
		 log.debug("=====================View service appointment method =====");
		 List<ServiceAppointment> listserviceappoinment = serviceAppointmentService.getAllServiceAppointment();
		 String from = request.getParameter("from");
			String to = request.getParameter("to");
			if (from != null && to != null) {
				List<ServiceAppointment> serviceAppointment=serviceAppointmentRepository.serviceapplointmentListBetweenDates(from, to);
				model.addAttribute("to", to);
				model.addAttribute("from", from);
		 model.addAttribute("serviceappointlist", listserviceappoinment);
		 model.addAttribute("contractempid", comempid);
		 return"offerappointmentlist";
			}
			model.addAttribute("contractempid", comempid);
			 return"offerappointmentlist";
	 }
	 @GetMapping("/acceptappointment/{ceid}/{csappid}")
	 public String specialServiceStatusChange(Model model,ContractEmployee contractEmployees,@PathVariable("ceid") int ceid,@PathVariable("csappid") int csappid) {
		 log.debug("===================== specialServiceStatusChange method ==============");
		 ServiceAppointment serviceappointment = serviceAppointmentService.getByServiceAppointmentId(csappid);
		 ContractEmployee ce= contractEmployeesService.getContactEmployeeById(ceid);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(serviceappointment.getPatients().getPatientMailId());
		mailMessage.setSubject("Complete Registration!");
		mailMessage.setFrom("choppariabhilash@gmail.com");
		mailMessage.setText(
				" confirm your service");
		emailSenderService.sendEmail(mailMessage);
		model.addAttribute("Emailid", serviceappointment.getPatients().getPatientMailId()
				);
		 serviceappointment.setContractEmployees(ce) ;
		 serviceappointment.setServiceappmntstatus("Accepted");
		 serviceAppointmentService.UpdateServiceAppointment(serviceappointment);
		 List<ServiceAppointment> listserviceappoinment = serviceAppointmentService.getAllServiceAppointment();
		 model.addAttribute("serviceappointlist", listserviceappoinment);
		 model.addAttribute("contractempid", ceid);
		 return"offerappointmentlist";
	 }
}
