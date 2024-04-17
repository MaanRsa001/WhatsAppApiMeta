package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClaimIntimationReq {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("ChassisNo")
	private String chassisNo;
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@JsonProperty("Vehicle")
	private String vehicle;
	
	@JsonProperty("LossType")
	private String lossType;
	
	@JsonProperty("CauseOfLoss")
	private String causeOfLoss;
	
	@JsonProperty("Type")
	private String ApiType;

	@JsonProperty("AccidentDate")
	private String accidentDate;
	
	@JsonProperty("AccidentTime")
	private String accidentTime;
	
	@JsonProperty("Location")
	private String location;
	
	@JsonProperty("AccidentDesc")
	private String accidentDesc;
	
	@JsonProperty("DrivenBy")
	private String drivenBy;

}
