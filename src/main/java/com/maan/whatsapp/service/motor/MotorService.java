package com.maan.whatsapp.service.motor;

import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDetail;
import com.maan.whatsapp.request.motor.ClaimDocumentReq;
import com.maan.whatsapp.request.motor.DocInsertReq;

public interface MotorService {

	public String callMotorApi(WhatsappRequestDetail detail, String waid);

	public DocInsertReq getFilePath(DocInsertReq request);

	public String getTreeStructMsg(WhatsappRequestDetail detail);



}
