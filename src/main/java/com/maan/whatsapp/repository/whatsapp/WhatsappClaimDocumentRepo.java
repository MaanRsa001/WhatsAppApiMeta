package com.maan.whatsapp.repository.whatsapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.WhatsappClaimDocumentSetup;
import com.maan.whatsapp.entity.master.WhatsappClaimDocumentSetupPk;

@Repository
public interface WhatsappClaimDocumentRepo extends JpaRepository<WhatsappClaimDocumentSetup, WhatsappClaimDocumentSetupPk> {
	
	//List<WhatsappClaimDocumentSetup> findByCheckedAndClaimPkMobNoAndSkippedAndExStatus(String checked,String mobile,String skip,String exstatus); 
	
	//List<WhatsappClaimDocumentSetup> findByCheckedAndClaimPkMobNoAndExStatus(String checked,String mobile,String exStatus); 
	
	
	//WhatsappClaimDocumentSetup findByCheckedAndClaimPkMobNoAndDocIdAndExStatus(String checked,String mobile,String docId,String exstatus); 
	//WhatsappClaimDocumentSetup findByClaimPkMobNoAndUserReplayAndExStatusAndFileUpload(String mobileNo,String userReplay,String exStatus,String fileUploadStatus); 

	//List<WhatsappClaimDocumentSetup> findByClaimPkMobNoAndClaimPkPartyNoAndClaimPkClaimNoAndClaimPkLossId(String mobileNo,Long partyNo,String claimNo,Long lossId);
//
	@Query(value = "select CLAIMDOC_TRANID_SEQ.NEXTVAL  from  dual " ,nativeQuery = true)
	Long getTransactionNo();
		
	//List<WhatsappClaimDocumentSetup> findByClaimPkMobNoAndExStatus(String mobileNo,String exStatus);
	
	//List<WhatsappClaimDocumentSetup> findByClaimPkMobNoOrderByCreatedDateDesc(String mobileNo);
	
	WhatsappClaimDocumentSetup findByClaimPkMobNoAndSentYnAndFileYnAndProcessYn(Object mobileNo,Object isSent,Object fileYn,Object processYn);
	
	List<WhatsappClaimDocumentSetup> findByClaimPkMobNoAndFileYnAndProcessYnOrderByEntryDateDesc(Long mobileNo,String fileyn,String procYn);


}
