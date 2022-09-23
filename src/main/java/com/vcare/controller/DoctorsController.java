
package com.vcare.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vcare.beans.Appointment;
import com.vcare.beans.Doctor;
import com.vcare.beans.DoctorAvailability;
import com.vcare.beans.DoctorBranch;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.MyQr;
import com.vcare.beans.Patients;
import com.vcare.beans.Prescription;
import com.vcare.beans.Rating;
import com.vcare.beans.Services;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.DoctorAvailabilityRepository;
import com.vcare.repository.DoctorBranchRepository;
import com.vcare.repository.DoctorRatingRepository;
import com.vcare.repository.DoctorRepository;
import com.vcare.repository.HospitalBranchRepository;
import com.vcare.repository.ServiceRepository;
import com.vcare.service.AppointmentService;
import com.vcare.service.DoctorBranchService;
import com.vcare.service.DoctorImplementation;
import com.vcare.service.DoctorRatingService;
import com.vcare.service.DoctorService;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.PatientsService;
import com.vcare.service.PrescriptionService;
import com.vcare.service.ServiceService;
import com.vcare.utils.VcareUtilies;

@Controller
@RequestMapping("/doctor")
public class DoctorsController {
	@Autowired
	DoctorService doctorservice;
	@Autowired
	AppointmentService appointmentService;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	HospitalBranchRepository branchRepository;
	@Autowired
	ServiceService serServices;
	@Autowired
	DoctorBranchService doctorBranchService;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	DoctorBranchRepository doctorBranchRepository;
	@Autowired
	DoctorRatingRepository doctorRatingRepository;
	@Autowired
	DoctorRatingService doctorRatingService;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	DoctorImplementation doctorImplementation;
	@Autowired
	DoctorAvailabilityRepository doctorAvailabilityRepository;
	@Autowired
	AppointmentRepository appointmentRepository;
	@Autowired
	PatientsService patientsService;
	@Autowired
	PrescriptionService prescriptionService;
	static Logger log = Logger.getLogger(DoctorsController.class.getClass());
	@GetMapping("/Drlist")
	public String viewAllDoctors(Model model) {
		model.addAttribute("doctorlist", doctorservice.getAllDoctor());
		List<Doctor> doctorList = doctorservice.getAllDoctor();
		log.debug("doctor List = "+doctorList);
		model.addAttribute("doctorlist", doctorList);
		return "newdoctors";
	}
	@GetMapping("/deletedoctors/{doctorId}")
	public String deletedoctors(Model model, @PathVariable(value = "doctorId") int doctorId) {
		Doctor doctor = doctorservice.GetDocotorById(doctorId);
		log.debug("doctor deleted = "+doctor);
		doctor.setIsActive('N');
		doctorservice.updateDoctor(doctor);
		return "redirect:doctor/Drlist";
	}
	@RequestMapping("/validatEmail")
    public String validateEmail(Model model, Doctor doctorObj, final HttpServletRequest request,
            final HttpServletResponse response,HttpSession session ) throws MessagingException{
        if (!doctorObj.getCaptchaOne().equals(doctorObj.getUserCaptcha())) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
        model.addAttribute("Emailid",doctorObj.getDoctorMailId());
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(doctorObj.getDoctorMailId());
        mailMessage.setSubject("Doctor Consultation");
        doctorservice.sendSimpleEmail(doctorObj.getDoctorMailId(),"To consult the doctor, please click here in your slot time: "
                +session.getAttribute("url")+"saveDoc?doctorMailId="+doctorObj.getDoctorMailId()+"&password="+doctorObj.getPassword(),"Mailid Verification" );
        model.addAttribute("Emailid",doctorObj.getDoctorMailId());
        session.setAttribute("DocId", doctorObj.getDoctorId());
        return "successfulRegistration";
}
	@RequestMapping(value = "/saveDoc", method = RequestMethod.GET)
	public String addDoctor(Model model, @ModelAttribute(value = "objdoctor") Doctor doctorObj,
			@RequestParam(name = "serviceList", required = false) List<String> serviceList,HttpServletRequest request) {
		HttpSession session =request.getSession();
		log.debug("doctor Registered with ="+doctorObj.getDoctorMailId());
		String objectDoctor = doctorservice.validateduplicate(doctorObj.getDoctorMailId());
		if (objectDoctor == null) {
			model.addAttribute("objdoctor", doctorObj);
			log.debug("Doctro Id  :  " + doctorObj.getDoctorId());
			String strEncPassword = VcareUtilies.getEncryptSecurePassword(doctorObj.getPassword(), "vcare");
			doctorObj.setPassword(strEncPassword);
			List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
			model.addAttribute("serviceList", serviceNames);
			model.addAttribute("selectedService", serviceNames.size());
			doctorservice.addDoctor(doctorObj);
			session.setAttribute("DocId",doctorObj.getDoctorId());
			model.addAttribute("doctormsg","Successfully Registered, Please complete your profile");
			return "doctorprofile";
		} else {
			session.setAttribute("indexmsg","save");
			return "redirect:/";
		}
	}
    @RequestMapping("/selectBranch")
    public String selectBranch(Model model, Doctor objdoctor, final HttpServletRequest request,
            @RequestParam(name = "serviceList", required = false) List<String> serviceList, HttpSession session) {
        String getQry = request.getParameter("getQry");
        String serviceName = request.getParameter("serviceName");
        if (getQry != null && getQry.equals("selectBranch") && serviceName != null && serviceName != "") {
            List<HospitalBranch> hblist = hospitalBranchService.getHospitalBranchesByServiceName(serviceName);
            List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
            model.addAttribute("serviceList", serviceNames);
            model.addAttribute("selectedService", serviceNames.size());
            model.addAttribute("objdoctor", objdoctor);
            model.addAttribute("hblist", hblist);
            model.addAttribute("ServiceName", serviceName);
        }
        model.addAttribute("doctormsg","registered successfully, Please complete your profile");
        return "doctorprofile";
    }
	@GetMapping("/valid")
	public String Validation(Model model, @ModelAttribute(value = "objectdoctor") Doctor doctorObj,
			HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!doctorObj.getCaptchaOne().equals(doctorObj.getUserCaptcha())) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
		String str = VcareUtilies.getEncryptSecurePassword(doctorObj.getPassword(), "vcare");
		Doctor objDoc = doctorservice.getDoctors(doctorObj.getDoctorMailId(), str);
		log.debug("Doctor details : " + objDoc);
		model.addAttribute("objdoctor", objDoc);
		if (objDoc != null) {
			log.debug("===========login successfully======");
			session.setAttribute("drname", objDoc.getDoctorName());
			session.setAttribute("DocId", objDoc.getDoctorId());
			return "dashboard";
		} else {
			session.setAttribute("indexmsg","logininvalidpasswordorusername");
			return "redirect:/";
		}
	}
	@GetMapping("/uploadDoctorProfile/{did}")
	public String uploaddoctorProfile(Model model, @PathVariable("did") int did, Doctor doctorObj) {
		doctorservice.updateDoctor(doctorObj);
		log.debug("Doctor after login : " + doctorObj);
		model.addAttribute("objdoctor", doctorObj);
		model.addAttribute("objdoctor", doctorservice.GetDocotorById(did));
		model.addAttribute("did", doctorservice.GetDocotorById(did).getDoctorId());
		return "completeprofile";
	}
	   @PostMapping("/uploadDoctorProfile")
	    public String uploaddoctorProfiles(Model model, Doctor doctorObj, @RequestParam("file") MultipartFile file,
	            @RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
	            final HttpServletRequest request,@RequestParam(name = "serviceList", required = false) List<String> serviceList) throws IOException, Exception {
	        String data = doctorObj.toString();
	        String path = "C:\\Users\\Lenovo\\Documents\\workspace-spring-tool-suite-4-4.14.0.RELEASE\\Vcare\\src\\main\\resources\\static\\images\\qr"
	                + doctorObj.getDoctorId() + ".png";
	        String charset = "UTF-8";
	        Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
	        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	        byte[] d = MyQr.createQR(data, path, charset, hashMap, 200, 200);
	        doctorObj.setDocQrcode(Base64.getEncoder().encodeToString(d));
	        if(file.getOriginalFilename()=="") {
	        	doctorObj.setProfilepic(doctorObj.getProfilepic());
	        }else {
	        doctorObj.setProfilepic(Base64.getEncoder().encodeToString(file.getBytes()));
	        }
	        doctorObj.setImage(Base64.getEncoder().encodeToString(file1.getBytes()));
	        doctorObj.setVideo(Base64.getEncoder().encodeToString(file2.getBytes()));
	        doctorservice.updateDoctor(doctorObj);
	        if (doctorObj.getHospitalBranchId() == null) {
	            model.addAttribute("objdoctor", doctorObj);
	            List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
	            model.addAttribute("serviceList", serviceNames);
	            return "doctorprofile";
	        } else {
	            Doctor doctor = doctorRepository.findByFields(doctorObj.getDoctorMailId(), doctorObj.getDoctorMobileNo(),
	                    doctorObj.getMedicallicense());
	            doctorBranchRepository.doctorBranchDeleteById((long) doctor.getDoctorId());
	            String hbidlist = doctorObj.getHospitalBranchId();
	            String[] arrhblist = hbidlist.split(",");
	            List<HospitalBranch> hblist=new ArrayList<HospitalBranch>();
	            for (String blist : arrhblist) {
	                int hid = Integer.parseInt(blist);
	                DoctorBranch docBranch = new DoctorBranch();
	                docBranch.setHospitalId(hid);
	                docBranch.setDoctorId(doctor.getDoctorId());
	                doctorBranchService.addDoctorBranch(docBranch);
	                HospitalBranch hbObj=hospitalBranchService.getHospitalbranchId(hid);
	                hblist.add(hbObj);
	            }
	            model.addAttribute("hblist",hblist);
	        }
	        model.addAttribute("objdoctor", doctorObj);
	        log.debug("doctor : " + doctorObj);
	        List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
	        model.addAttribute("serviceList", serviceNames);
	        model.addAttribute("selectedService",serviceNames.size());
	        model.addAttribute("ServiceName",doctorObj.getServiceName());
	        model.addAttribute("doctormsg","Dr."+doctorObj.getDoctorName()+" your profile Successfully updated");
	        return "doctorprofile";
	    }
	@RequestMapping(value = "/forgot", method = RequestMethod.GET)
	public String UserforgotPasswordPage(Model model,Doctor doctorObj) {
		model.addAttribute("objdoctor", doctorObj);
		return "forgot";
	}
	@PostMapping(value = "/validate")
	public String checkMailId(Model model, Doctor doctorObj) {
		model.addAttribute("warninig", "invalid credentials..please try to login again...!");
		Doctor objdoc = doctorservice.findByMail(doctorObj.getDoctorMailId());
		if (objdoc != null) {
			model.addAttribute("message", "");
			model.addAttribute("objdoctor", doctorObj);
			return "reset";
		} else {
			model.addAttribute("message", "Email-Id Not Exists");
			model.addAttribute("objdoctor", new Doctor());
			return "forgot";
		}
	}
	@RequestMapping(value = "/updateClient", method = RequestMethod.POST)
	public String updateUserPassword(Model model, @ModelAttribute("objdoctor") Doctor doctorObj) {
		Doctor doctor = doctorservice.findByMail(doctorObj.getDoctorMailId());
		String strEncPassword = VcareUtilies.getEncryptSecurePassword(doctorObj.getPassword(), "vcare");
		doctorObj.setPassword(strEncPassword);
		doctor.setPassword(doctorObj.getPassword());
		doctorservice.updateDoctor(doctor);
		model.addAttribute("objdoctor", doctor);
		return "index";
	}
	@GetMapping("/updatedDoctorProfile/{doctorId}")
    public String viewUodateddoctorProfile(Model model, @PathVariable("doctorId") int doctorId,
            @RequestParam(name = "serviceList", required = false) List<String> serviceList) {
        Doctor doctorObj = doctorservice.GetDocotorById(doctorId);
        model.addAttribute("objdoctor", doctorObj);
        List<String> serviceNames = serviceRepository.findDistinctService(serviceList);
        String hbidlist = doctorObj.getHospitalBranchId();
        List<HospitalBranch> hblist = new ArrayList<HospitalBranch>();
        if (hbidlist == null) {
            model.addAttribute("hblist", branchRepository.hospitalBranches().get(0));

        } else {

            String[] arrhblist = hbidlist.split(",");
            if (arrhblist.length == 0) {
                HospitalBranch hd = hospitalBranchService.getHospitalbranchId(Integer.parseInt(hbidlist));
                hblist.add(hd);
            } else {
                for (String blist : arrhblist) {
                    int hid = Integer.parseInt(blist);
                    DoctorBranch docBranch = new DoctorBranch();
                    docBranch.setHospitalId(hid);
                    docBranch.setDoctorId(doctorObj.getDoctorId());
                    doctorBranchService.addDoctorBranch(docBranch);
                    HospitalBranch hbObj = hospitalBranchService.getHospitalbranchId(hid);
                    hblist.add(hbObj);
                }
                model.addAttribute("hblist",hblist);
            }
        }
        model.addAttribute("objdoctor",doctorObj);
        log.debug("doctorid after regestration  : "+doctorObj);
        model.addAttribute("ServiceName",doctorObj.getServiceName());
        model.addAttribute("serviceList",serviceNames);
        model.addAttribute("selectedService",serviceNames.size());
        model.addAttribute("name",doctorObj.getDoctorName());
        model.addAttribute("objMailId",doctorObj.getDoctorMailId());
        return"doctorprofile";
    }
	@GetMapping("/Viewdoctors/{patientId}/{doctorId}")
    public String viewdoctor(Model model, @PathVariable("patientId") int patientId, @PathVariable(value = "doctorId") int doctorId) throws Exception {
        Doctor doctorObj = doctorservice.GetDocotorById(doctorId);
        String[] s=doctorObj.getHospitalBranchId().split(",");
        int HospitalBranchId=0;
        if(s.length==0) {
            HospitalBranchId=Integer.parseInt(doctorObj.getHospitalBranchId());
        }
        else {
            for(String st:s) {
                HospitalBranchId=Integer.parseInt(st);
                break;
            }
        }
        return "redirect:doctor/Viewdoctors/"+HospitalBranchId+"/"+patientId+"/"+doctorId;
    }
	@SuppressWarnings("unlikely-arg-type")
	@GetMapping("/Viewdoctors/{hospitalBranchId}/{patientId}/{doctorId}")
	public String doctorScreenWithSlots(Model model, @PathVariable("patientId") int patientId, @PathVariable(value = "doctorId") int doctorId,
			@PathVariable(value = "hospitalBranchId") int hospitalBranchId, Rating doctorRating, Date timeStart, Date timeEnd,
			HttpServletRequest request) throws Exception {
		Patients patient = patientsService.getPatientById(patientId);
		model.addAttribute("pid", patient.getPatientId());
		Doctor doctorObj = doctorservice.GetDocotorById(doctorId);
		List<Rating> ratingList = doctorRatingRepository.findAllRatingsInDescOrder();
		model.addAttribute("patientname", patient.getFirstName());
		List<Rating> doctorRatingList =ratingList.stream().filter(ratingObj->ratingObj.getDoctor().getDoctorId()==doctorObj.getDoctorId()).collect(Collectors.toList());
		log.debug(" doctor List "+doctorRatingList.toString());
		model.addAttribute("pi", patient);
		model.addAttribute("pid", patientId);
		model.addAttribute("id", doctorId); 
		model.addAttribute("rating", doctorRatingList);
		model.addAttribute("drating", doctorRating);
		model.addAttribute("doc", doctorObj);
		model.addAttribute("hbid", hospitalBranchId);
		long ratingSum=doctorRatingList.stream().map(obj->obj.getRating()).reduce((long) 0,(a,b)->a+b);		
		double average=0.0;
		long ratingCount=0;
		if(ratingSum>0){
			ratingCount=doctorRatingList.size();
			average = (ratingSum / ratingCount);
			model.addAttribute("average", average);
		}
		model.addAttribute("average", average);
		model.addAttribute("totalreviews", ratingCount);
		Long fivecount=doctorRatingList.stream().filter(obj->obj.getRating()==5).count();
		model.addAttribute("fivecount", fivecount);
		Long fourcount = doctorRatingList.stream().filter(obj->obj.getRating()==4).count();
		model.addAttribute("fourcount", fourcount);
		Long threecount = doctorRatingList.stream().filter(obj->obj.getRating()==3).count();
		model.addAttribute("threecount", threecount);
		Long twocount = doctorRatingList.stream().filter(obj->obj.getRating()==2).count();
		model.addAttribute("twocount", twocount);
		Long onecount = doctorRatingList.stream().filter(obj->obj.getRating()==1).count();
		model.addAttribute("onecount", onecount);
		List<DoctorAvailability> docAvailable = doctorAvailabilityRepository.availableDoctor(doctorId);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		try {
		timeStart = (Date) formatter.parse(docAvailable.get(0).getStartTimings());
		timeEnd = (Date) formatter.parse(docAvailable.get(0).getEndTimings());
		LocalDate sd = LocalDate.of(timeStart.getYear() + 1900, timeStart.getMonth() + 1, timeStart.getDate());
		LocalDate ed = LocalDate.of(timeEnd.getYear() + 1900, timeEnd.getMonth() + 1, timeEnd.getDate());
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		List<String> datesRange = new ArrayList<>();
		List<Appointment> app = appointmentRepository.appointmentListByDoctorid(doctorId, request.getParameter("date"));
		ArrayList<String> totalSlots = new ArrayList<>();
		for (Appointment a : app) {
			totalSlots.add(a.getSlot());
		}
		while (sd.isBefore(ed) || sd.equals(ed)) {
			if (sd.isAfter(LocalDate.now()) || sd.isEqual(LocalDate.now())) {
				datesRange.add(sd.toString());
				sd = sd.plusDays(1);
				model.addAttribute("date", datesRange);
			} else {
				sd = sd.plusDays(1);
			}
		}
		model.addAttribute("monthdate", datesRange);
		log.debug("Date doctor available = "+datesRange);
		return "doctorsscreen";
		}
		catch(Exception e){
            model.addAttribute("appmsg","error occured");
            log.error("Error : "+e);
            return "error";
        }
	}
	@SuppressWarnings("unlikely-arg-type")
	@GetMapping("/Viewdoctors")
	public String viewdoctorSelectedDropdown(Model model, Date timeStart, Date timeEnd, Rating doctorRating,
			HttpServletRequest request) throws Exception {
		String doctorId = request.getParameter("doctorId");
		String patientId = request.getParameter("patientId");
		String hospitalBranchId = request.getParameter("hbid");
		model.addAttribute("hbid", Integer.parseInt(hospitalBranchId));
		Patients patient = patientsService.getPatientById(Integer.parseInt(patientId));
		Doctor doctorObj = doctorservice.GetDocotorById(Integer.parseInt(doctorId));
		List<Rating> ratingList = doctorRatingRepository.findAllRatingsInDescOrder();
		List<Rating> doctorRatingList =ratingList.stream().filter(ratingObj->ratingObj.getDoctor().getDoctorId()==doctorObj.getDoctorId()).collect(Collectors.toList());
		model.addAttribute("patientname", patient.getFirstName());
		model.addAttribute("pi", patient);
		model.addAttribute("pid", patientId);
		model.addAttribute("id", doctorId); 
		model.addAttribute("rating", doctorRatingList);
		model.addAttribute("drating", doctorRating);
		model.addAttribute("doc", doctorObj);
		model.addAttribute("hbid", hospitalBranchId);
		long ratingSum=doctorRatingList.stream().map(obj->obj.getRating()).reduce((long) 0,(a,b)->a+b);		
		double average=0.0;
		long ratingCount=0;
		if(ratingSum>0){
			ratingCount=doctorRatingList.size();
			average = (ratingSum / ratingCount);
			model.addAttribute("average", average);
		}
		model.addAttribute("average", average);
		model.addAttribute("totalreviews", ratingCount);
		Long fivecount=doctorRatingList.stream().filter(obj->obj.getRating()==5).count();
		model.addAttribute("fivecount", fivecount);
		Long fourcount = doctorRatingList.stream().filter(obj->obj.getRating()==4).count();
		model.addAttribute("fourcount", fourcount);
		Long threecount = doctorRatingList.stream().filter(obj->obj.getRating()==3).count();
		model.addAttribute("threecount", threecount);
		Long twocount = doctorRatingList.stream().filter(obj->obj.getRating()==2).count();
		model.addAttribute("twocount", twocount);
		Long onecount = doctorRatingList.stream().filter(obj->obj.getRating()==1).count();
		model.addAttribute("onecount", onecount);
		List<DoctorAvailability> docAvailable = doctorAvailabilityRepository
				.availableDoctor(Integer.parseInt(doctorId));
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		timeStart = (Date) formatter.parse(docAvailable.get(0).getStartTimings());
		timeEnd = (Date) formatter.parse(docAvailable.get(0).getEndTimings());
		// Separating Start and End Dates to year,month,date
		LocalDate sd = LocalDate.of(timeStart.getYear() + 1900, timeStart.getMonth() + 1, timeStart.getDate());
		LocalDate ed = LocalDate.of(timeEnd.getYear() + 1900, timeEnd.getMonth() + 1, timeEnd.getDate());
		Calendar calendar = Calendar.getInstance();// get present calendar
		calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		List<LocalDate> datesInRange = new ArrayList<>();
		List<String> slots = new ArrayList<>();
		List<String> monthdate = new ArrayList<>();
		int endHour = timeEnd.getHours();
		DateFormat df = new SimpleDateFormat("HH:mm");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, timeStart.getHours());
		cal.set(Calendar.MINUTE, timeStart.getMinutes());
		cal.set(Calendar.SECOND, 0);
		LocalDate selectedDate = LocalDate.parse(request.getParameter("date"));
		LocalDate today = LocalDate.parse(request.getParameter("date"));
		ArrayList<String> timeList = new ArrayList<>();
		if (selectedDate.equals(null)) {
			model.addAttribute("selectedDate", "");// Before selecting date
		} else {
			model.addAttribute("selectedDate", selectedDate);// After Selecting date
		}
		Calendar calObj = Calendar.getInstance();
		List<Appointment> app = appointmentRepository.appointmentListByDoctorid(Integer.parseInt(doctorId),
				request.getParameter("date"));
		ArrayList<String> totalSlots = new ArrayList<>();
		for (Appointment a : app) {
			totalSlots.add(a.getSlot());
		}
		int amount = 30;
		ArrayList<String> bookedSlots=new ArrayList<>();
		while (LocalDate.now().equals(selectedDate)) {
			if (cal.getTime().getHours() == endHour) {
				break;
			} else {
				if (app.size() == 0) {
					if (cal.getTime().getHours() == calObj.get(Calendar.HOUR_OF_DAY)) {
						if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE)) {
							timeList.add(df.format(cal.getTime()));
							model.addAttribute("times", timeList);
						}
					}
				}
				else {
					if (totalSlots.contains(df.format(cal.getTime()).toString())) {
						bookedSlots.add(df.format(cal.getTime()).toString());
						cal.add(Calendar.MINUTE, amount);
						continue;
					}
					else {
						if (cal.getTime().getHours() == calObj.get(Calendar.HOUR_OF_DAY)) {
							if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE)) {
								timeList.add(df.format(cal.getTime()));
								model.addAttribute("times", timeList);
							}
						}
					}
				}
				if (cal.getTime().getHours() > calObj.get(Calendar.HOUR_OF_DAY)) {
					if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE))
					timeList.add(df.format(cal.getTime()));
					model.addAttribute("times", timeList);
				}
				cal.add(Calendar.MINUTE, amount);
			}
		}
		while (!LocalDate.now().equals(selectedDate)) {
			if (cal.getTime().getHours() == endHour) {
				today = today.minusDays(1);
				break;
			} else {
				if (app.size() == 0) {
					timeList.add(df.format(cal.getTime()));
					cal.add(Calendar.MINUTE, amount);
					model.addAttribute("times", timeList);
				} else {
					if (totalSlots.contains(df.format(cal.getTime()).toString())) {
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
		while (sd.isBefore(ed) || sd.equals(ed)) {
			datesInRange.add(sd);
			if (sd.isAfter(LocalDate.now()) || sd.isEqual(LocalDate.now())) {
				monthdate.add(sd.toString());
				sd = sd.plusDays(1);
				model.addAttribute("date", monthdate);
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
		model.addAttribute("totalslots", totalslots);
		model.addAttribute("monthdate", monthdate);
		log.debug(selectedDate+" : "+totalslots);
		return "doctorsscreen";
	}
	@GetMapping("/docid/{doctorId}")
	public String doctorIdCard(Model model, @PathVariable("doctorId") int doctorId) {
		Doctor doctorObj = doctorservice.GetDocotorById(doctorId);
		model.addAttribute("objdoctor", doctorObj);
		log.debug("doctor id card "+doctorObj);
		return "docidcard";
	}
	@GetMapping("/checkallappointments/{doctorId}")
	public String allAppointments(Model model, @PathVariable("doctorId") int doctorId) {
		Doctor doctor=doctorservice.GetDocotorById(doctorId);
		model.addAttribute("objdoctor", doctor);
		log.debug("Appointmnts of doctor : "+doctor.getDoctorName()+" "+doctor.getDoctorMailId());
		return "patientAppointmentList";
	}
	@GetMapping("/getallappointments/{doctorId}")
	public String getAllAppointments(Model model, @PathVariable("doctorId") int doctorId, Appointment appointment,
			Doctor doctorObj, String from, String to, HttpSession session,HttpServletRequest request) {
		doctorObj = doctorservice.GetDocotorById(doctorId);
		model.addAttribute("objdoctor", doctorObj);
		to=request.getParameter("to");
		from = request.getParameter("from");
		List<Appointment> doctorAppointmentList = appointmentRepository.applointmentListBetweenDatesByDoctorId(doctorId, from, to);
		List<Prescription> pres = prescriptionService.getAllPrescription();
		if (doctorAppointmentList.size() == 0) {
			model.addAttribute("nodata", "No Appointments found!");
		}
		model.addAttribute("prescreptionlist", pres);
		model.addAttribute("from", from);
		model.addAttribute("to", to);
		model.addAttribute("prescreptionlist", pres);
		model.addAttribute("appList", doctorAppointmentList);
		model.addAttribute("doctorid", doctorId);
		session.setAttribute("drname", doctorObj.getDoctorName());
		model.addAttribute("meetingLink", appointment.getLink());
		log.debug(doctorObj);
		return "patientAppointmentList";
	}
	@GetMapping("/Doctorlist/{patientId}")
	public String doctrsForpatien(Model model, @PathVariable("patientId") int patientId) {
		Patients patient = patientsService.getPatientById(patientId);
		if (patient.getFirstName() == "" || patient.getPatientMailId() == "" || patient.getPatientAge() == 0
				|| patient.getPatientMobile() == 0) {
			model.addAttribute("pprofile", "First Complete your Profile");
			Patients patients = patientsService.getPatientById(patientId);
			model.addAttribute("patientObj", patients);
			model.addAttribute("patientId", patientId);
			model.addAttribute("patientmsg", "Please, complete your profile to Book an Appointment");
			return "patientProfile";
		}
		model.addAttribute("doctorlist", doctorservice.getAllDoctor());
		List<Doctor> doctorList = doctorservice.getAllDoctor();
		model.addAttribute("doctorlist", doctorList);
		model.addAttribute("pid", patientId);
		log.debug(doctorList);
		return "drlist";
	}
	@GetMapping("/BranchList/{doctorId}")
	public String BranchListByDoctor(Model model,@PathVariable("doctorId") String doctorId) {
		List<Doctor> doctor=doctorImplementation.doctorByBranch(doctorId);
		model.addAttribute("doctoId",doctor);
		log.debug(doctor);
		return "hospitalbranchlist";
	}
	@GetMapping("/viewAllDoctors")
    public String viewHomePages(Model model) {
		List<Doctor> doctor=doctorservice.getAllDoctor();
        model.addAttribute("doctorlist", doctor);
        return "indexdoctors";
    }
	@GetMapping("/payment/{doctorId}")
	public String payments(Model model,@PathVariable("doctorId")int doctorId,Appointment objAppointment) {
		List<Appointment> doctorAppointments =  appointmentRepository.appointmentListByDoctorid(doctorId);
		Doctor doctorObj = doctorservice.GetDocotorById(doctorId); 
		int total=	doctorAppointments.size() * doctorObj.getDoctorFee();
		model.addAttribute("objectapp", doctorAppointments);
		model.addAttribute("name",doctorObj.getDoctorName());
		model.addAttribute("amount", total);	
		log.debug(doctorAppointments);
		return "payment";
	}
	@GetMapping("/appointmentReschedule/{appointmentId}")
    public String appointmentReschedule(Model model,@PathVariable("appointmentId") int appointmentId,HttpSession session) {
        Appointment appointment=appointmentService.getAppointmentById(appointmentId);
        session.setAttribute("iddoc", appointment.getDoctor().getDoctorId());
        session.setAttribute("idpatient", appointment.getPatient().getPatientId());
        session.setAttribute("idhbranch", appointment.getHospitalBranchId());
        session.setAttribute("appid", appointment.getAppointmentId());
        session.setAttribute("reschedule","reschedule");
        return "redirect:doctor/Viewdoctors/"+session.getAttribute("idhbranch")+"/"+session.getAttribute("idpatient")+"/"+session.getAttribute("iddoc");
    }
}