package com.maan.whatsapp.claimintimation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InalipaIntimatedTableRepository extends JpaRepository<InalipaIntimatedTable, String>{

	@Query(value = "select * from inalipa_intimated_table where policy_no = ?1 and mobile_no = ?2 and ?3 between policy_start_date and policy_end_date",nativeQuery = true)
	List<InalipaIntimatedTable> getExistsClaimDetails(String policyNo, Long mobileNo, LocalDate accidentDate);

	@Query(value = "select * from inalipa_intimated_table where mobile_no = ?1 and accident_Date = ?2",nativeQuery = true)
	List<InalipaIntimatedTable> getClaimUploadDetails(String mobileNo,LocalDate accidentDate);

}
