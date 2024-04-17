package com.maan.whatsapp.repository.whatsapp;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.WhatsappLossMaster;
import com.maan.whatsapp.entity.master.WhatsappLossMasterPk;

@Repository
public interface WhatsappLossRepository extends JpaRepository<WhatsappLossMaster, WhatsappLossMasterPk> {
	
	
	List<WhatsappLossMaster> findByPkLossIdAndStatus(Long lossId,String status);
	
	

}
