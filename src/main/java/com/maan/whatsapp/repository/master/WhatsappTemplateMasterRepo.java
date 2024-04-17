package com.maan.whatsapp.repository.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.WhatsappTemplateMaster;
import com.maan.whatsapp.entity.master.WhatsappTemplateMasterPK;

@Repository
@QuerydslPredicate
public interface WhatsappTemplateMasterRepo extends JpaRepository<WhatsappTemplateMaster, WhatsappTemplateMasterPK>,
		QuerydslPredicateExecutor<WhatsappTemplateMaster> {

	@Query(value = "SELECT STATUS FROM CONSTANT_DETAIL WHERE CATEGORY_ID=?1 AND CATEGORY_DETAIL_ID=?2", nativeQuery = true)
	String getStatus(String categoryid, String detailid);

	@Query(value = "SELECT REMARKS FROM CONSTANT_DETAIL WHERE CATEGORY_ID=?1 AND STATUS='Y' AND REMARKS IS NOT NULL", nativeQuery = true)
	List<String> getRemarks(String categoryid);

	@Query(value = "SELECT STATUS FROM CONSTANT_DETAIL WHERE CATEGORY_ID=?1 AND CATEGORY_DETAIL_ID=?2 AND REMARKS=?3", nativeQuery = true)
	String getStatus(String categoryid, String detailid, String ip);

	@Query(value = "SELECT DISTINCT COUNTRY_MOBILECODE FROM MOTOR_COUNTRY_MASTER WHERE STATUS='Y' AND COUNTRY_MOBILECODE IS NOT NULL", nativeQuery = true)
	List<String> getMobCode();

}
