package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;

public interface NamibiaInsuranceService {

	Object generateNamibiaQuote(InsuranceReq req) throws JsonProcessingException,JsonMappingException, WhatsAppValidationException;

}
