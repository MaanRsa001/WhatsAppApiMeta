package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ShortTermPolicyReq {
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("mobileNo")
	private String mobileNumber;
	
	@JsonProperty("BodyId")
	private String bodyId;
	
	@JsonProperty("MakeId")
	private String makeId;
	
}
