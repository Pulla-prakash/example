package com.vcare.controller;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import com.vcare.beans.Aboutus;
import com.vcare.beans.ContractEmployee;
import com.vcare.beans.ContractService;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.ContractEmployeeRepository;
import com.vcare.service.ContractEmployeesService;
import com.vcare.service.ContractServicesService;
import com.vcare.service.PatientsService;
import com.vcare.service.ServiceAppointmentService;
import com.vcare.service.popularOfferService;
import com.vcare.utils.MailUtil;
import com.vcare.utils.VcareUtilies;

@Controller
@RequestMapping("/ContractEmployee")
public class ContractEmployeeController {
	
	@Autowired
	ContractEmployeesService  ContractEmployeesService;
	@Autowired
	popularOfferService popularOfferService;
	@Autowired
	PatientsService patientsService;
	@Autowired
	AppointmentRepository appointmentRepository;
	@Autowired
	ContractEmployeeRepository   contractEmployeeRepository ;
	@Autowired
	ServiceAppointmentService appointmentService;
	@Autowired
	ContractServicesService contractServicesService;
	@Autowired
	JavaMailSender MailSender;
	
	static Logger log=Logger.getLogger(ContractEmployeeController.class.getClass());

	@RequestMapping(value = ("/addcontractEmployee/{contractserviceId}"), method = RequestMethod.GET)
	public String addingNewContractEmplyee(Model model,@PathVariable("contractserviceId") int contractserviceId, ContractEmployee  contractEmployees,HttpServletRequest request) {
		ContractService contractServices=contractServicesService.getByConServiceId(contractserviceId);
		HttpSession session=request.getSession();
		session.setAttribute("sessionId", contractServices.getConserviceId());
	    model.addAttribute("conserviceid", contractServices.getConserviceId());
		model.addAttribute("objContract", contractEmployees);
		model.addAttribute("objContractId", contractEmployees.getId());
		return "contractemployeeform";
	}
	@Value("${app.name}")
	String applicationName;
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/saveContract", method = RequestMethod.POST)
	public String saveContractEmployee(Model model, @ModelAttribute(value = "objContract") ContractEmployee  contractEmployees) {
		model.addAttribute("contractObj", contractEmployees);
    	LocalDate currentDate = LocalDate.now();
	   log.debug("date od birth"+contractEmployees.getDOB());
       Period period = Period.between(contractEmployees.getDOB(), currentDate);
        contractEmployees.setAge(period.getYears() + " years " + period.getMonths() + " months " + period.getDays() + " days");
        log.info("hello this is a mail"+contractEmployees.getEmail());
		  MailUtil.sendSimpleEmail(MailSender, contractEmployees.getEmail()
		  ,"Login creadentials here : "
		  +"Email&password"+" "+"UserMailId="+contractEmployees.getEmail()+"&password="
		  +contractEmployees.getPassword(),"login credentials");
		 
        String strEncPassword = VcareUtilies.getEncryptSecurePassword(contractEmployees.getPassword(), applicationName);
        contractEmployees.setPassword(strEncPassword);
        ContractEmployeesService.addContactEmployee(contractEmployees);
		List<ContractEmployee> empList = ContractEmployeesService.getAllContactEmployee();
		model.addAttribute("contractemplist", empList);
		return "redirect:/ContractEmployee/contractemployeelist";
	}
	@GetMapping("/contractemployeelist")
	public String getAllContractEmployee(Model model, Aboutus aboutus) {
		List<ContractEmployee> objContractEmployee = ContractEmployeesService.getAllContactEmployee();
		model.addAttribute("contractemplist", objContractEmployee);
		return "contractemployeelist";
	}
	  @GetMapping("/editcontractemp/{contractemployeeId}")
	  public String editContractEmployeeById(Model model, @PathVariable("contractemployeeId") int contractemployeeId,ContractEmployee  contractEmployees) {
	  ContractEmployee objSecAbout = ContractEmployeesService.getContactEmployeeById(contractemployeeId);
	  model.addAttribute("age",contractEmployees.getAge());
	  model.addAttribute("objContract", objSecAbout);
	  return "contractemployeeform";
	  }
	  @GetMapping("/deletecontractemp/{contractemployeeId}")
	  public String deleteContractEmplyee(Model model, @PathVariable("contractemployeeId") int contractemployeeId) { 
		  ContractEmployee objcontrectemp = ContractEmployeesService.getContactEmployeeById(contractemployeeId);
		  objcontrectemp.setIsactive('N');
		  ContractEmployeesService.addContactEmployee(objcontrectemp);
	  return "redirect:/ContractEmployee/contractemployeelist";
	  }
	  @GetMapping("/contractlogin")
		public String validationForContractEmployee(Model model, @ModelAttribute(value = "objContract") ContractEmployee objContractEmp) {
			model.addAttribute("warninig", "invalid credentials..please try to login again...!");
			log.debug("email::::::::::::::"+objContractEmp.getEmail());
			String str = VcareUtilies.getEncryptSecurePassword(objContractEmp.getPassword(), "vcare");
			ContractEmployee signinObj = ContractEmployeesService.getcontractEmployee(objContractEmp.getEmail(), str);
			model.addAttribute("objContract", signinObj);
			if (signinObj != null) {
				model.addAttribute("objContract",signinObj );
				model.addAttribute("contractempid", signinObj.getId());
				return "contractempdashboard";
			} else {
				return "redirect:/";
			}
		}
	  @RequestMapping(value = "/forgotContract", method = RequestMethod.GET)
	    public ModelAndView UserforgotPasswordPage(ContractEmployee contractEmployees) {
	        log.debug("entered into user/controller::::forgot paswword method");
	        ModelAndView mav = new ModelAndView("contractupdatepass");
	        mav.addObject("empObj", contractEmployees);
	        return mav;
	    }
	   @PostMapping(value = "/validateContractEmp")
	    public String checkMailIdForContractEmployee(Model model, ContractEmployee contractEmployees) {
	        model.addAttribute("warninig", "invalid credentials..please try to login again...!");
	        log.debug("entered into user/controller::::check EmailId existing or not");
	        log.debug("UI given mail Id:" + contractEmployees.getEmail());
	        ContractEmployee objEmp = ContractEmployeesService.findByMail(contractEmployees.getEmail());
	        if (objEmp != null) {
	            String s1 = "";
	            model.addAttribute("message", s1);
	            log.debug("UI given mail Id:" + objEmp.getEmail());
	            model.addAttribute("empObj", contractEmployees);
	            return "contractpresentpass";
	        } else {
	            log.debug("Invalid Mail");
	            String s1 = "Email-Id Not Exists";
	            model.addAttribute("message", s1);
	            model.addAttribute("empObj", new ContractEmployee());
	            return "";
	        }
	    }
	   @RequestMapping(value = "/updateContractEmp", method = RequestMethod.POST)
	    public String updateContractEmployeePassword(Model model, @ModelAttribute("empObj") ContractEmployee contractEmployees,HttpSession session) {
		   ContractEmployee pUser = ContractEmployeesService.findByMail(contractEmployees.getEmail());
	        log.debug("inside updateClientPassword after update id is:::" + contractEmployees.getEmail());
	        String strEncPassword = VcareUtilies.getEncryptSecurePassword(contractEmployees.getPassword(), "vcare");
	        contractEmployees.setPassword(strEncPassword);
	        pUser.setPassword(contractEmployees.getPassword());
	        log.debug(contractEmployees.getPassword());
	        log.debug("in update method pUser:: name " + pUser.getName());
	        ContractEmployeesService.updateContactEmployee(pUser);
	        log.debug("password is updated sucessfully");
	        model.addAttribute("warning", "Password is updated Successfully. Please Login!!");
	        log.debug("login page is displayed");
	        model.addAttribute("empObj", pUser);
	        session.setAttribute("indexmsg","updatepassword");
	        return "redirect:/";
	    }
}
