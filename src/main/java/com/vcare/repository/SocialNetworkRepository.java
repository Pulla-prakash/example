package com.vcare.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.vcare.beans.SocialNetworks;

@Repository
public interface SocialNetworkRepository extends JpaRepository<SocialNetworks,Integer> {
	
	@Query(value="select * from SocialNetworks where is_Active ='y' or is_Active ='Y'",nativeQuery=true)
	List<SocialNetworks> getAllActiveNetworks();

}
