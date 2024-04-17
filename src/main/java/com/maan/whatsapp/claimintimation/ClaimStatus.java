package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimStatus {
	
	@JsonProperty("ChassisNo")
	private String chassisNo;

	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
}
