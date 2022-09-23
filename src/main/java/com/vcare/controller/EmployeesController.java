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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.vcare.beans.Appointment;
import com.vcare.beans.Department;
import com.vcare.beans.Doctor;
import com.vcare.beans.DoctorAvailability;
import com.vcare.beans.Employees;
import com.vcare.beans.HospitalBranch;
import com.vcare.beans.MyQr;
import com.vcare.beans.Patients;
import com.vcare.beans.Services;
import com.vcare.repository.AppointmentRepository;
import com.vcare.repository.DoctorAvailabilityRepository;
import com.vcare.repository.DoctorRepository;
import com.vcare.repository.ServiceRepository;
import com.vcare.service.DepartmentService;
import com.vcare.service.DepartmentServiceImpl;
import com.vcare.service.DoctorService;
import com.vcare.service.EmployeesService;
import com.vcare.service.EmployeesServiceImpl;
import com.vcare.service.HospitalBranchService;
import com.vcare.service.PatientsService;
import com.vcare.utils.MailUtil;
import com.vcare.utils.VcareUtilies;

@Controller
@RequestMapping("/Employee")
public class EmployeesController {

	@Autowired
	EmployeesService employeeService;
	@Autowired
	PatientsService patientService;
	@Autowired
	JavaMailSender mailSender;
	@Autowired
	DoctorService doctorService;
	@Autowired
	EmployeesServiceImpl employeeServiceimpl;
	@Autowired
	DepartmentServiceImpl departmentServiceImpl;
	@Autowired
	DepartmentService departmentService;
	@Autowired
	ServiceRepository serviceRepository;
	@Autowired
	HospitalBranchService hospitalBranchService;
	@Autowired
	DoctorRepository doctorRepository;
	@Autowired
	DoctorAvailabilityRepository availablilityRepository; 
	@Autowired
	AppointmentRepository appointmentRepository;
	

	static Logger log = Logger.getLogger(EmployeesController.class.getClass());

