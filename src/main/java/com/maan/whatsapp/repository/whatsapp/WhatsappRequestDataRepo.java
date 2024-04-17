package com.maan.whatsapp.repository.whatsapp;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maan.whatsapp.entity.whatsapp.WhatsappRequestData;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDataPK;

@Repository
@QuerydslPredicate
public interface WhatsappRequestDataRepo extends JpaRepository<WhatsappRequestData, WhatsappRequestDataPK>,
		QuerydslPredicateExecutor<WhatsappRequestData> {

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "UPDATE WHATSAPP_REQUEST_DATA SET ISSESSIONACTIVE=?1, REQUEST_TIME=?2, RESPONSE_TIME=?3, LASTUPDATED_TIME=?6, WA_RESPONSE=?7 WHERE WHATSAPPCODE=?4 AND MOBILENO=?5 AND STATUS='Y' AND (ISPROCESSCOMPLETED !='Y' OR ISPROCESSCOMPLETED IS NULL)", nativeQuery = true)
	int updateSessionStatus(String issessionactiv, Date reqtime, Date restime, Long code, Long mobileno,
			Date lastUpdatedTime, String response);

	@Modifying(clearAutomatically = true)
	@Transactional
	@Query(value = "UPDATE WHATSAPP_REQUEST_DATA SET ISSESSIONACTIVE=?1, REQUEST_TIME=?2, RESPONSE_TIME=?3, WA_RESPONSE=?6 WHERE WHATSAPPCODE=?4 AND MOBILENO=?5 AND STATUS='Y' AND (ISPROCESSCOMPLETED !='Y' OR ISPROCESSCOMPLETED IS NULL)", nativeQuery = true)
	int updateSessionStatus(String issessionactiv, Date reqtime, Date restime, Long code, Long mobileno,
			String response);

}
