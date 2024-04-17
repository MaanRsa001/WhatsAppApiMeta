package com.maan.whatsapp.repository.master;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.WAChatRecipientMaster;
import com.maan.whatsapp.entity.master.WAChatRecipientMasterPK;

@Repository
@QuerydslPredicate
public interface WAChatRecipientMasterRepo extends JpaRepository<WAChatRecipientMaster, WAChatRecipientMasterPK>,
		QuerydslPredicateExecutor<WAChatRecipientMaster> {

	@Query(value = "SELECT * FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE PARENTMESSAGEID=?1 AND USEROPTTED_MESSAGEID=?2 AND STATUS = 'Y' AND TRUNC(EFFECTIVEDATE) <= TRUNC (SYSDATE)", nativeQuery = true)
	Map<String, Object> getNextMsgId(String parentMsgId, Long userReply);

	@Query(value = "SELECT * FROM WHATSAPP_CHATRECIPIENT_MASTER WHERE MESSAGEID=?1 AND STATUS = 'Y' AND TRUNC(EFFECTIVEDATE) <= TRUNC (SYSDATE) AND USEROPTTED_MESSAGEID NOT IN(0, 9, 99)", nativeQuery = true)
	Map<String, Object> getChatInputs(String msgId);
	
	@Query(value = "select PARENTMESSAGEID,USEROPTTED_MESSAGEID  from WHATSAPP_CHATRECIPIENT_MASTER where MESSAGEID=?1 and status ='Y' ", nativeQuery = true)
	Map<String, Object> getParentMessageId(String msgId);

}
