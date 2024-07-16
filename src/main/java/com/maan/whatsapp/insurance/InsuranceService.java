package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;

public interface InsuranceService {

	void validateInputField(InsuranceReq req) throws WhatsAppValidationException;

	Object generateQuote(InsuranceReq req) throws WhatsAppValidationException ,JsonMappingException, JsonProcessingException;

	Object getMotorSectionUsage(MotorSectionImageReq req);

	String buypolicy(String request) throws WhatsAppValidationException ;

	Object b2cGenerateQuote(B2CQuoteRequest req) throws WhatsAppValidationException;

	Object renewalQuote(B2CQuoteRequest req)throws WhatsAppValidationException ;

	Object generateStpQuote(InsuranceReq req) throws WhatsAppValidationException,JsonMappingException, JsonProcessingException ;

	Object getPreinspectionDetails(UploadPreinspectionReq req);
	
}
