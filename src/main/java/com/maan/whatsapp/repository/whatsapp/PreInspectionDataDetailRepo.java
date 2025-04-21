package com.maan.whatsapp.repository.whatsapp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.PreinspectionDataDetail;

import jakarta.transaction.Transactional;

@Repository
public interface PreInspectionDataDetailRepo extends JpaRepository<PreinspectionDataDetail, Long> {

	@Query(value = "SELECT TRANID_SEQ.NEXTVAL FROM DUAL",nativeQuery=true)
	Long getTranId();
	
	@Modifying
	@Transactional
	@Query(value ="insert into WH_PREINSPECTION_IMAGE_DETAIL(TRANID,IMAGENAME,IMAGEFILEPATH,ENTRY_DATE,STATUS,EXIF_IMAGE_DATE,EXIF_IMAGE_STATUS,ORIGINAL_FILE_NAME,IS_CAPTURE_UPLOAD) VALUES (?1,?2,?3,sysdate,'Y',?4,?5,?6,?7)",nativeQuery=true)
	int insertImageDetails(String tranId,String imageName,String imagePath, Date exifDate,String exifStatus,String orginalFileName,String isCaptureUpload);

	@Query(value = "select trunc(entry_date) as ENTRY_DATE,count(*)as TOTAL_COUNT from wh_PreInspection_Data_Detail group by trunc(entry_date) order by entry_date desc",nativeQuery=true)
	List<Map<String,Object>> getPreInspectionImage();

	@Query(value = "SELECT pdd.tranid, pdd.registraionno, pdd.chassisno, pdd.mobileno, wcd.sendername, pid.imagename, pid.imagefilepath,pid.original_file_name FROM wh_preinspection_data_detail pdd, whatsapp_contact_data wcd, wh_preinspection_image_detail pid WHERE trunc(pdd.entry_date) between trunc(to_date(?1,'DD/MM/YYYY')) and trunc(to_date(?2,'DD/MM/YYYY')) AND pdd.mobileno = wcd.whatsappid and pdd.tranid=pid.tranid order by pdd.entry_date",nativeQuery=true)
	List<Map<String, Object>> getPreInspectionImageByDate(String startDate ,String endDate);
	
	@Query(value="select senderName from whatsapp_contact_data where whatsappid=?1",nativeQuery=true)
	String getCustomerName(String watiId);

	@Query(value ="select IMAGENAME,IMAGEFILEPATH,ORIGINAL_FILE_NAME FROM wh_PreInspection_image_Detail WHERE TRANID=?1",nativeQuery=true)
	List<Map<String, Object>> getPreInspectionImageByTranId(String tranId);

	@Query(value ="select * from WH_PREINSPECTION_IMAGE_DETAIL where tranid=?1",nativeQuery=true)
	List<Map<String, Object>> getGroupOfRecordByTranId(String tranId);
	
	@Query(value ="select WCD.WHATSAPPID,WCD.SENDERNAME from WH_PREINSPECTION_DATA_DETAIL PDD,WHATSAPP_CONTACT_DATA WCD where PDD.MOBILENO=WCD.WHATSAPPID AND TRANID=?1",nativeQuery=true)
	List<Map<String, Object>> getMobileNoByTranId(String mobileNo);
	
	@Query(value="select item_value from list_item_value where item_type='PRE_INSPECTION_IMAGES' and status='Y'",nativeQuery=true)
	String getPreinsDocuments();

	@Query(value="select pdd.REGISTRAIONNO,pdd.CHASSISNO,pdd.MOBILENO,wcd.sendername,pmd.* from WH_PREINSPECTION_IMAGE_DETAIL pmd,WH_PREINSPECTION_DATA_DETAIL pdd ,WHATSAPP_CONTACT_DATA wcd where pmd.tranid in((select tranid from WH_PREINSPECTION_DATA_DETAIL where REGISTRAIONNO=?1)) and pdd.tranid=pmd.tranid and pdd.mobileno=wcd.whatsappid",nativeQuery=true)
	List<Map<String, Object>> searchPreInspectionByRegNo(String regNo);

	@Query(value="select pdd.REGISTRAIONNO,pdd.CHASSISNO,pdd.MOBILENO,wcd.sendername,pmd.* from WH_PREINSPECTION_IMAGE_DETAIL pmd,WH_PREINSPECTION_DATA_DETAIL pdd ,WHATSAPP_CONTACT_DATA wcd where pmd.tranid in((select tranid from WH_PREINSPECTION_DATA_DETAIL where CHASSISNO=?1)) and pdd.tranid=pmd.tranid and pdd.mobileno=wcd.whatsappid",nativeQuery=true)
	List<Map<String, Object>> searchPreInspectionByChassisNo(String chassisNo);
	
	
}
