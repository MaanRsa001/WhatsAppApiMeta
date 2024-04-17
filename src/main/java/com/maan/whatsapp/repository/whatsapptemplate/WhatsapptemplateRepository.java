package com.maan.whatsapp.repository.whatsapptemplate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.whatsapptemplate.Whatsapptemplate;
import com.maan.whatsapp.entity.whatsapptemplate.WhatsapptemplatePK;

@Repository
public interface WhatsapptemplateRepository extends JpaRepository<Whatsapptemplate, WhatsapptemplatePK> {
	@Query(value="SELECT * FROM WHATSAPP_TEMPLATE_MASTER WHERE AGENCY_CODE = ?1 AND PRODUCT_ID=?2 ORDER BY STAGE_CODE",nativeQuery=true)
	List<Whatsapptemplate> gettemplatelist(String agencycode, String productid);
	
	@Query(value="SELECT * FROM WHATSAPP_TEMPLATE_MASTER WHERE REMARKS = ?1 AND ISCHATYN ='Y' ORDER BY STAGE_ORDER ",nativeQuery=true)
	List<Whatsapptemplate> maintemplatechatlist(String remarksid);
	
	@Query(value="SELECT * FROM WHATSAPP_TEMPLATE_MASTER WHERE  PRODUCT_ID = ?1 AND AGENCY_CODE = ?2  AND  STAGE_CODE = ?3 AND STAGESUB_CODE = ?4",nativeQuery=true)
	List<Whatsapptemplate> getmaintemplateedit(String productid, String agencycode, String stagecode, String substagecode);
	
	@Query(value="SELECT NVL(MAX(STAGESUB_CODE)+1 , 1) FROM WHATSAPP_TEMPLATE_MASTER WHERE  PRODUCT_ID = ?1 AND AGENCY_CODE = ?2  AND  STAGE_CODE = ?3",nativeQuery=true)
	Long getmaxstagesubcode(Long product, String agencycode, Long stagecode);
	
	@Query(value="SELECT NVL(MAX(STAGE_CODE)+1 , 1) FROM WHATSAPP_TEMPLATE_MASTER WHERE  PRODUCT_ID = ?1 AND AGENCY_CODE = ?2",nativeQuery=true)
	Long getmaxstagecode(Long product, String agencycode);
	
	@Query(value="SELECT STAGE_CODE FROM WHATSAPP_TEMPLATE_MASTER WHERE PRODUCT_ID = ?1 AND REMARKS = ?2 AND ROWNUM = 1",nativeQuery=true)
	Long getcheckstagecode(Long product, String remarks);
}
