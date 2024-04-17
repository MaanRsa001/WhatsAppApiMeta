package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimErrors {

	@JsonProperty("Code")
	private String Code;
	
	@JsonProperty("Field")
	private String Field;
	
	@JsonProperty("Message")
	private String Message;
	
}

