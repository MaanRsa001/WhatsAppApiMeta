package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClaimDocumentReq {
	
	@JsonProperty("message")
	private String message;
	
}
