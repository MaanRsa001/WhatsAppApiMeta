package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimDocRes {
	
	@JsonProperty("DocumentNo")
	private String docNo;
	@JsonProperty("DocumentName")
	private String docName;
	@JsonProperty("WhatsappText")
	private String whatsText;
	
	@JsonProperty("Partyno")
	private String partyId;
	@JsonProperty("Claimrefno")
	private String claimno;
	@JsonProperty("Losstypeid")
	private String lossId;
	@JsonProperty("message")
	private String message;

}
