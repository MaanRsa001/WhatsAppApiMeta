package com.maan.whatsapp.insurance;

import com.maan.whatsapp.config.exception.WhatsAppValidationException;

public interface InsuranceService {

	void validateInputField(InsuranceReq req) throws WhatsAppValidationException;

	Object generateQuote(InsuranceReq req) throws WhatsAppValidationException ;

	Object getMotorSectionUsage(MotorSectionImageReq req);

	String buypolicy(String request) throws WhatsAppValidationException ;

	Object b2cGenerateQuote(B2CQuoteRequest req) throws WhatsAppValidationException;
	
}
