package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimRefNoReq {

	private String mobileNo;

	@JsonProperty("AccidentDate")
	private String accidentDate;
	
	@JsonProperty("ClaimType")
	private String claimType;
	
}
