package com.vcare.service;

import java.util.List;
import com.vcare.beans.ContractEmployee;

public interface ContractEmployeesService {
	
	List<ContractEmployee> getAllContactEmployee();
	ContractEmployee addContactEmployee(ContractEmployee contactEmployees);
	void updateContactEmployee(ContractEmployee contactEmployees);
	void deleteContactEmployeeId(int id);
	ContractEmployee getcontractEmployee(String email, String password);
	ContractEmployee getContactEmployeeById(int id);
	ContractEmployee findByMail(String email);
	List<ContractEmployee> getAllContractEmployee();

}
