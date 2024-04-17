package com.maan.whatsapp.repository.whatsapptemplate;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maan.whatsapp.entity.whatsapptemplate.WhatsappMessageMaster;

@Repository
public interface WhatschattemplateRepository extends JpaRepository<WhatsappMessageMaster,String> {
	
	@Query(value="SELECT * FROM WHATSAPP_MESSAGE_MASTER WHERE ISCOMMONMSG = 'Y' ",nativeQuery=true)
	List<WhatsappMessageMaster> chattemplatecommonlist();
	
	@Query(value="SELECT * FROM WHATSAPP_MESSAGE_MASTER WHERE MESSAGEID = ?1 ",nativeQuery=true)
	List<WhatsappMessageMaster> chattemplateparentlist(String messageid);
	
	@Query(value="SELECT NVL(lpad(MAX(MAX_VAL),3,'0'),'001') MAX_VAL FROM (SELECT MAX(SUBSTR(MESSAGEID, -3)) MAX_VAL FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE MESSAGEID LIKE '%'||?1||'%' AND LENGTH(MESSAGEID) = ?2 ) TEMP WHERE regexp_like(MAX_VAL, '[[:digit:]]{3}$') ",nativeQuery=true)
	String  getmaxmsgid(String messageidtype,Long length);
	
	@Query(value="SELECT * FROM WHATSAPP_MESSAGE_MASTER WHERE MESSAGEID = ?1",nativeQuery=true)
	List<WhatsappMessageMaster> getparentdeletepreview(String id);
	
	@Modifying
	@Transactional
	@Query(value="DELETE FROM WHATSAPP_MESSAGE_MASTER WHERE MESSAGEID = ?1",nativeQuery=true)
	void getparentdeleterec(String id);
	
	@Query(value = "SELECT KEY_CODE CODE, KEY_NAME CODEDESC  FROM WHATSAPP_REQRES_KEYS WHERE STATUS = 'Y' AND PRODUCT_ID = '65'", nativeQuery = true)
	List<Map<String, Object>> getrequestkeylist();
	
	@Query(value = "SELECT PREFIX_CODE CODE, PREFIX_DESC_EN CODEDESC FROM WHATSAPP_PREFIX_LABEL WHERE STATUS = 'Y' AND PRODUCT_ID = '65'", nativeQuery = true)
	List<Map<String, Object>> getchatmsgidlist();
	
	@Query(value = "SELECT ITEM_CODE CODE, ITEM_DESC CODEDESC FROM LIST_ITEM_VALUE WHERE ITEM_TYPE = 'BROKER_RATING' AND STATUS = 'Y'", nativeQuery = true)
	List<Map<String, Object>> getuserlist();
	
	@Query(value = "SELECT PRODUCT_ID CODE, PRODUCT_NAME CODEDESC FROM PRODUCT_MASTER WHERE STATUS='Y'", nativeQuery = true)
	List<Map<String, Object>> getproductlist();

}
