package com.maan.whatsapp.repository.whatsapp;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDetail;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDetailPK;

@Repository
@QuerydslPredicate
public interface WhatsappRequestDetailRepo extends JpaRepository<WhatsappRequestDetail, WhatsappRequestDetailPK>,
		QuerydslPredicateExecutor<WhatsappRequestDetail> {

	@Query(value ="select API_MESSAGE_TEXT from whatsapp_request_detail where WHATSAPPCODE||MOBILENO =?1 and "
			+ "CURRENT_STAGE=?2 and CURRENT_SUB_STAGE=?3 ",nativeQuery=true)
	String getMessageText(String mobileNo, String cuStage, String subStage);

	@Query(value="select * from WHATSAPP_REQRES_KEYS where DESCRIPTION ='EMOJI' AND STATUS='Y'",nativeQuery=true)
	List<Map<String,Object>> getEmojiDetails();


	@Query(value ="select ISTEMPLATE_MSG from whatsapp_request_detail where WHATSAPPCODE||MOBILENO =?1 and "
			+ "CURRENT_STAGE=?2 and CURRENT_SUB_STAGE=?3 and  remarks=?4 ",nativeQuery=true)
	String getTemplateStatus(String mobileNo, String cuStage, String subStage,String remarks);

}
