package com.vcare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcare.beans.ContractEmployee;

@Repository
public interface ContractEmployeeRepository extends JpaRepository<ContractEmployee,Integer> {
	
	@Query("select al from ContractEmployee al where al.email=?1 and al.password=?2")
	ContractEmployee findByEmailAndPassword(String email, String password);
	@Query("select al from ContractEmployee al WHERE lower(al.email)=lower(?1)")
    public ContractEmployee findByEmail(String email);
	@Query(value="select * from ContractEmployee where is_active ='y' or is_active ='Y'",nativeQuery=true)
	List<ContractEmployee> getAllActiveContractEmploye();

}
