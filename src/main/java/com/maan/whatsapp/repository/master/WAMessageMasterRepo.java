package com.maan.whatsapp.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.WAMessageMaster;

@Repository
@QuerydslPredicate
public interface WAMessageMasterRepo
		extends JpaRepository<WAMessageMaster, String>, QuerydslPredicateExecutor<WAMessageMaster> {

	@Query(value = "SELECT * FROM WHATSAPP_MESSAGE_MASTER WHERE MESSAGEID=?1 AND STATUS='Y' AND TRUNC(EFFECTIVEDATE) <= TRUNC(SYSDATE)", nativeQuery = true)
	WAMessageMaster getMsgCont(String msgid);
	
	WAMessageMaster findByMessageidAndStatus(String messageId,String status);

}
