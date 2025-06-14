package com.maan.whatsapp.insurance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
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
public class PhoenixInsuranceController {
	
	@Autowired
	private NamibiaInsuranceService serviceNamibia;
	
	@Autowired
	private ZambiaInsuranceService serviceZambia;
	
	@Autowired
	private PhoenixInsuranceService insService;
	
	@Autowired
	private SwazilandInsuranceService serviceSwaziland;
	
	@PostMapping("/generate/namibia/quote")
	public Object generateNamibiaQuote(@RequestBody InsuranceReq req) throws WhatsAppValidationException,JsonProcessingException, JsonMappingException{
		return serviceNamibia.generateNamibiaQuote(req);
	}

	@GetMapping("/phoenix/buypolicy/{request}")
	public void buypolicy(@PathVariable("request") String request,HttpServletResponse reponse) throws Exception{
		String redirectLink = insService.buypolicy(request);
		reponse.sendRedirect(redirectLink);
	}
	
	//@PostMapping("/generate/zambia/quote")
	public Object generateZambiaQuote(@RequestBody InsuranceReq req) throws WhatsAppValidationException,JsonProcessingException, JsonMappingException{
		return serviceZambia.generateZambiaQuote(req);
	}
	
	@PostMapping("/generate/zambia/quote")
	public Object ZambiaQuote(@RequestBody Object req) throws WhatsAppValidationException,JsonProcessingException, JsonMappingException{
		return serviceZambia.zambiaQuote(req);
	}
	
	@PostMapping("/generate/swaziland/quote")
	public Object generateSwazilandQuote(@RequestBody Object req) throws WhatsAppValidationException,JsonProcessingException, JsonMappingException{
		return serviceSwaziland.swazilandQuote(req);
	}
}
