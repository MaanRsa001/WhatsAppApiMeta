package com.maan.whatsapp.service.whatsapptemplate;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.maan.whatsapp.insurance.SearchPreInsPectionReq;
import com.maan.whatsapp.request.motor.DocumentResponse;
import com.maan.whatsapp.request.motor.PreFileUploadReq;
import com.maan.whatsapp.request.motor.PreInsPectionReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchatrecipiantReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchattempReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsapptemplateReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatschatdeleteRes;
import com.maan.whatsapp.response.motor.FileUploadRes;
import com.maan.whatsapp.response.motor.WACommonRes;
import com.maan.whatsapp.response.motor.WhFileUploadReq;
public interface WhatsapptemplateService {
	List<WhatsapptemplateReq> gettemplatelist(String agencycode, String productid);
	
	List<WhatsappchattempReq> chattemplateparentlist(String messageid);
	
	List<WhatsappchatrecipiantReq> chattemplatechildlist(String parentmessageid);
	
	WhatsappchatrecipiantReq chattemplatechildlistedit(String messageid, String parentmessageid);
	
	WhatsappchattempReq chattemplateparentsave( WhatsappchattempReq req);
	
	WhatsappchatrecipiantReq chattemplateanssave( WhatsappchatrecipiantReq req);
	
	List<WhatsapptemplateReq> maintemplatechatlist(String remarksid);
	
	WhatsapptemplateReq getmaintemplateedit(String productid, String agencycode, String stagecode, String substagecode);
	
	WhatsapptemplateReq maintemplatesave(WhatsapptemplateReq req);
	
	List<WhatschatdeleteRes> getchatdeletepreview(String id);
	
	WhatschatdeleteRes chatdeleterec(String id);

	WACommonRes getPreinspectionImageByDate();

	WACommonRes getPreinspectionImagesByDate(String entry_date, String endDate);

	WACommonRes getPreinspectionImagesByTranId(String tranId);

	String sendWhatsappMessage(String tranId);

	List<DocumentResponse> getMasterDocuments();

	FileUploadRes uploadFile(PreFileUploadReq req);

	WACommonRes searchPreInspection(SearchPreInsPectionReq req);

}
