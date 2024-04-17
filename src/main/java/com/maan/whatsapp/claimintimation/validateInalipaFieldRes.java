package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class validateInalipaFieldRes {

	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("ErrorMsg")
	private String errorMsg;
	
}
