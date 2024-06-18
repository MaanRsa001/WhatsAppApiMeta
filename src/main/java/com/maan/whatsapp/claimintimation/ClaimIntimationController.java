package com.maan.whatsapp.claimintimation;

import java.text.ParseException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.whatsapp.config.exception.WhatsAppValidationException;

@RestController
@RequestMapping("/claim")
public class ClaimIntimationController {

	
	@Autowired
	private ClaimIntimationServiceImpl service;
	
	@PostMapping("/intimation")
	public Object claimintimation(@RequestBody ClaimIntimationReq req){
		return service.claimintimation(req);
	}
	
	@PostMapping("/validate")
	public void validateInputField(@RequestBody ClaimIntimationValidationReq req) throws WhatsAppValidationException, NumberFormatException, ParseException {
		 service.validateInputField(req);
	}
	
	@PostMapping("/upload/preinspection")
	public Object uploadPreinspection(@RequestBody PreInspectionReq req) {
		return service.uploadPreinspection(req);
	}
	
	@PostMapping("/status")
	public Object checkClaimStatus(@RequestBody ClaimStatus req) throws WhatsAppValidationException{
		return service.checkClaimStatus(req);
	}
	
	@PostMapping("/shorttermpolicy")
	public Object shortTermPolicy(@RequestBody ShortTermPolicyReq req) {
		return service.shortTermPolicy(req);
	}
	
	@PostMapping("/shorttermpolicyresponse")
	public Object shortTermPolicyResponse(@RequestBody ShortTermPolicyReq req) {
		return service.shortTermPolicyResponse(req);
	}
	
	@PostMapping("/getClaimRefNo")
	public Object getClaimRefNo(@RequestBody ClaimRefNoReq req) throws WhatsAppValidationException {
		return service.getClaimRefNo(req);
	}
	
	@PostMapping("/intimate/new")
	public Object intimateNewClaim(@RequestBody IntimateNewClaimReq req) {
		return service.intimateNewClaim(req);
	}
	
	@PostMapping("/getClaimDetailsByClaimRefNo")
	public Map<String,String> getClaimDetailsByClaimRefNo(@RequestBody Map<String,String> req){
		return service.getClaimDetailsByClaimRefNo(req);
	}

	
}
