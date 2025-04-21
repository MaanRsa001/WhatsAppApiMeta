package com.maan.whatsapp.controller.whatsapptemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.maan.whatsapp.insurance.SearchPreInsPectionReq;
import com.maan.whatsapp.request.motor.DocumentResponse;
import com.maan.whatsapp.request.motor.PreFileUploadReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchatrecipiantReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchattempReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsapptemplateReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatschatdeleteRes;
import com.maan.whatsapp.response.motor.FileUploadRes;
import com.maan.whatsapp.response.motor.PolicyInfoReq;
import com.maan.whatsapp.response.motor.PolicyInfoRes;
import com.maan.whatsapp.response.motor.WACommonRes;
import com.maan.whatsapp.service.whatsapptemplate.WhatsapptemplateService;

@RestController
@RequestMapping("/whatsapptemplate")
public class WhatsapptemplateController {
	
	Logger log =LogManager.getLogger(WhatsapptemplateController.class);
	
	@Autowired
	private WhatsapptemplateService service;
	
	
	
	@GetMapping("/templatelist/{agencycode}/{productid}")
	public  List<WhatsapptemplateReq> templatelist(@PathVariable String agencycode,@PathVariable String productid){
		List<WhatsapptemplateReq> response = service.gettemplatelist(agencycode, productid);
		return response;
	}
	
	
	@GetMapping("/maintemplateedit/{productid}/{agencycode}/{stagecode}/{substagecode}")
	public  WhatsapptemplateReq getmaintemplateedit(@PathVariable String productid,@PathVariable String agencycode,@PathVariable String stagecode, @PathVariable String substagecode){
		WhatsapptemplateReq response = service.getmaintemplateedit(productid, agencycode, stagecode, substagecode);
		return response;
	}
	
	@PostMapping("/maintemplatesave")
	public  WhatsapptemplateReq maintemplatesave(@RequestBody WhatsapptemplateReq req){
		WhatsapptemplateReq response = service.maintemplatesave(req);
		return response;
	}
		
	
	/*  Whatsapp chat master Start  */
	
	@GetMapping("/chattemplateparentlist/{messageid}")
	public  List<WhatsappchattempReq> chattemplateparentlist(@PathVariable String messageid){
			List<WhatsappchattempReq> response = service.chattemplateparentlist(messageid);
		return response;
	}
	
	@GetMapping("/chattemplatechildlist/{parentmessageid}")
	public  List<WhatsappchatrecipiantReq> chattemplatechildlist(@PathVariable String parentmessageid){
			List<WhatsappchatrecipiantReq> response = service.chattemplatechildlist(parentmessageid);
		return response;
	}
	
	@GetMapping("/chattemplatechildlistedit/{messageid}/{parentmessageid}")
	public  WhatsappchatrecipiantReq chattemplatechildlistedit(@PathVariable String messageid, @PathVariable String parentmessageid){
			WhatsappchatrecipiantReq response = service.chattemplatechildlistedit(messageid, parentmessageid);
		return response;
	}
	
	@PostMapping("/chattemplateparent/save")
	public  WhatsappchattempReq chattemplateparentsave(@RequestBody WhatsappchattempReq req){
			WhatsappchattempReq response = service.chattemplateparentsave(req);
		return response;
	}
	
	@PostMapping("/chattemplateans/save")
	public  WhatsappchatrecipiantReq chattemplateanssave(@RequestBody WhatsappchatrecipiantReq req){
		WhatsappchatrecipiantReq response = service.chattemplateanssave(req);
		return response;
	}
	
	@GetMapping("/maintemplatechatlist/{remarksid}")
	public   List<WhatsapptemplateReq> maintemplatechatlist(@PathVariable String remarksid){
		 	 List<WhatsapptemplateReq> response = service.maintemplatechatlist(remarksid);
		return response;
	}
	
	/*  Whatsapp chat master End  */
	
	@GetMapping("/getchatdeletepreview/{id}")
	public  List<WhatschatdeleteRes> getchatdeletepreview(@PathVariable String id){
		List<WhatschatdeleteRes> response = service.getchatdeletepreview(id);
		return response;
	}
	
	@GetMapping("/chatdeleterec/{id}")
	public WhatschatdeleteRes chatdeleterec(@PathVariable String id){
		WhatschatdeleteRes response = service.chatdeleterec(id);
		return response;
	}
	
	@GetMapping("/getPreinspectionImages")
	public WACommonRes getPreinspectionImageByDate() {
		return service.getPreinspectionImageByDate();
	}
	
	@GetMapping("/getPreinspectionImagesByDate")
	public WACommonRes getPreinspectionImagesByDate(@RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate) {
		return service.getPreinspectionImagesByDate(startDate,endDate);
	}
	
	@GetMapping("/getPreinspectionImagesByTranId")
	public WACommonRes getPreinspectionImagesByTranId(@RequestParam("tranId") String tranId) {
		return service.getPreinspectionImagesByTranId(tranId);
	}
	
	
	@GetMapping("/document/download")
	public ResponseEntity<Resource> download(@RequestParam("FilePath") String filePath,@RequestParam("OriginalFileName") String originalFileName) throws IOException {
		File file = new File(filePath);

	    InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
	    
	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"")
	            .contentLength(file.length())
	            .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
	            .body(resource);
	}
	
	
	@PostMapping("/get/policy/info")
	public PolicyInfoRes getPolicyInfo(@RequestBody PolicyInfoReq req ) {
		log.info("/get/policy/info request || "+new Gson().toJson(req));
		return PolicyInfoRes.builder()
				.chassisNo("HSFAFAK2324")
				.civilId("4363636")
				.customerName("MaanSarovar")
				.policyNo("P-01-1007WB-2023-567")
				.registraionMark("B Y/7825")
				.build();
	}
		
	
	@PostMapping("/upload/file")
	public FileUploadRes uploadFile(@RequestBody PreFileUploadReq req) {
		return service.uploadFile(req);
	}
	
	@GetMapping("/send/whatsapp/message")
	public String sendWhatsappMessage(@RequestParam("tranId") String tranId) {
		return service.sendWhatsappMessage(tranId);
	}
	
	@GetMapping("/master/documents")
	public List<DocumentResponse> getMasterDocuments() {
		return service.getMasterDocuments();
		
	}
	
	@PostMapping("/search/preinspection")
	public WACommonRes searchPreInspection(@RequestBody SearchPreInsPectionReq req){
		return service.searchPreInspection(req);
	}
	
}
