package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;

public interface SwazilandInsuranceService {

	Object swazilandQuote(Object req)throws JsonProcessingException,JsonMappingException, WhatsAppValidationException;

}