	@GetMapping("/employeelist/{departmentId}")
	public String getAllEmployees(Model model,@PathVariable("departmentId")int departmentId,HttpServletRequest request) {
		List<Employees> empList = employeeServiceimpl.getAllEmployee(departmentId);
		log.info("List:::::" + empList);
      Department objDepartment =departmentService.getDepartmentById(departmentId);
      model.addAttribute("departmentObj", objDepartment.getDepartmentId());
      model.addAttribute("hbId", objDepartment.getHospitalBranch().getHospitalBranchId());
		
		HttpSession session=request.getSession();
		if(session.getAttribute("empadd")=="empadd") {
			model.addAttribute("adminmsg", "\tEmployee\t Added");
			session.setAttribute("empadd", "");
		}
		if(session.getAttribute("empup")=="empup") {
			model.addAttribute("adminmsg", "\tEmployee \t Updated");
			session.setAttribute("empup","");
		}
		if(session.getAttribute("empdelete")=="empdelete") {
			model.addAttribute("adminmsg", "\tEmployee \t Successfully Deleted");
			session.setAttribute("empdelete", "");
		}
		model.addAttribute("employeesList", empList);
		return "employelist";
	}
	@RequestMapping(value = "/employeeform/{departmentId}", method = RequestMethod.GET)
	public String showEmployeeForm(Model model, @PathVariable("departmentId") int departmentId,
			@ModelAttribute(value = "employeeObj") Employees objEmployee) {
		Department objDepart = departmentService.getDepartmentById(departmentId);
		model.addAttribute("department", objDepart);
		log.info("name:" + objDepart.getDepartmentId());
		model.addAttribute("obj", objDepart.getDepartmentName());
		log.info("NAME:" + objDepart.getDepartmentName());
		Map<Integer, String> objDep = dropdownDepat();
		model.addAttribute("Object", objDep);
		model.addAttribute("Id", objDep.keySet());
		model.addAttribute("employeeObj", objEmployee);
		return "employeeform";
	}
	@RequestMapping(value = "/employeeform/{hospitalbranchId}/{departmentId}", method = RequestMethod.GET)
	public String showEmployeeFormById(Model model, @PathVariable("hospitalbranchId") int hospitalbranchId, @PathVariable("departmentId") int departmentId,
			@ModelAttribute(value = "employeeObj") Employees objEmployee) {
		HospitalBranch hbObj = hospitalBranchService.getHospitalbranchId(hospitalbranchId);
		model.addAttribute("hObj", hbObj);
		log.info("hospital branch"+hbObj.getHospitalBranchId());
		Department objDepart = departmentService.getDepartmentById(departmentId);
		model.addAttribute("department", objDepart);
		log.info("name:" + objDepart.getDepartmentId());
		model.addAttribute("obj", objDepart.getDepartmentName());
		log.info("NAME:" + objDepart.getDepartmentName());
		List<Department> objDep= departmentServiceImpl.departmentByBranch(hospitalbranchId);
		model.addAttribute("depObj", objDepart);
		model.addAttribute("employeeObj", objEmployee);
		 model.addAttribute("password",objEmployee.getPassword());
		return "employeeform";
	}
	@Value("${app.name}")
	String applicationName;
	@RequestMapping(value ="/saveEmployee", method = RequestMethod.POST)
	public String saveEmployee(Model model, @RequestParam("files") MultipartFile file,@ModelAttribute(value = "employeeObj") Employees objEmployee,
			@ModelAttribute(value = "hospitalBranch") HospitalBranch hospitalBranch ,HttpServletRequest request)
			throws IOException, WriterException {
		log.info("inside deleteEmployee id:::" + objEmployee.getEmployeeId());
		log.info("inside deleteEmployee id:::" + objEmployee.getEmployeeName());
		log.info("inside deleteEmployee id:::" + objEmployee.getEmployeePosition());
		model.addAttribute("employeeObj", objEmployee);
		if(objEmployee.getEmployeeId()==null) {
			MailUtil.sendSimpleEmail(mailSender, objEmployee.getEmail(),"Login creadentials here : "
	                +"Email&password"+" "+"UserMailId="+objEmployee.getEmail()+"&password="+objEmployee.getPassword(),"login credentials");
		log.info(objEmployee.getEmployeeId());
		String strEncPassword = VcareUtilies.getEncryptSecurePassword(objEmployee.getPassword(), applicationName);
		objEmployee.setPassword(strEncPassword);
		String data = objEmployee.toString();
		String path = "C:\\Users\\Abhi\\eclipse-workspace\\JavaTraining\\vcare-project\\src\\main\\resources\\static\\images\\qrcode_"
				+ objEmployee.getEmployeeId() + ".png";
		String charset = "UTF-8";
		Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
		hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		byte[] d = MyQr.createQR(data, path, charset, hashMap, 200, 200);
		objEmployee.setEmpQrCode((Base64.getEncoder().encodeToString(d)));
		HttpSession session=request.getSession();
		if(objEmployee.getEmployeeId()!=null) {
			session.setAttribute("empup", "empup");
		}else {
			session.setAttribute("empadd", "empadd");
		}
		model.addAttribute("employeeObj", objEmployee);
		if(file.getOriginalFilename()=="") {
			log.debug("employeename"+objEmployee.getEmployeeName());
			objEmployee.setProfile(objEmployee.getProfile());
		}else {
		
			log.debug("getEmployee Name"+objEmployee.getEmployeeName());
		objEmployee.setProfile(Base64.getEncoder().encodeToString(file.getBytes()));
		}
		employeeService.saveEmployees(objEmployee);
		}
		else {
		List<Employees> empList = employeeService.getAllEmployees();
		model.addAttribute("employeesList", empList);
			log.info(objEmployee.getEmployeeId());
			String strEncPassword = VcareUtilies.getEncryptSecurePassword(objEmployee.getPassword(), "vcare");
			objEmployee.setPassword(strEncPassword);
			String data = objEmployee.toString();
			String path = "C:\\Users\\Abhi\\eclipse-workspace\\JavaTraining\\vcare-project\\src\\main\\resources\\static\\images\\qrcode_"
					+ objEmployee.getEmployeeId() + ".png";
			String charset = "UTF-8";
			Map<EncodeHintType, ErrorCorrectionLevel> hashMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
			hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			byte[] d = MyQr.createQR(data, path, charset, hashMap, 200, 200);
			objEmployee.setEmpQrCode((Base64.getEncoder().encodeToString(d)));
			HttpSession session=request.getSession();
			session.setAttribute("empadd", "empadd");
			model.addAttribute("employeeObj", objEmployee);
			if(file.getOriginalFilename()=="") {
				log.debug(";;;;;"+objEmployee.getEmployeeName());
				objEmployee.setProfile(objEmployee.getProfile());
			}else {
			
				log.debug("get Employee Name"+objEmployee.getEmployeeName());
			objEmployee.setProfile(Base64.getEncoder().encodeToString(file.getBytes()));
			}
			employeeService.saveEmployees(objEmployee);
		}
		if(objEmployee.getEmployeePosition().equals("Receptionist")) {
            int objectDep=objEmployee.getDepartmentId();
            Department dep=departmentService.getDepartmentById(objectDep);
            Employees objSecEmployee = employeeService.getById(objEmployee.getEmployeeId());
            HospitalBranch hObj=hospitalBranchService.getHospitalbranchId(objSecEmployee.getHospitalBranch().getHospitalBranchId());
            model.addAttribute("depObj", dep);
            model.addAttribute("hObj", hObj);
            model.addAttribute("password","passwordpresent");
            model.addAttribute("employeeObj", objEmployee);
            model.addAttribute("empmsg", "Profile updated Successfully!!");
            return "employeeform";
		}
		return "redirect:/Employee/employeelist/"+objEmployee.getDepartmentId();
	}
	@GetMapping("/editEmployee/{employeeId}")
	public String editEmployeeById(Model model, @PathVariable("employeeId") int employeeId) {
	Employees objSecEmployee = employeeService.getById(employeeId);
	HospitalBranch hObj=hospitalBranchService.getHospitalbranchId(objSecEmployee.getHospitalBranch().getHospitalBranchId());
	List<Department> depList=departmentServiceImpl.departmentByBranch(hObj.getHospitalBranchId());
	Map<Integer, String> objDep = dropdownDepat();
    model.addAttribute("Object", objDep);
	model.addAttribute("department", depList);
	int objectDep=objSecEmployee.getDepartmentId();
	Department dep=departmentService.getDepartmentById(objectDep);
	model.addAttribute("depObj", dep);
	log.info("inside getHospitalbranchId id is:::" + employeeService.getById(employeeId));
	model.addAttribute("employeeObj", objSecEmployee);
	model.addAttribute("hObj", hObj);
	model.addAttribute("hosp", hObj.getHospitalBranchId());
	model.addAttribute("password","passwordpresent");
    return "employeeform";
    }
	@GetMapping("/deleteEmployee/{id}")
	public String deleteService(Model model, @PathVariable int id,HttpServletRequest request) {
		log.info("inside deleteHospitalBranch id:::" + id);
		Employees inactive = employeeService.getById(id);
		inactive.setIsactive('N');
		employeeService.UpdateEmployees(inactive);
		HttpSession session =request.getSession();
		session.setAttribute("empdelete", "empdelete");
		return "redirect:/Employee/employeelist/"+inactive.getDepartmentId();

	}
	public Map<Integer, String> dropdownDepat() {
		Map<Integer, String> objDep = new HashMap<Integer, String>();
		List<Department> deptList = departmentService.getAllDepartments();
		for (Department depObj : deptList) {
			objDep.put(depObj.getDepartmentId(), depObj.getDepartmentName());
		}
		return objDep;
	}
	@SuppressWarnings("unlikely-arg-type")
	@PostMapping("/selectDoc")
	public String serviceDoctor(Model model,
			@RequestParam(name = "serviceList", required = false) List<String> serviceList, Employees objEmployee,
			Appointment appointmentObj, HospitalBranch hospitalBranch, Services service,
			@RequestParam(name = "serviceName", required = false) String servName,
			@RequestParam(name = "docName", required = false) String docName, DoctorsController docController,
			final HttpServletRequest request, Date timeStart, Date timeEnd) throws Exception {
		log.debug("hello eddi" + objEmployee.getHospitalBranch().getHospitalBranchId());
		int employeeid =  objEmployee.getEmployeeId();
        Employees employee =employeeService.getById(employeeid);
        model.addAttribute("hosp", employee.getHospitalBranch().getHospitalBranchId());
		model.addAttribute("hosp", objEmployee.getHospitalBranch().getHospitalBranchId());
		List<String> serviceNames = serviceRepository.BranchServices(objEmployee.getHospitalBranch().getHospitalBranchId());
		model.addAttribute("serviceList", serviceNames);
		String getQry = request.getParameter("getQry");
		String serviceName = request.getParameter("serviceName");
		if (getQry != null && getQry.equals("selectService") && serviceName != null && serviceName != "") 
		{
			List<Doctor> doctorList = doctorRepository
					.serviceDoctors(objEmployee.getHospitalBranch().getHospitalBranchId(), serviceName);
			model.addAttribute("doctorList", doctorList);// method in repository
			log.debug(getQry + "and" + serviceName);
			log.debug(doctorList + "and" + doctorList);
			model.addAttribute("servName", serviceName);
			model.addAttribute("doctorName", docName);
			model.addAttribute("doctorId","");
			log.debug("get doctor name"+docName);
			model.addAttribute("appointmentObj", appointmentObj);
			model.addAttribute("employee", employee);
			return "receptionbookingscreen";
		}
		List<Doctor> doctorList = doctorRepository.serviceDoctors(hospitalBranch.getHospitalBranchId(),
				service.getServiceName());
        Employees employees =employeeService.getById(employeeid);
        model.addAttribute("hosp", employee.getHospitalBranch().getHospitalBranchId());
        model.addAttribute("employee", employees);
		model.addAttribute("doctorList", doctorList);
		String doctorId = request.getParameter("doctorId");
		List<LocalDate> datesInRange = new ArrayList<>();
		List<String> slots = new ArrayList<>();
		List<String> days = new ArrayList<>();
		List<String> monthdate = new ArrayList<>();
		ArrayList<String> timeList = new ArrayList<>();
		String getQryDoc = request.getParameter("getQryDoc");
		if (getQryDoc != null && getQryDoc.equals("doctorAvailable") && doctorId != null && doctorId != "") {
			List<DoctorAvailability> docAvailable = availablilityRepository.availableDoctor(Integer.valueOf(doctorId));
			model.addAttribute("docid", docAvailable.get(0).getDoctor().getDoctorId());
			model.addAttribute("hbid", docAvailable.get(0).getDoctor().getHospitalBranchId());
			log.debug("StartTimings==========" + docAvailable.get(0).getStartTimings());
			log.debug("EndTimings==========" + docAvailable.get(0).getEndTimings());//
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
			DateFormat formatterDay = new SimpleDateFormat("EEE");
			timeStart = (Date) formatter.parse(docAvailable.get(0).getStartTimings());
			timeEnd = (Date) formatter.parse(docAvailable.get(0).getEndTimings());
			log.debug("end" + timeEnd.getHours());
			log.debug("start" + timeStart.getHours());
			LocalDate sd = LocalDate.of(timeStart.getYear() + 1900, timeStart.getMonth() + 1, timeStart.getDate()); // LocalDate
			LocalDate ed = LocalDate.of(timeEnd.getYear() + 1900, timeEnd.getMonth() + 1, timeEnd.getDate()); // LocalDate
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
			model.addAttribute("times", timeList);
			model.addAttribute("doctorId",docAvailable.get(0).getDoctor().getDoctorId());
			model.addAttribute("days", days);
			model.addAttribute("servName", serviceName);
			model.addAttribute("doctorName",doctorService.GetDocotorById(Integer.parseInt(docName)).getDoctorName());
			log.debug("hello:::::::"+docName);
			return "receptionbookingscreen";
		}
		String getQryDate = request.getParameter("getQryDate");
		LocalDate selectedDate = LocalDate.parse(request.getParameter("Date"));
		LocalDate today = LocalDate.parse(request.getParameter("Date"));
		// Getting DoctorAvailability records by doctorID
		List<DoctorAvailability> docAvailable = availablilityRepository.availableDoctor(Integer.parseInt(doctorId));
		log.debug("StartTimings==========" + docAvailable.get(0).getStartTimings());
		// Formatting the date from html format to java
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm");
		DateFormat formatterDay = new SimpleDateFormat("EEE");// gets week days as MON,TUE....
		// getting start and end timings from DoctorAvailability and setting to Date
		timeStart = (Date) formatter.parse(docAvailable.get(0).getStartTimings());
		timeEnd = (Date) formatter.parse(docAvailable.get(0).getEndTimings());
		log.debug("timeEndtimeEnd" + timeEnd);
		// stores only hour of endtime
		int endHour = timeEnd.getHours();
		// Setting the time to Hours:Minutes
		DateFormat df = new SimpleDateFormat("HH:mm");
		// Separating Start and End Dates to year,month,date
		LocalDate sd = LocalDate.of(timeStart.getYear() + 1900, timeStart.getMonth() + 1, timeStart.getDate());
		LocalDate ed = LocalDate.of(timeEnd.getYear() + 1900, timeEnd.getMonth() + 1, timeEnd.getDate());
		log.debug("sdf" + formatterDay.format(timeStart));
		List<String> times = new ArrayList<>();// To store slots
		Calendar calendar = Calendar.getInstance();// get present calendar
		calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
		int minutes = calendar.get(Calendar.MINUTE);
		String hospitalBranchId=request.getParameter("hospitalBranchId");
		if (getQryDate != null && getQryDate.equals("dateAvailable") && selectedDate != null) {
				model.addAttribute("selectedDate", selectedDate);// After Selecting date
			model.addAttribute("hbid", Integer.parseInt(hospitalBranchId));
			model.addAttribute("docid", docAvailable.get(0).getDoctor().getDoctorId());
			model.addAttribute("doctorId", docAvailable.get(0).getDoctor().getDoctorId());
			List<Doctor> docList = doctorRepository.serviceDoctors(objEmployee.getHospitalBranch().getHospitalBranchId(), serviceName);
			model.addAttribute("doctorList", docList);// method in repository
			log.debug(getQry + "and" + serviceName);
			log.debug(docList + "and" + docList);
			log.debug("in if timeEnd" + timeEnd);
			model.addAttribute("servName", serviceName);
			model.addAttribute("appointmentObj", appointmentObj);
			// sets start-time hour and minutes and seconds
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, timeStart.getHours());
			cal.set(Calendar.MINUTE, timeStart.getMinutes());
			cal.set(Calendar.SECOND, 0);
			// Present calender for getting todays data
			Calendar calObj = Calendar.getInstance();
			// Gets listOfAppointmnetByDoctorId&Date
			List<Appointment> app = appointmentRepository.appointmentListByDoctorid(Integer.parseInt(doctorId),
					request.getParameter("Date"));
			// For storing the booked slots as string
			ArrayList<String> totalSlots = new ArrayList<>();
			for (Appointment a : app) {
				totalSlots.add(a.getSlot());
			}
			// For Present Day Slots
			log.debug(selectedDate+"and"+LocalDate.now());
			while (LocalDate.now().equals(selectedDate)) {
				log.debug("=======and=========="+df.format(cal.getTime()));
				if (cal.getTime().getHours() == endHour) {
					log.debug("endhour=="+cal.getTime().getHours());
					break;
				} else {
					// if no Appointments are booked for present day
					if (app.size() == 0) {
						// Displaying slots for only current hour
						log.debug("No appointments are available");
						if (cal.getTime().getHours() == calObj.get(Calendar.HOUR_OF_DAY)) {
							log.debug("Displaying slots for only current hour="+cal.getTime().getHours());
							if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE)) {
								timeList.add(df.format(cal.getTime()));
								model.addAttribute("times", timeList);
							}
						}
					}
					// if Appointments are booked for present day
					else {
						// For Not Displaying the slots booked
						if (totalSlots.contains(df.format(cal.getTime()).toString())) {
							log.debug("app.contains(df.format(cal.getTime()).toString()=="
									+ totalSlots.contains(df.format(cal.getTime()).toString()));
							log.debug(
									"df.format(cal.getTime()).toString()==" + df.format(cal.getTime()).toString());
							cal.add(Calendar.MINUTE, 10);
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
					log.debug("cal.add(Calendar.MINUTE, 10)==" + df.format(cal.getTime()));
					// Displaying slots for after current hour
					if (cal.getTime().getHours() > calObj.get(Calendar.HOUR_OF_DAY)) {
						if (cal.getTime().getMinutes() >= calObj.get(Calendar.MINUTE)) {
							log.debug("cal.getTime()==" + cal.getTime());
						timeList.add(df.format(cal.getTime()));
						model.addAttribute("times", timeList);
						}
					}
					cal.add(Calendar.MINUTE, 10);
				}
			} // For Slots from next-day
			while (!LocalDate.now().equals(selectedDate)) {
				if (cal.getTime().getHours() == endHour) {
					today = today.minusDays(1);
					log.debug("Nottoday" + today);
					break;
				} else {
					if (app.size() == 0) {
						timeList.add(df.format(cal.getTime()));
						cal.add(Calendar.MINUTE, 10);
						model.addAttribute("times", timeList);
					} else {
						if (totalSlots.contains(df.format(cal.getTime()).toString())) {
							cal.add(Calendar.MINUTE, 10);
							continue;
						} else {
							log.debug("cal.getTime()==" + cal.getTime());
							timeList.add(df.format(cal.getTime()));
							cal.add(Calendar.MINUTE, 10);
							model.addAttribute("times", timeList);
						}
					}
				}
			}
			// Dates drop-down according to DoctorAvailability
			while (sd.isBefore(ed) || sd.equals(ed)) {
				datesInRange.add(sd);
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
					log.debug(sd);
					model.addAttribute("date", monthdate);
				} else {
					sd = sd.plusDays(1);
				}
			}
			model.addAttribute("times", timeList);
			model.addAttribute("monthdate", monthdate);
			model.addAttribute("days", days);
			model.addAttribute("doctorName",doctorService.GetDocotorById(Integer.parseInt(docName)).getDoctorName());
			return "receptionbookingscreen";
		}
		model.addAttribute("appointmentObj", appointmentObj);
		return "receptionscreen";
	}
	@GetMapping("/employeelogin")
	public String Validation(Model model, @ModelAttribute(value = "employeeObj") Employees objEmployee,
			Appointment appointmentObj, @RequestParam(name = "serviceList", required = false) List<String> serviceList,
			HospitalBranch hospitalBranch, HttpServletRequest request) {
		HttpSession session = request.getSession();
		if (!objEmployee.getCaptchaDepart().equals(objEmployee.getUserCaptcha())) {
			session.setAttribute("indexmsg","logininvalidcaptcha");
			return "redirect:/";
		}
		model.addAttribute("warninig", "invalid credentials..please try to login again...!");
		log.info("Get doctor name : " + objEmployee.getEmployeeName());
		log.info("get doctor password  : " + objEmployee.getEmployeePosition());
		String str = VcareUtilies.getEncryptSecurePassword(objEmployee.getPassword(), "vcare");
		Employees signinObj = employeeService.getEmployee(objEmployee.getEmail(), str);
		log.info("validation details : " + signinObj);
		model.addAttribute("employeeObj", signinObj);
		if (signinObj != null) {
			log.info("===========login successfully======");
			session.setAttribute("name", signinObj.getEmployeeName());
			log.debug("hey hey" + signinObj.getHospitalBranch().getHospitalBranchId());
			model.addAttribute("hosp", signinObj.getHospitalBranch().getHospitalBranchId());
			List<String> serviceNames = serviceRepository
					.BranchServices(signinObj.getHospitalBranch().getHospitalBranchId());
			model.addAttribute("serviceList", serviceNames);
			model.addAttribute("appointmentObj", appointmentObj);
			model.addAttribute("position",signinObj.getEmployeePosition());
			model.addAttribute("employee",signinObj );
			return "employeescreen";
		} else {
			session.setAttribute("indexmsg","logininvalidpasswordorusername");
			return "redirect:/";
		}
	}
	@GetMapping("/empid/{id}")
	public String showId(Model model, @PathVariable("id") int employeeId) {
		Employees objEmployee = employeeService.getById(employeeId);
		model.addAttribute("objEmployee", objEmployee);
		return "empidcard";
	}
	@RequestMapping("/offlineappform/{hbid}/{did}")
	public String offlineAppointments(Model model,@RequestParam("slot") String slot,@RequestParam("date") String date, @PathVariable("hbid") int branchId,
	@PathVariable("did") int did,Patients patientObj, @ModelAttribute(value = "employeeObj") Employees objEmployee,
	Appointment appointment) {
	HospitalBranch branch= hospitalBranchService.getHospitalbranchId(branchId);
	patientObj.setCreated(LocalDate.now());
	Doctor doc=doctorService.GetDocotorById(did);
	log.debug("branchId"+ branch.getHospitalBranchId());
	model.addAttribute("branchId", branch.getHospitalBranchId());
	model.addAttribute("patientObj",patientObj);
	model.addAttribute("doctorId",did);
	model.addAttribute("doctor",doc);
	model.addAttribute("slot",slot);
	model.addAttribute("date",date);
	model.addAttribute("employee",objEmployee);
	return "offlinepatientform";
	}
	@GetMapping("/updateEmployee/{id}")
	public String updateEmployee(Model model,@PathVariable("id") int id,Employees employee) {
		Employees objEmployee=employeeService.getById(id);
		model.addAttribute("hObj", objEmployee);
		model.addAttribute("employeeObj", objEmployee);
		model.addAttribute("employeeId", id);
		Map<Integer, String> objDep = dropdownDepat();
		model.addAttribute("Object", objDep);
		model.addAttribute("Id", objDep.keySet());
		model.addAttribute("employeeObj", objEmployee);
		return"employeeform";
	}
	@RequestMapping("/receptionform/{employeeId}")
	public String receptionForm(Model model,@PathVariable("employeeId") int employeeId) {
	Employees employee=employeeService.getById(employeeId);
	List<String> serviceNames = serviceRepository.BranchServices(employee.getHospitalBranch().getHospitalBranchId());
	model.addAttribute("hosp", employee.getHospitalBranch().getHospitalBranchId());
	model.addAttribute("empid",employeeId);
	model.addAttribute("serviceList", serviceNames);
	model.addAttribute("employee",employee);
	return "receptionbookingscreen";
			}
	 @RequestMapping(value = "/forgotpassword", method = RequestMethod.GET)
	    public ModelAndView employeeForgotPasswordPage(Employees empObj) {
	        log.debug("entered into user/controller::::forgot paswword method");
	        ModelAndView mav = new ModelAndView("employeeupdatepassword");
	        mav.addObject("empObj", empObj);
	        return mav;
	    }
	    @PostMapping(value = "/validateemployee")
	    public String checkMailIdForEmployee(Model model, Employees empObj) {
	        model.addAttribute("warninig", "invalid credentials..please try to login again...!");
	        log.debug("entered into user/controller::::check EmailId existing or not");
	        log.debug("UI given mail Id:" + empObj.getEmail());
	        Employees objEmp = employeeService.findByMail(empObj.getEmail());
	        if (objEmp != null) {
	            String s1 = "";
	            model.addAttribute("message", s1);
	            log.debug("UI given mail Id:" + objEmp.getEmail());
	            model.addAttribute("empObj", empObj);
	            return "employeeresetpassword";
	        } else {
	            log.debug("Invalid Mail");
	            String s1 = "Email-Id Not Exists";
	            model.addAttribute("message", s1);
	            model.addAttribute("empObj", new Employees());
	            return "employeeresetpassword";
	        }
	    }
	    @RequestMapping(value = "/updateemployee", method = RequestMethod.POST)
	    public String updateEmployeePassword(Model model, @ModelAttribute("empObj") Employees empObj,HttpSession session) {
	        Employees pUser = employeeService.findByMail(empObj.getEmail());
	        log.debug("inside updateClientPassword after update id is:::" + empObj.getEmail());
	        String strEncPassword = VcareUtilies.getEncryptSecurePassword(empObj.getPassword(), "vcare");
	        empObj.setPassword(strEncPassword);
	        pUser.setPassword(empObj.getPassword());
	        log.debug(empObj.getPassword());
	        log.debug(" in update method Client created date::" + pUser.getCreated());
	        log.debug("in update method pUser:: name " + pUser.getEmployeeName());
	        employeeService.UpdateEmployees(pUser);
	        log.debug("password is updated sucessfully");
	        model.addAttribute("warning", "Password is updated Successfully. Please Login!!");
	        log.debug("login page is displayed");
	        model.addAttribute("empObj", pUser);
	        session.setAttribute("indexmsg","updatepassword");
	        return "redirect:/";
	    }
}