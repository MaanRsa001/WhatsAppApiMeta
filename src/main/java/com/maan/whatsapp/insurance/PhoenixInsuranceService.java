package com.maan.whatsapp.insurance;

import com.maan.whatsapp.config.exception.WhatsAppValidationException;

public interface PhoenixInsuranceService {

	String buypolicy(String request)throws WhatsAppValidationException;

}
