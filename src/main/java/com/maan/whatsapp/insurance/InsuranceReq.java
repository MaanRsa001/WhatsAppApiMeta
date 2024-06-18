package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceReq {
	
	@JsonProperty("WhatsAppCode")
	private String whatsAppCode;
	
	@JsonProperty("WhatsAppNo")
	private String whatsAppNo;
	
	private String quote_form;
	
	private String mobile_no;
	
	private String type;


}
