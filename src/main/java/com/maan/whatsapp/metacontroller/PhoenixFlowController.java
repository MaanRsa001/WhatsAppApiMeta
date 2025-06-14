package com.maan.whatsapp.metacontroller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.auth.basic.WhatsappEncryptionDecryption;
import com.maan.whatsapp.meta.MetaEncryptDecryptRes;

@RestController
@RequestMapping("/whatsappflow")
public class PhoenixFlowController {

	Logger log = LogManager.getLogger(PhoenixFlowController.class);
	
	ObjectMapper mapper = new ObjectMapper();
	
	public static Gson printReq =new Gson();
	
	@Autowired
	private PhoenixZambiaFlowService zambiaService;
	
	@Autowired
	private PhoenixNamibiaFlowService namibiaService;
	
	@PostMapping("/create/zambia/quote")
	public ResponseEntity<Object> createZambiaQoute(@RequestBody Map<String, Object> req) throws JsonMappingException,JsonProcessingException{
		
		log.info("/createZambiaQuote encrypted request : "+printReq.toJson(req));
		MetaEncryptDecryptRes dcryptData = WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> request = mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		String action = request.get("action") == null ? "" : request.get("action").toString();
		log.info("/createZambiaQuote decrypted request : "+printReq.toJson(dcryptData));
		
		if("ping".equals(action)) {
			String version = request.get("version") == null ? "" : request.get("version").toString();
			Map<String, Object> data = new HashMap<String,Object>();
			data.put("status", "active");
			
			Map<String,Object> healthCheckReq = new HashMap<String,Object>();
			healthCheckReq.put("version", version);
			healthCheckReq.put("data", data);
			
			String encryptReq = printReq.toJson(healthCheckReq);
			dcryptData.setEncrypted_flow_data(encryptReq);
			String response = WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(response,HttpStatus.OK);
		}
		else {
			String response = zambiaService.createZambiaQuote(request);
			dcryptData.setEncrypted_flow_data(response);
			String encrypt_response = WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(encrypt_response,HttpStatus.OK);
		}
	}
	
	@GetMapping("/zambia/flow/screen/data")
	public Map<String,Object> zambiaFlowScreenData(){
		return zambiaService.zambiaFlowScreenData();
	}
	
	@PostMapping("/create/namibia/quote")
	public ResponseEntity<Object> createNamibiaQoute(@RequestBody Map<String, Object> req) throws JsonMappingException,JsonProcessingException{
		log.info("/createNamibiaQuote encrypted request : "+printReq.toJson(req));
		MetaEncryptDecryptRes dcryptData = WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> request = mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		String action = request.get("action") == null ? "" : request.get("action").toString();
		log.info("/createNamibiaQuote decrypted request : "+printReq.toJson(dcryptData));
		
		if("ping".equals(action)) {
			String version = request.get("version") == null ? "" : request.get("version").toString();
			Map<String, Object> data = new HashMap<String,Object>();
			data.put("status", "active");
			
			Map<String,Object> healthCheckReq = new HashMap<String,Object>();
			healthCheckReq.put("version", version);
			healthCheckReq.put("data", data);
			
			String encryptReq = printReq.toJson(healthCheckReq);
			dcryptData.setEncrypted_flow_data(encryptReq);
			String response = WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(response,HttpStatus.OK);
		}else {
			String response = namibiaService.createNamibiaQuote(request);
			dcryptData.setEncrypted_flow_data(response);
			String encrypt_response = WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(encrypt_response,HttpStatus.OK);
		}
	}
	
	@GetMapping("/namibia/flow/screen/data")
	public Map<String,Object> namibiaFlowScreenData(){
		return namibiaService.namibiaFlowScreenData();
	}
}
