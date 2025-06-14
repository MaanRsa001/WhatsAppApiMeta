package com.maan.whatsapp.repository.whatsapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.whatsapp.WhatsappContactData;

@Repository
@QuerydslPredicate
public interface WhatsappContactDataRepo
		extends JpaRepository<WhatsappContactData, Long>, QuerydslPredicateExecutor<WhatsappContactData> {

	@Query(value = "SELECT WHATSAPPID FROM whatsapp_contact_data WHERE STATUS='Y' AND SYSTIMESTAMP BETWEEN SESSION_END_TIME -(NUMTODSINTERVAL (30, 'MINUTE')) AND SESSION_END_TIME", nativeQuery = true)
	List<Long> getWhatsAppIds();

	@Query(nativeQuery = true,value ="SELECT LANGUAGE FROM whatsapp_contact_data WHERE STATUS='Y' AND WHATSAPPID=?1")
	String getLanguage(String mobileNo);
	
	
}
