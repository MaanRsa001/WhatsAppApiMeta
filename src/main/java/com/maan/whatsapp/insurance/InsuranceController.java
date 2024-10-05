package com.maan.whatsapp.insurance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/insurance")
public class InsuranceController {
	
	
	@Autowired
	private InsuranceService service;
	
	
	@PostMapping("/validate")
	public void validateInputField(@RequestBody InsuranceReq req) throws WhatsAppValidationException{
		service.validateInputField(req);
	}
	
	@PostMapping("/generate/quote")
	public Object generateQuote(@RequestBody InsuranceReq req) throws WhatsAppValidationException,JsonMappingException, JsonProcessingException {
		return service.generateQuote(req);
	}

	@GetMapping("/buypolicy/{request}")
	public void buypolicy(@PathVariable("request") String request,HttpServletResponse reponse) throws Exception{
		String redirectLink =service.buypolicy(request);
		reponse.sendRedirect(redirectLink);
	}
	
	@PostMapping("/get/motor/section/usage")
	public Object getMotorSectionUsage(@RequestBody MotorSectionImageReq req) {
		return service.getMotorSectionUsage(req);
	}
	
	
	@PostMapping("/b2c/generate/quote")
	public Object b2cGenerateQuote(@RequestBody B2CQuoteRequest req) throws WhatsAppValidationException {
		return service.b2cGenerateQuote(req);
	}
	
	@PostMapping("/renewal/quote")
	public Object renewalQuote(@RequestBody B2CQuoteRequest req) throws WhatsAppValidationException {
		return service.renewalQuote(req);
	}
	
	@PostMapping("/generate/stp/quote")
	public Object generateStpQuote(@RequestBody InsuranceReq req) throws WhatsAppValidationException,JsonMappingException, JsonProcessingException {
		return service.generateStpQuote(req);
	}
	
	@PostMapping("/generate/uganda/quote")
	public Object generateUgandaQuote(@RequestBody InsuranceReq req) throws WhatsAppValidationException,JsonMappingException, JsonProcessingException {
		return service.generateUgandaQuote(req);
	}
	
	@PostMapping("/get/preinspection/details")
    public Object getPreinspectionDetails(@RequestBody UploadPreinspectionReq req) {
		return service.getPreinspectionDetails(req);
	}
}
