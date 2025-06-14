package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;

public interface ZambiaInsuranceService {

	Object generateZambiaQuote(InsuranceReq req) throws JsonProcessingException,JsonMappingException, WhatsAppValidationException;

	Object zambiaQuote(Object req) throws JsonMappingException, JsonProcessingException, WhatsAppValidationException;

}
