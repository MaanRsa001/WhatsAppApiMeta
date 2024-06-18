package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IntimateNewClaimReq {
	
	private String claim_form;
	
	private String location_desc;
	
	@JsonProperty("WhatsAppNo")
	private String WhatsAppNo;
	
	@JsonProperty("WhatsAppCode")
	private String WhatsAppCode;

}
