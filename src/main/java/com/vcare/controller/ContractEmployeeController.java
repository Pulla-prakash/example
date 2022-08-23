package com.vcare.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vcare.beans.Aboutus;
import com.vcare.beans.ContractEmployees;
import com.vcare.beans.popularOffers;
import com.vcare.repository.ContractEmployeeRepository;
import com.vcare.service.ContractEmployeesService;
import com.vcare.service.popularOfferService;
import com.vcare.utils.VcareUtilies;

@Controller
public class ContractEmployeeController {
	
	@Autowired
	
	ContractEmployeesService  ContractEmployeeService;
	
	@Autowired
	popularOfferService popularOfferService;
	
	@Autowired
	
	ContractEmployeeRepository   contractEmployeeRepository ;
	

	@RequestMapping(value = ("/addcontractEmployee/{id}"), method = RequestMethod.GET)
	public String newAbout(Model model,@PathVariable("id") int id, ContractEmployees  contractEmployees,HttpServletRequest request) {
		
		popularOffers popularOffers =popularOfferService.getoOfferById(id);
		HttpSession session=request.getSession();
		session.setAttribute("sessionId", popularOffers.getId());
	 //  model.addAttribute("offerid", popularOffers.getId());
		model.addAttribute("objContract", contractEmployees);
		model.addAttribute("objContractId", contractEmployees.getId());

		return "contractemployeeform";

	}
	
	
	@RequestMapping(value = "/saveContract", method = RequestMethod.POST)
	public String addDepartment(Model model, @ModelAttribute(value = "objContract") ContractEmployees  contractEmployees) {
		model.addAttribute("contractObj", contractEmployees);
	LocalDate currentDate = LocalDate.now();
	System.err.println("date od birth"+contractEmployees.getDOB());
       Period period = Period.between(contractEmployees.getDOB(), currentDate);
        contractEmployees.setAge(period.getYears() + " years " + period.getMonths() + " months " + period.getDays() + " days");
        ContractEmployeeService.sendSimpleEmail(contractEmployees.getEmail(),"Login creadentials here : "
                +"Email&password"+" "+"UserMailId="+contractEmployees.getEmail()+"&password="+contractEmployees.getPassword(),"login credentials");
        String strEncPassword = VcareUtilies.getEncryptSecurePassword(contractEmployees.getPassword(), "vcare");
        contractEmployees.setPassword(strEncPassword);
		ContractEmployeeService.addContactEmployee(contractEmployees);
		List<ContractEmployees> empList = ContractEmployeeService.getAllContactEmployee();
		model.addAttribute("contractemplist", empList);
		return "redirect:/contractemployeelist";
	}
	
	
	@GetMapping("/contractemployeelist")
	public String getAllAbouts(Model model, Aboutus aboutus) {

		List<ContractEmployees> listAbout = ContractEmployeeService.getAllContactEmployee();

//		List<Aboutus> list = new ArrayList<>();
//		for (Aboutus about : listAbout) {
//			if (about.getIsActive() == 'Y' || about.getIsActive() == 'y') {
//				list.add(about);
//			}
//		}

		model.addAttribute("contractemplist", listAbout);
		return "contractemployeelist";
	}
	
	
	  @GetMapping("/editcontractemp/{id}") public String getById(Model
	  model, @PathVariable("id") int id,ContractEmployees  contractEmployees) {
	  
	  ContractEmployees objSecAbout = ContractEmployeeService.getContactEmployeeById(id);
	  
	  model.addAttribute("age",contractEmployees.getAge());
	  model.addAttribute("objContract", objSecAbout);
	  
	  return "contractemployeeform";
	  
	  }
	  
	  @GetMapping("/deletecontractemp/{id}") public String deleteService(Model
	  model, @PathVariable int id) { 
		  ContractEmployeeService.deleteContactEmployeeId(id);
	  //ContractEmployees inactive = ContractEmployeeService.getContactEmployeeById(id);
	  List<ContractEmployees> listAbout = ContractEmployeeService.getAllContactEmployee();
	  model.addAttribute("contractemplist", listAbout);
	  
	  return "redirect:/contractemployeelist";
	  
	  }
	 
	  @GetMapping("/contractlogin")
		public String Validation(Model model, @ModelAttribute(value = "objContract") ContractEmployees objContractEmp) {
			
			
//			  if
//			  (!objContractEmp.getCaptchaContract().equals(objContractEmp.getUserCaptcha())
//			  ) { session.setAttribute("indexmsg","logininvalidcaptcha"); return
//			  "redirect:/"; }
			 
			model.addAttribute("warninig", "invalid credentials..please try to login again...!");
			System.err.println("email::::::::::::::"+objContractEmp.getEmail());
			String str = VcareUtilies.getEncryptSecurePassword(objContractEmp.getPassword(), "vcare");
			ContractEmployees signinObj = ContractEmployeeService.getcontractEmployee(objContractEmp.getEmail(), str);
			model.addAttribute("objContract", signinObj);
			if (signinObj != null) {
				model.addAttribute("objContract",signinObj );
				return "successfulRegistration";
			} else {
				//session.setAttribute("indexmsg","logininvalidpasswordorusername");
				return "redirect:/";
			}

		}
	  
	  
	  
	  @GetMapping("/service/{Pid}")
	  public String serviceById(Model model,@PathVariable("Pid") int Pid) {
		  
		 List<ContractEmployees> offers=contractEmployeeRepository.empDriverList(Pid);	  
		  System.err.println("list of employee:::::"+offers);
		 model.addAttribute("offer", offers);
		  
		  return"popularservices";
	  }

}
