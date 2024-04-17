package com.maan.whatsapp.service.wati;

import com.maan.whatsapp.entity.master.WAMessageMaster;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDetail;
import com.maan.whatsapp.request.whatsapp.WAWatiReq;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public interface WatiService {

	String updateMsgStatus();

	String sendSessionMsg();

	String sendSessionMsg(Long waid);

	long checkSessionStatus(Long waid);

	String callSendSessionMsg(Long waid, String msgid);

	WAWatiReq sendSessMsg(WAMessageMaster wamsgM, Long waid);

	String storeWAFile(String wafile);


}
