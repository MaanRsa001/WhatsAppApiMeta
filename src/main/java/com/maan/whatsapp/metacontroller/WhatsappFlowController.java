package com.maan.whatsapp.metacontroller;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class WhatsappFlowController {
	
	Logger log = LogManager.getLogger(WhatsappFlowController.class);
	
	@Autowired
	private  WhatsapppFlowService service;
	
	ObjectMapper mapper = new ObjectMapper();
	
	public static Gson printReq =new Gson();
	
	
	
	@PostMapping("/claimIntimation")
	public ResponseEntity<Object> claimIntimation(@RequestBody Map<String,Object> req) {
		try {
			log.info("claimIntimation encrypted request ==>"+printReq.toJson(req));
			MetaEncryptDecryptRes decrypt =WhatsappEncryptionDecryption.metaDecryption(req);
			Map<String,Object> healthCheck =mapper.readValue(decrypt.getEncrypted_flow_data(), Map.class);
			String action =healthCheck.get("action")==null?"":healthCheck.get("action").toString();
			// health check
			if("ping".equals(action)) {
				
				String version =healthCheck.get("version")==null?"":healthCheck.get("version").toString();
				log.info("/webhook/meta/dynamic health check version  ==>"+version);
				Map<String,Object> data =new HashMap<String, Object>();
				data.put("status", "active");
				
				Map<String,Object> healthCheckReq =new HashMap<String, Object>();
				healthCheckReq.put("version", version);
				healthCheckReq.put("data", data);
				
				String encryptReq =printReq.toJson(healthCheckReq);
				
				
				decrypt.setEncrypted_flow_data(encryptReq);
				String response =WhatsappEncryptionDecryption.metaEncryption(decrypt);
				
				
				return new ResponseEntity<Object>(response,HttpStatus.OK);
			}else {
				
				Map<String,Object> data =(Map<String,Object>) healthCheck.get("data");
				
				String encryptRes =service.claimIntimation(data);
				
				return new ResponseEntity<Object>(encryptRes,HttpStatus.OK);
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@PostMapping("/shortTermPolicy")
	public ResponseEntity<Object> createShortTermPolicy(@RequestBody Map<String,Object> req) throws JsonMappingException, JsonProcessingException{
		MetaEncryptDecryptRes dcryptData =WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> healthCheck =mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		String action =healthCheck.get("action")==null?"":healthCheck.get("action").toString();

		if("ping".equals(action)) {
			String version =healthCheck.get("version")==null?"":healthCheck.get("version").toString();
			Map<String,Object> data =new HashMap<String, Object>();
			data.put("status", "active");
			
			Map<String,Object> healthCheckReq =new HashMap<String, Object>();
			healthCheckReq.put("version", version);
			healthCheckReq.put("data", data);
			
			String encryptReq =printReq.toJson(healthCheckReq);
			dcryptData.setEncrypted_flow_data(encryptReq);
			String response =WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		}else {
			
			Map<String,Object> data =(Map<String,Object>) healthCheck.get("data");
			
			log.info("/ShortTermPolicy Request : "+printReq.toJson(data));
			
			String response =service.createShortTermPolicy(data);
		}
		
		return null;
	}

}
