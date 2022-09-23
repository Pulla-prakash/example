package com.vcare.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

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

import com.vcare.beans.Aboutus;
import com.vcare.beans.Appointment;
import com.vcare.beans.ContractService;
import com.vcare.beans.Patients;
import com.vcare.beans.Rating;
import com.vcare.beans.popularOffers;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.ContractServiceRepository;
import com.vcare.service.ContractEmployeesService;
import com.vcare.service.ContractServicesService;
import com.vcare.service.PatientsService;
import com.vcare.service.popularOfferService;

@Controller
@RequestMapping("/contractService")
public class ContractServiceController {
	
	@Autowired
	ContractServicesService contractServicesService;
	@Autowired
	ContractEmployeesService contractEmployeesService;	
	@Autowired
	ContractServiceRepository contractServiceRepository;
	@Autowired
	popularOfferService popularOfferService;
	@Autowired
	PatientsService patientsService;
	@Autowired
	AppointmentRepository appointmentRepository;
	
	static Logger log=Logger.getLogger(ContractServiceController.class.getClass());
	
	@GetMapping("/offerservicelist")
	public String getAllServices(Model model, Aboutus aboutus) {
		List<ContractService> listservice = contractServicesService.getAllActiveConServices();
		model.addAttribute("serviceclist", listservice);
		return "offerservicelist";
	}
	@RequestMapping(value = ("/addcontractService/{offerid}"), method = RequestMethod.GET)
	public String addingServices(Model model, @ModelAttribute(value = "serviceObj")@PathVariable("offerid") int offerid, ContractService contractServices,HttpServletRequest request) {
		model.addAttribute("serviceObj", contractServices);
		model.addAttribute("offerid",offerid);
		HttpSession session=request.getSession();
		session.setAttribute("offerId", offerid);
		return "offersserviceform";
	}
	@RequestMapping(value = "/saveservice", method = RequestMethod.POST)
	public String saveContractServices(Model model, @ModelAttribute(value = "serviceObj") ContractService contractServices) {
		model.addAttribute("serviceObj", contractServices);
		contractServicesService.UpdateConServices(contractServices);
		return "redirect:/contractService/offerservicelist";
	}
	  @GetMapping("/service/{Pid}")
	  public String serviceById(Model model,@PathVariable("Pid") int Pid, ContractService  contractServices) {
		List<ContractService> conServices=contractServiceRepository.contractServList(Pid);
		 popularOffers popularOffers = popularOfferService.getoOfferById(Pid);
		  log.debug("list of employee:::::"+conServices);
		 model.addAttribute("offer", conServices);
		 log.debug("get Contract Service Id::::::"+contractServices.getConserviceId());
		 model.addAttribute("name", popularOffers.getName());
		 model.addAttribute("id",popularOffers.getId());
		  return"popularservices";
	  }
	  @GetMapping("/slotsServices/{pid}/{csid}")
	  public String slotsByServices(Model model,@PathVariable("pid")int pid,@PathVariable("csid") int csid) {
			Patients patient = patientsService.getPatientById(pid);
		  model.addAttribute("patId", patient.getPatientId());
		  ContractService contractServices = contractServicesService.getByConServiceId(csid);
		  model.addAttribute("conId", contractServices.getConserviceId());
		  model.addAttribute("contract", contractServices);
		  LocalDate sd=LocalDate.now();
		  LocalDate ed=sd.plusDays(5);
		  List<String> times = new ArrayList<>();
		  Calendar calendar = Calendar.getInstance();
		  int ti = calendar.get(Calendar.HOUR_OF_DAY);
	        int minutes = calendar.get(Calendar.MINUTE);
	        String[] quarterHours = { "00", "120", };
	        // log.debug(LocalDate.now());
	        List<LocalDate> datesInRange = new ArrayList<>();
	        List<String> slots = new ArrayList<>();
	        log.debug(minutes);
	        boolean isflag = false;
	        List<String> days = new ArrayList<>();
	        List<String> monthdate = new ArrayList<>();
	        HashMap<String, ArrayList<String>> map = new HashMap<>();
	        times = new ArrayList<>();
	        log.debug("============" + sd);
	        DateFormat df = new SimpleDateFormat("HH:mm");
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.SECOND, 0);
	        while (sd.isBefore(ed)) {
	            datesInRange.add(sd);
	            sd = sd.plusDays(1);
	            String day = sd.format(DateTimeFormatter.ofPattern("EEE"));
	            String month = sd.format(DateTimeFormatter.ofPattern("MMM"));
	            log.debug(day);
	            log.debug(sd);
	            log.debug(month);
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
	                        log.debug("Integer.parseInt(quarterHours[j])=" + Integer.parseInt(quarterHours[j]));
	                        log.debug("minutes=" + minutes);
	                        log.debug("ti=" + ti);
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
	                        log.debug("++++++++++" + timeslot);
	                        times.add(timeslot);
	                    }
	                    map.put(sd.toString(), (ArrayList<String>) times);
	                }
	            }
	            log.debug("============count=============" + count);
	            model.addAttribute("count", count);
	        }
	        model.addAttribute("times", "");
	        model.addAttribute("map", map);
	        model.addAttribute("monthdate", monthdate);
	        model.addAttribute("days", days);
	        model.addAttribute("selectedDate", "");
	        model.addAttribute("selecteddate", "");
		  return "servicesslots";
	  }
	 @GetMapping("/ViewSlots")
		 public String slotsContract(Model model, Date timeStart, Date timeEnd, Rating drating,
					HttpServletRequest request) throws Exception {
				String  cid = request.getParameter("conserviceId");
				String pid = request.getParameter("patientId");
				String Date = request.getParameter("date");
				Patients patient = patientsService.getPatientById(Integer.parseInt(pid));
				model.addAttribute("pid", patient.getPatientId());
				ContractService contract = contractServicesService.getByConServiceId(Integer.parseInt(cid));
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
				List<String> times = new ArrayList<>();// To store slots
				Calendar calendar = Calendar.getInstance();// get present calendar
				calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
				int minutes = calendar.get(Calendar.MINUTE);
				// to Store dates
				List<LocalDate> datesInRange = new ArrayList<>();
				List<String> slots = new ArrayList<>();
				// to store week days
				List<String> days = new ArrayList<>();
				// to store dates
				List<String> monthdate = new ArrayList<>();
				times = new ArrayList<>();
				log.debug("============" + sd);
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
				log.debug("today=" + today);
				log.debug("selectedDate=" + selectedDate);
				// STORES SLOTS
				ArrayList<String> timeList = new ArrayList<>();
				if (selectedDate.equals(null)) {
					model.addAttribute("selectedDate", "");// Before selecting date
				} else {
					model.addAttribute("selectedDate", selectedDate);// After Selecting date
				}
				Calendar c = Calendar.getInstance();
				String n = (c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE)).toString();
				log.debug("+++++++++N" + n); // Present calender for getting todays data
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
					log.debug(df.format(cal.getTime()));
					if (cal.getTime().getHours() == 20) {
						log.debug("todayIf=" + today);
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
								log.debug("app.contains(df.format(cal.getTime()).toString()=="
										+ totalSlots.contains(df.format(cal.getTime()).toString()));
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
						log.debug("cal.add(Calendar.MINUTE, 15)==" + df.format(cal.getTime()));
						// Displaying slots for after current hour

						if (cal.getTime().getHours() > calObj.get(Calendar.HOUR_OF_DAY)) {
							if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE))
								log.debug("cal.getTime()==" + cal.getTime());
							timeList.add(df.format(cal.getTime()));
							model.addAttribute("times", timeList);
						}
						cal.add(Calendar.MINUTE, amount);
					}
				} // For Slots from next-day
				while (!LocalDate.now().equals(selectedDate)) {
					if (cal.getTime().getHours() == 20) {
						today = today.minusDays(1);
						log.debug("todayIf=" + today);
						break;
					} else {
						if (app.size() == 0) {
							timeList.add(df.format(cal.getTime()));
							cal.add(Calendar.MINUTE, amount);
							model.addAttribute("times", timeList);
						} else {
							if (totalSlots.contains(df.format(cal.getTime()).toString())) {
								log.debug("cal.getTime()==" + cal.getTime());
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
					log.debug("=======" + sd);
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
				log.debug("this is a date:::::"+Date);
				model.addAttribute("selecteddate", Date);
		 return "servicesslots";
	 }
	 @GetMapping("/editofferservice/{offserviceid}") public String getById(Model model, @PathVariable("offserviceid") int id) {
	      ContractService  objservice = contractServicesService.getByConServiceId(id);
	      contractServicesService.UpdateConServices(objservice);
	      model.addAttribute("serviceObj", objservice);
	       return "offersserviceform";
	       }
	       @GetMapping("/deleteservice/{offserviceid}")
	       public String deleteService(Model model, @PathVariable("offserviceid") int id) {
	           ContractService objservice = contractServicesService.getByConServiceId(id);
	           objservice.setIsactive('N');
	           contractServicesService.addConServices(objservice);
	       return "redirect:/contractService/offerservicelist";
	       
	       }
}
