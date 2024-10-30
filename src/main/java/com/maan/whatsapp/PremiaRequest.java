package com.maan.whatsapp;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiaRequest {
	
	@JsonProperty("Token")
	private String token;
	
	@JsonProperty("QuoteNoList")
	private List<String> quoteNoList;

}
