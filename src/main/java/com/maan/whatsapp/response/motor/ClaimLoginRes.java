package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClaimLoginRes {
	
	@JsonProperty("Token")
	private String token;
	@JsonProperty("LoginResponse")
	private String loginResponse;
	@JsonProperty("Errors")
	private String error;

}
