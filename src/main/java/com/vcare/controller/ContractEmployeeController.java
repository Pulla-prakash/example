package com.vcare.controller;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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
import com.vcare.beans.Appointment;
import com.vcare.beans.ContractEmployees;
import com.vcare.beans.Patients;
import com.vcare.beans.Rating;
import com.vcare.beans.popularOffers;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.ContractEmployeeRepository;
import com.vcare.service.ContractEmployeesService;
import com.vcare.service.PatientsService;
import com.vcare.service.popularOfferService;
import com.vcare.utils.VcareUtilies;

@Controller
public class ContractEmployeeController {
	
	@Autowired
	
	ContractEmployeesService  ContractEmployeeService;
	
	@Autowired
	popularOfferService popularOfferService;
	
	@Autowired
	PatientsService patientsService;
	
	@Autowired
	AppointmentRepository appointmentRepository;
	
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
		 model.addAttribute("id",Pid);
		  return"popularservices";
	  }
	  
	  @GetMapping("/slotsServices/{pid}/{cid}")
	  public String slotsByServices(Model model,@PathVariable("pid")int pid,@PathVariable("cid") int cid) {
		  
			Patients patient = patientsService.getPatientById(pid);
			
		  model.addAttribute("patId", patient.getPatientId());
		  
		  ContractEmployees contractEmployees = ContractEmployeeService.getContactEmployeeById(cid);
		  
		  model.addAttribute("conId", contractEmployees.getId());
		  model.addAttribute("contract", contractEmployees);
		  
		  LocalDate sd=LocalDate.now();
		  LocalDate ed=sd.plusDays(5);
		  List<String> times = new ArrayList<>();
		  Calendar calendar = Calendar.getInstance();
		  int ti = calendar.get(Calendar.HOUR_OF_DAY);
	        int minutes = calendar.get(Calendar.MINUTE);
	        String[] quarterHours = { "00", "120", };
	        // System.out.println(LocalDate.now());
	        List<LocalDate> datesInRange = new ArrayList<>();
	        List<String> slots = new ArrayList<>();
	        System.out.println(minutes);
	        boolean isflag = false;
	        List<String> days = new ArrayList<>();
	        List<String> monthdate = new ArrayList<>();
	        HashMap<String, ArrayList<String>> map = new HashMap<>();
	        times = new ArrayList<>();
	        System.out.println("============" + sd);
	        DateFormat df = new SimpleDateFormat("HH:mm");
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.SECOND, 0);

	        while (sd.isBefore(ed)) {
	            datesInRange.add(sd);
	            sd = sd.plusDays(1);
	            String day = sd.format(DateTimeFormatter.ofPattern("EEE"));
	            String month = sd.format(DateTimeFormatter.ofPattern("MMM"));
	            System.out.println(day);
	            System.out.println(sd);
	            System.out.println(month);
	            String s = Integer.toString(sd.getDayOfMonth());
	            if (sd.isAfter(LocalDate.now()) || sd.isEqual(LocalDate.now())) {
	                days.add(day);
	                slots.add(month);
	                slots.add(s);
	                String slot = month.toString() + s;
	                monthdate.add(sd.toString());
	                model.addAttribute("date", monthdate);
	            }
	        }
	        int count = 0;
	        for (int i = 9; i < 13; i++) {
	            if (ti > 1) {
	                for (int j = 0; j < 2; j++) {
	                    if ((i == ti && minutes < Integer.parseInt(quarterHours[j])) || (i != ti) || isflag == true) {
	                        System.out.println("Integer.parseInt(quarterHours[j])=" + Integer.parseInt(quarterHours[j]));
	                        System.out.println("minutes=" + minutes);
	                        System.out.println("ti=" + ti);
	                        isflag = true;
	                        String time = i + ":" + quarterHours[j];
	                        if (i < 24) {
	                            time = "" + time;
	                        }
	                        String hourFormat = i + ":" + quarterHours[j];
	                        if (i < 12) {
	                            hourFormat = time + " AM";
	                        } else
	                            hourFormat = time + " PM";
	                        String timeslot = hourFormat;
	                        System.out.println("++++++++++" + timeslot);
	                        times.add(timeslot);
	                    }
	                    map.put(sd.toString(), (ArrayList<String>) times);
	                }
	            }
	            System.out.println("============count=============" + count);
	            model.addAttribute("count", count);
	        }
	        model.addAttribute("times", "");
	        model.addAttribute("map", map);
	        model.addAttribute("monthdate", monthdate);
	        model.addAttribute("days", days);
	        model.addAttribute("selectedDate", "");

	        
		  return "servicesslots";
	  }
	  
	 @GetMapping("/ViewSlots")
	 
		 public String slotsContract(Model model, Date timeStart, Date timeEnd, Rating drating,
					HttpServletRequest request) throws Exception {
				String  cid = request.getParameter("id");
				String pid = request.getParameter("patientId");
				String Date = request.getParameter("date");
				Patients patient = patientsService.getPatientById(Integer.parseInt(pid));
				model.addAttribute("pid", patient.getPatientId());
				ContractEmployees contract = ContractEmployeeService.getContactEmployeeById(Integer.parseInt(cid));
				model.addAttribute("contract", contract);
				model.addAttribute("patientname", patient.getFirstName());
				
				
				model.addAttribute("patId", patient.getPatientId());
				model.addAttribute("doc", contract);
				double average;
				long totalrating;
				// Getting Average rating for doctor
				
				// Formatting the date from html format to java
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
				DateFormat formatterDay = new SimpleDateFormat("EEE");// gets week days as MON,TUE....
				// getting start and end timings from DoctorAvailability and setting to Date
				// Separating Start and End Dates to year,month,date
				LocalDate sd = LocalDate.now();
				LocalDate ed = sd.plusDays(5);
				//System.out.println("sdf" + formatterDay.format(timeStart));
				List<String> times = new ArrayList<>();// To store slots
				Calendar calendar = Calendar.getInstance();// get present calendar
				calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
				int minutes = calendar.get(Calendar.MINUTE);
				// to Store dates
				List<LocalDate> datesInRange = new ArrayList<>();
				//
				List<String> slots = new ArrayList<>();
				// to store week days
				List<String> days = new ArrayList<>();
				// to store dates
				List<String> monthdate = new ArrayList<>();
				/* HashMap<String, ArrayList<String>> map = new HashMap<>(); */
				times = new ArrayList<>();
				System.out.println("============" + sd);
				// stores only hour of endtime
			//	int endHour = timeEnd.getHours();
				// Setting the time to Hours:Minutes
				DateFormat df = new SimpleDateFormat("HH:mm");
				// sets start-time hour and minutes and seconds
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY,9);
				cal.set(Calendar.MINUTE, 30);
				cal.set(Calendar.SECOND, 0);
				// ======================== //Getting the Selected dated from URL
				LocalDate selectedDate = LocalDate.parse(request.getParameter("date"));
				LocalDate today = LocalDate.parse(request.getParameter("date"));
				System.out.println("today=" + today);
				System.out.println("selectedDate=" + selectedDate);
				// STORES SLOTS
				ArrayList<String> timeList = new ArrayList<>();
				if (selectedDate.equals(null)) {
					model.addAttribute("selectedDate", "");// Before selecting date
				} else {
					model.addAttribute("selectedDate", selectedDate);// After Selecting date
				}
				Calendar c = Calendar.getInstance();
				// System.out.println("+++++DF++++++" + df.format(cal.getTime())); /*String t =
				// timeStart.getHours() + ":" + timeStart.getMinutes();
				// System.out.println("=========T" + t);
				String n = (c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)).toString();// df.format(LocalTime.now()).toString();
				System.out.println("+++++++++N" + n); // Present calender for getting todays data
				Calendar calObj = Calendar.getInstance();
				// Gets listOfAppointmnetByDoctorId&Date
				List<Appointment> app = appointmentRepository.appointmentListByDoctorid(Integer.parseInt(cid),
						request.getParameter("date"));
				// For storing the booked slots as string
				ArrayList<String> totalSlots = new ArrayList<>();
				for (Appointment a : app) {
					totalSlots.add(a.getSlot());
				}
				// For Present Day Slots
				int amount = 60;
				//Array list For Storing the booked slots
				ArrayList<String> bookedSlots=new ArrayList<>();
				while (LocalDate.now().equals(selectedDate)) {
					System.out.println(df.format(cal.getTime()));
					if (cal.getTime().getHours() == 20) {
						System.out.println("todayIf=" + today);
						break;
					} else {
						// if no Appointments are booked for present day
						if (app.size() == 0) {
							// Displaying slots for only current hour
							if (cal.getTime().getHours() == calObj.get(Calendar.HOUR_OF_DAY)) {
								if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE)) {
									timeList.add(df.format(cal.getTime()));
									model.addAttribute("times", timeList);
								}
							}
						}
						// if Appointments are booked for present day
						else {
							// For "Not Displaying" the slots booked
							if (totalSlots.contains(df.format(cal.getTime()).toString())) {
								//adding the booked slots
								bookedSlots.add(df.format(cal.getTime()).toString());
								System.out.println("app.contains(df.format(cal.getTime()).toString()=="
										+ totalSlots.contains(df.format(cal.getTime()).toString()));
								System.out
										.println("df.format(cal.getTime()).toString()==" + df.format(cal.getTime()).toString());
								cal.add(Calendar.MINUTE, amount);
								continue;
							}
							// For Displaying the slots not booked
							else {
								if (cal.getTime().getHours() == calObj.get(Calendar.HOUR_OF_DAY)) {
									if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE)) {
										timeList.add(df.format(cal.getTime()));
										model.addAttribute("times", timeList);
									}
								}
							}
						}
						System.out.println("cal.add(Calendar.MINUTE, 15)==" + df.format(cal.getTime()));
						// Displaying slots for after current hour

						if (cal.getTime().getHours() > calObj.get(Calendar.HOUR_OF_DAY)) {
							if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE))
								System.out.println("cal.getTime()==" + cal.getTime());
							timeList.add(df.format(cal.getTime()));
							model.addAttribute("times", timeList);
						}
						cal.add(Calendar.MINUTE, amount);
					}
				} // For Slots from next-day
				while (!LocalDate.now().equals(selectedDate)) {
					if (cal.getTime().getHours() == 20) {
						today = today.minusDays(1);
						System.out.println("todayIf=" + today);
						break;
					} else {
						if (app.size() == 0) {
							timeList.add(df.format(cal.getTime()));
							cal.add(Calendar.MINUTE, amount);
							model.addAttribute("times", timeList);
						} else {
							if (totalSlots.contains(df.format(cal.getTime()).toString())) {
								System.out.println("cal.getTime()==" + cal.getTime());
								cal.add(Calendar.MINUTE, amount);
								continue;
							} else {
								timeList.add(df.format(cal.getTime()));
								cal.add(Calendar.MINUTE, amount);
								model.addAttribute("times", timeList);
							}
						}
					}
				}
				// Dates drop-down according to DoctorAvailability
				while (sd.isBefore(ed) || sd.equals(ed)) {
					datesInRange.add(sd);
					System.out.println("=======" + sd);
					String day = sd.format(DateTimeFormatter.ofPattern("EEE"));
					String month = sd.format(DateTimeFormatter.ofPattern("MMM"));
					String s = Integer.toString(sd.getDayOfMonth());
					if (sd.isAfter(LocalDate.now()) || sd.isEqual(LocalDate.now())) {
						days.add(day);
						slots.add(month);
						slots.add(s);
						String slot = month.toString() + s;
						monthdate.add(sd.toString());
						sd = sd.plusDays(1);
					} else {
						sd = sd.plusDays(1);
					}
				}
				ArrayList<String> totalslots=new ArrayList<>();
				if(bookedSlots.size()==0)
					model.addAttribute("bookedslots",0);
				else
					model.addAttribute("bookedslots",bookedSlots);
				totalslots.addAll(bookedSlots);
				totalslots.addAll(timeList);
				//model.addAttribute("times", timeList);
				model.addAttribute("totalslots", totalslots);
				/* model.addAttribute("map", map); */
				model.addAttribute("monthdate", monthdate);
				model.addAttribute("days", days);
				System.err.println("this is a date:::::"+Date);
				model.addAttribute("date", Date);
		 
		 return "servicesslots";
	 }

}
