package com.maan.whatsapp.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WhatsappDropdownRes {

	@JsonProperty("Code")
	private String code ; 
	
	@JsonProperty("CodeDesc")
	private String codeDesc ;
	
	@JsonProperty("Status")
	private String status ;
}
