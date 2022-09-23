package com.vcare.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.vcare.beans.ContractEmployee;
import com.vcare.repository.ContractEmployeeRepository;

@Service
public class ContractEmployeeServiceImpl implements ContractEmployeesService {
	
	@Autowired
	JavaMailSender javaMailSender;
	@Autowired
	ContractEmployeeRepository  contactEmployeeRepository;
	@Override
	public List<ContractEmployee> getAllContactEmployee() {
		return contactEmployeeRepository.findAll();
	}
	@Override
	public ContractEmployee addContactEmployee(ContractEmployee contactEmployees) {
		return contactEmployeeRepository.save(contactEmployees);
	}
	@Override
	public void updateContactEmployee(ContractEmployee contactEmployees) {
		contactEmployeeRepository.save(contactEmployees);
	}
	@Override
	public void deleteContactEmployeeId(int id) {
		contactEmployeeRepository.deleteById(id);
	}
	@Override
	public ContractEmployee getContactEmployeeById(int id) {
		return contactEmployeeRepository.getById(id);
	}
	@Override
	public ContractEmployee getcontractEmployee(String email, String password) {
		return contactEmployeeRepository.findByEmailAndPassword(email, password);
	}
	@Override
	public ContractEmployee findByMail(String email) {
		return contactEmployeeRepository.findByEmail(email);
	}
	@Override
	public List<ContractEmployee> getAllContractEmployee() {
		return contactEmployeeRepository.getAllActiveContractEmploye();
	}
}
