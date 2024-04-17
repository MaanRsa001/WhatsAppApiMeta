package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestData {
	
	@JsonProperty("PolicyNo")
	private String policyNumber;
	@JsonProperty("ChassisNo")
	private String chassisNo;
	@JsonProperty("PolicyLossDate")
	private String policyLossDate;
	@JsonProperty("MobileNumber")
	private String mobileNumber;
	@JsonProperty("PolicySearchType")
	private String policySearchType;
	@JsonProperty("Type")
	private String type;
	@JsonProperty("ClaimNo")
	private String claimno;
	@JsonProperty("Plate_Char")
	private String plateChar;
	@JsonProperty("Plate_Number")
	private String plateNumber;
	
}
