package com.maan.whatsapp.claimintimation;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimIntimationRepository extends JpaRepository<ClaimIntimationEntity, ClaimIntimationEntityId> {
	
	
	@Query(value ="select serial_no.nextval from dual",nativeQuery=true)
	public Long getSerialNo();
	
	@Query(value ="select * from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where serial_no="
			+ "(select max(serial_no) from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where MOBILE_NO=?1 and upper(API_TYPE)=upper('Policy')) and rownum=1",nativeQuery=true)
	public Map<String,Object> getProductCode(String mobileNo);

	@Query(value="select * from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where serial_no=(select max(serial_no) from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where MOBILE_NO=?1 and upper(API_TYPE)=?2)",nativeQuery=true)
	public List<Map<String, Object>> getDeatilsByMobileNo(String mobileNo, String string);
	
	@Query(value ="select * from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where serial_no=(select max(serial_no) from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where MOBILE_NO=?1 and upper(API_TYPE)=?2) and BOT_OPTION_NO=?3",nativeQuery=true)
	public Map<String,Object> getClaimDeatils(String mobileNo,String type,String optionNo);
	
	@Query(value = "select CODE from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where "
			+ "    serial_no=(select max(serial_no) from WAHTSAPP_CLAIM_INTIMATION_SAVE_RES where MOBILE_NO=?1 and upper(API_TYPE)=?2)"
			+ "    AND BOT_OPTION_NO=?3",nativeQuery = true)
	public String getType(String mobileNo,String bodyType,String bodyId);

	@Query(value = "SELECT CONCAT(RPAD('LM-',7,0),IL_CLAIM_SEQUENCE.NEXTVAL) FROM DUAL",nativeQuery = true)
	public String getInalipaClamRefMax();

	
	@Query(value ="SELECT DISTINCT body_id FROM EWAY_MOTOR_BODYTYPE_MASTER  WHERE company_id=?1 AND UPPER(TRIM(regulatory_code))=UPPER(TRIM(?2))"
			+ " AND STATUS ='Y'",nativeQuery=true)
	public String getBodyId(String companyId,String bodyName);
	
	@Query(value ="SELECT mm.* FROM motor_VEHICLEUSAGE_MASTER mm WHERE mm.company_id=?1 AND UPPER(TRIM(mm.regulatory_code))=UPPER(TRIM(?2))"
			+ " AND mm.STATUS ='Y' AND mm.amend_id=(SELECT * FROM(SELECT MAX(amend_id) FROM motor_VEHICLEUSAGE_MASTER rr WHERE mm.company_id=rr.company_id AND"
			+ " UPPER(TRIM(mm.regulatory_code))=UPPER(TRIM(rr.regulatory_code)) AND rr.status='Y')X)",nativeQuery=true)
	public List<Map<String,Object>> getVehicleUsage(String companyId,String vehUsageName);
	
	@Query(nativeQuery=true,value ="SELECT distinct SECTION_ID FROM PRODUCT_SECTION_MASTER HPM WHERE COMPANY_ID=?1 AND PRODUCT_ID=?2 AND STATUS='Y' AND TRIM(UPPER(REPLACE(SECTION_NAME,'\\n','')))=TRIM(UPPER(REPLACE(?3,'\\n',''))) AND AMEND_ID=(SELECT MAX(AMEND_ID) FROM PRODUCT_SECTION_MASTER WHERE COMPANY_ID=?1 AND PRODUCT_ID=?2 AND STATUS='Y' AND TRIM(UPPER(REPLACE(SECTION_NAME,'\\n','')))=TRIM(UPPER(REPLACE(?3,'\\n','')))  AND HPM.SECTION_ID=SECTION_ID)")
	String getPolicyTypeId(String companyId,String productId,String desc);
	
	@Query(nativeQuery=true,value ="SELECT distinct VEHICLE_USAGE_ID FROM MOTOR_VEHICLEUSAGE_MASTER HPM WHERE COMPANY_ID=?1 AND STATUS='Y' AND TRIM(UPPER(REPLACE(VEHICLE_USAGE_DESC,'\\n','')))=TRIM(UPPER(REPLACE(?2,'\\n',''))) AND AMEND_ID=(SELECT MAX(AMEND_ID) FROM MOTOR_VEHICLEUSAGE_MASTER WHERE COMPANY_ID=?1 AND STATUS='Y' AND TRIM(UPPER(REPLACE(VEHICLE_USAGE_DESC,'\\n','')))=TRIM(UPPER(REPLACE(?2,'\\n','')))  AND HPM.VEHICLE_USAGE_ID=VEHICLE_USAGE_ID)")
	String getVehicleUsageId(String companyId,String desc);
	
	@Query(nativeQuery=true,value ="SELECT distinct BODY_ID FROM EWAY_MOTOR_BODYTYPE_MASTER HPM WHERE COMPANY_ID=?1 AND STATUS='Y' AND TRIM(UPPER(REPLACE(BODY_NAME_EN,'\\n','')))=TRIM(UPPER(REPLACE(?2,'\\n',''))) AND AMEND_ID=(SELECT MAX(AMEND_ID) FROM EWAY_MOTOR_BODYTYPE_MASTER WHERE COMPANY_ID=?1 AND STATUS='Y' AND TRIM(UPPER(REPLACE(BODY_NAME_EN,'\\n','')))=TRIM(UPPER(REPLACE(?2,'\\n',''))) AND HPM.BODY_ID=BODY_ID)")
	String getBodyTypeId(String companyId,String desc);
	
	
}
