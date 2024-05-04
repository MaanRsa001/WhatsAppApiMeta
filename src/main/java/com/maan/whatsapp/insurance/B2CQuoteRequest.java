package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class B2CQuoteRequest {
	
	@JsonProperty("VehicleForm")
	private String vehicleForm;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@JsonProperty("RegistrationNo")
	private String regisrationNo;
	

	@JsonProperty("TypeofInsurance")
	private String typeofInsurance;

	@JsonProperty("WhatsAppCode")
	private String whatsAppCode;
	
	@JsonProperty("WhatsAppNo")
	private String whatsAppNo;
	
	@JsonProperty("AirtelPayNo")
	private String airtelPayNo;

	@JsonProperty("SumInsured")
	private String sumInsured;
	

}
