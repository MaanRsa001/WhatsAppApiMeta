package com.maan.whatsapp.repository.master;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.whatsapp.entity.master.WhatsappMessageMenuMaster;
import com.maan.whatsapp.entity.master.WhatsappMessageMenuMasterID;

@Repository
public interface WhatsappMessageMenuMasterRepository extends JpaRepository<WhatsappMessageMenuMaster, WhatsappMessageMenuMasterID> {


	List<WhatsappMessageMenuMaster> findByMessageIdAndStatusIgnoreCase(String messageId, String status);

	List<WhatsappMessageMenuMaster> findByMessageIdAndStatusIgnoreCaseOrderByDisplayOrder(String messageId,
			String string);

}
