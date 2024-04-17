package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ClaimIntimationValidationReq {

	@JsonProperty("Type")
	private String type;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@JsonProperty("Vehicle")
	private String vehicle;
	
	@JsonProperty("LossType")
	private String lossType;
	
	@JsonProperty("CauseOfLoss")
	private String causeOfLoss;
	
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
	
	@JsonProperty("Claimrefno")
	private String claimrefno;
	
	@JsonProperty("BodyId")
	private String bodyId;
	
	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("ChassisNo")
	private String chassisNo;
	
	@JsonProperty("ModelId")
	private String modelId;
	
	@JsonProperty("EngineCapacity")
	private String engineCapacity;
	
	@JsonProperty("EngineNo")
	private String engineNo;
	
	@JsonProperty("SeatingCapacity")
	private String seatingCapacity;
	
	@JsonProperty("ManufactureYear")
	private String manufactureYear;
	
	@JsonProperty("ClaimType")
	private String claimType;
	
	@JsonProperty("ClaimNo")
	private String claimNo;
	
}
