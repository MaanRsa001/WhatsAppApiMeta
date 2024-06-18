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
import com.maan.whatsapp.auth.basic.ImageDecryptionService;
import com.maan.whatsapp.auth.basic.WhatsappEncryptionDecryption;
import com.maan.whatsapp.meta.MetaEncryptDecryptRes;

@RestController
@RequestMapping("/whatsappflow")
public class WhatsappFlowController {
	
	Logger log = LogManager.getLogger(WhatsappFlowController.class);
	
	@Autowired
	private  WhatsapppFlowService service;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
	private ImageDecryptionService imageDecryptionService;
	 
	public static Gson printReq =new Gson();
	
	@PostMapping("/claimIntimation")
	public ResponseEntity<Object> claimIntimation(@RequestBody Map<String,Object> req) {
		try {
			log.info("claimIntimation encrypted request ==>"+printReq.toJson(req));
			MetaEncryptDecryptRes decrypt =WhatsappEncryptionDecryption.metaDecryption(req);
			Map<String,Object> healthCheck =mapper.readValue(decrypt.getEncrypted_flow_data(), Map.class);
			String action =healthCheck.get("action")==null?"":healthCheck.get("action").toString();
			log.info("/claimIntimation decrypted request : "+printReq.toJson(decrypt));

			if("ping".equals(action)) {
				
				String version =healthCheck.get("version")==null?"":healthCheck.get("version").toString();
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
								
				String encryptRes =service.claimIntimation(healthCheck);
				decrypt.setEncrypted_flow_data(encryptRes);
				
				String encrypt_response =WhatsappEncryptionDecryption.metaEncryption(decrypt);
				return new ResponseEntity<Object>(encrypt_response,HttpStatus.OK);
			}
				
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@PostMapping("/shortTermPolicy")
	public ResponseEntity<Object> createShortTermPolicy(@RequestBody Map<String,Object> req) throws JsonMappingException, JsonProcessingException{
	
		log.info("/ShortTermPolicy encrypted request : "+printReq.toJson(req));
		MetaEncryptDecryptRes dcryptData =WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> request =mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		String action =request.get("action")==null?"":request.get("action").toString();
		log.info("/ShortTermPolicy decrypted request : "+printReq.toJson(dcryptData));

		if("ping".equals(action)) {
			String version =request.get("version")==null?"":request.get("version").toString();
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
						
			String response =service.createShortTermPolicy(request);
			dcryptData.setEncrypted_flow_data(response);
			
			String encrypt_response =WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			return new ResponseEntity<Object>(encrypt_response, HttpStatus.OK);
		}
		
	}
	
	@GetMapping("/quotation/flow/screen/data")
	public Map<String,Object> quotation_flow_screen_data(){
		return service.quotation_flow_screen_data();
	}
	
	@GetMapping("/stp/flow/screen/data")
	public Map<String,Object> stp_flow_screen_data(){
		return service.stp_flow_screen_data();
	}
	
	@GetMapping("/claim/intimation/screen/data")
	public Map<String,Object> claimIntimateScreenData(){
		return service.claimIntimateScreenData();
	}
	
	@GetMapping("/inalipa/intimation/screen/data")
	public Map<String,Object> InalipaIntimateScreenData(){
		return service.InalipaIntimateScreenData();
	}
	
	@GetMapping("/preinspection/screen/data")
	public Map<String,Object> preinspectionScreenData(){
		return service.preinspectionScreenData();
	}
	
	@PostMapping("/create/vehicle/quotation")
	public ResponseEntity<Object> createVehicleQuotation(@RequestBody Map<String,Object> req) throws JsonProcessingException{
		log.info("/create/vehicle/quotation : "+printReq.toJson(req));
		MetaEncryptDecryptRes dcryptData =WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> request =mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		log.info("/create/vehicle/quotation"+printReq.toJson(request));

		String action =request.get("action")==null?"":request.get("action").toString();
		
		// health check
		if("ping".equals(action)) {
				
				String version =request.get("version")==null?"":request.get("version").toString();
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
			
			String response =service.createVehicleQuotation(request);
			
			dcryptData.setEncrypted_flow_data(response);
			
			String encrypt_response =WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(encrypt_response, HttpStatus.OK);
			
		}
		
	}
	
	
	@PostMapping("/inalipa/claim/intimation")
	public ResponseEntity<Object> inalipaClaimIntimation(@RequestBody Map<String,Object> req) throws JsonMappingException, JsonProcessingException{
		log.info("/inalipa/claim/intimation || encrypt request : "+printReq.toJson(req));
		MetaEncryptDecryptRes dcryptData =WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> request =mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		log.info("/inalipa/claim/intimation || decrypt request :"+printReq.toJson(request));

		String action =request.get("action")==null?"":request.get("action").toString();
		
		// health check
		if("ping".equals(action)) {
				
				String version =request.get("version")==null?"":request.get("version").toString();
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
			
			String response =service.inalipaClaimIntimation(request);
			
			dcryptData.setEncrypted_flow_data(response);
			
			String encrypt_response =WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(encrypt_response, HttpStatus.OK);
			
		}
		
	}
	
	@GetMapping("/getInalipaClaimTypes")
	public Object getInalipaClaimTypes(){
		return service.getInalipaClaimTypes();
	}
	
	@PostMapping("/preinspection/image/upload")
    public ResponseEntity<Object> decryptMetaImage(@RequestBody Map<String,Object> req) throws Exception {
		log.info("/decrypt/meta/images || encrypt request : "+printReq.toJson(req));
		MetaEncryptDecryptRes dcryptData =WhatsappEncryptionDecryption.metaDecryption(req);
		Map<String,Object> request =mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
		log.info("/decrypt/meta/images || decrypt request :"+printReq.toJson(request));

		String action =request.get("action")==null?"":request.get("action").toString();
		
		// health check
		if("ping".equals(action)) {
				
				String version =request.get("version")==null?"":request.get("version").toString();
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
			
			Map<String,Object> data =mapper.readValue(dcryptData.getEncrypted_flow_data(), Map.class);
				
			String response =service.decryptMetaImage(data);
						
			dcryptData.setEncrypted_flow_data(response);
			
			String encrypt_response =WhatsappEncryptionDecryption.metaEncryption(dcryptData);
			
			return new ResponseEntity<Object>(encrypt_response, HttpStatus.OK);
			
		}
		
	}
	
}
