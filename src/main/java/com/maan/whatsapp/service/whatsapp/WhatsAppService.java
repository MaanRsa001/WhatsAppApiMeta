package com.maan.whatsapp.service.whatsapp;

import java.util.List;

import com.maan.whatsapp.request.motor.WAQuoteReq;
import com.maan.whatsapp.request.wati.getcont.WebhookReq;
import com.maan.whatsapp.request.whatsapp.WhatsAppReq;
import com.maan.whatsapp.response.motor.WAQuoteRes;

public interface WhatsAppService {

	String saveRequestDetail(List<WhatsAppReq> request);

	String webhookRes(WebhookReq request);

	String sendSessExpMsg();

	List<WAQuoteRes> getWAMsgDet(WAQuoteReq request);

}
