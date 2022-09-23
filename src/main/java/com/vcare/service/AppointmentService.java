package com.vcare.service;
import java.util.List;
import com.vcare.beans.Appointment;
public interface AppointmentService {
	List<Appointment> getAllAppointment();
	Appointment getAppointmentById(long appointmentId);
	Appointment addAppointment(Appointment appointment);
	void updateAppointment(Appointment appointment);
	void deleteAppointmentById(long appointmentId);
	List<Appointment> getPatientAppointment(List<Appointment> appointment);
	List<Appointment> getAllActiveAppointments();
	List<Appointment> getAllActiveAppointmentsByDoctorId(int doctorId);
}
