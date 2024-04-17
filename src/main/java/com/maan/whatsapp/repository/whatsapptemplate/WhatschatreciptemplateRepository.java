package com.maan.whatsapp.repository.whatsapptemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maan.whatsapp.entity.whatsapptemplate.WhatsappChatrecipiantMessageMaster;
import com.maan.whatsapp.entity.whatsapptemplate.WhatsappChatrecipiantMessageMasterpk;

@Repository
public interface WhatschatreciptemplateRepository extends JpaRepository<WhatsappChatrecipiantMessageMaster,WhatsappChatrecipiantMessageMasterpk> {

	@Query(value="SELECT * FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE PARENTMESSAGEID = ?1 ORDER BY USEROPTTED_MESSAGEID ",nativeQuery=true)
	List<WhatsappChatrecipiantMessageMaster> chattemplatechildlist(String parentmsgid);
	
	@Query(value="SELECT * FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE MESSAGEID = ?1 AND PARENTMESSAGEID = ?2 ",nativeQuery=true)
	List<WhatsappChatrecipiantMessageMaster> chattemplatechildlistedit(String messageid, String  parentmessageid);
	
	@Query(value="SELECT NVL(lpad(MAX(MAX_VAL)+1,3,'0'),'001') MAX_VAL FROM (SELECT MAX(SUBSTR(MESSAGEID, -3)) MAX_VAL FROM WHATSAPP_MESSAGE_MASTER WHERE MESSAGEID LIKE '%'||?1||'%' AND LENGTH(MESSAGEID) = ?2 UNION SELECT MAX(SUBSTR(MESSAGEID, -3)) MAX_VAL FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE MESSAGEID LIKE '%'||?1||'%' AND LENGTH(MESSAGEID) = ?2 ) TEMP WHERE regexp_like(MAX_VAL, '[[:digit:]]{3}$') ",nativeQuery=true)
	String  geansmaxmsgid(String messageid, Long  length);
	
	@Query(value="SELECT NVL(MAX(USEROPTTED_MESSAGEID)+1 , '0') FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE PARENTMESSAGEID = ?1 ",nativeQuery=true)
	Long  getmaxuseropt(String parentmsgid);
	
	@Query(value="SELECT count(*) FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE USEROPTTED_MESSAGEID = ?1 AND PARENTMESSAGEID = ?2 ",nativeQuery=true)
	Long  optcount(String optid, String pmsgid);
	
	@Query(value="SELECT * FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE PARENTMESSAGEID = ?1",nativeQuery=true)
	List<WhatsappChatrecipiantMessageMaster> getchilddeletepreview(String id);
	
	@Modifying
	@Transactional
	@Query(value="DELETE  FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE MESSAGEID = ?1",nativeQuery=true)
	void getchildmappedoptiondel(String id);
	
	@Modifying
	@Transactional
	@Query(value="DELETE  FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE PARENTMESSAGEID = ?1",nativeQuery=true)
	void getchilddeleterec(String id);
	
	@Query(value="SELECT COUNT(*) FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE PARENTMESSAGEID = ?1",nativeQuery=true)
	Long getcountparentidbased(String parentid);

}
