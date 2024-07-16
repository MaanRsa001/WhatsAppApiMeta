package com.maan.whatsapp.repository.whatsapp;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.PreinspectionImageDetail;

@Repository
public interface PreInspectionDataImageRepo extends JpaRepository<PreinspectionImageDetail, Long>{

	List<PreinspectionImageDetail> findByTranId(Long tranId);
	
	@Query(value ="select p1.*,count(*)over() as total_images from WH_PREINSPECTION_DATA_DETAIL p1,WH_PREINSPECTION_IMAGE_DETAIL p2\r\n"
			+ "where p1.tranid=p2.tranid and p1.tranid=?1",nativeQuery=true)
	List<Map<String,Object>> getPreinspectionDetailsByTranId(String tranId);

}
