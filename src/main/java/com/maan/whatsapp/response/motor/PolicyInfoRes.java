package com.maan.whatsapp.response.motor;

import org.springframework.batch.core.configuration.annotation.JobScope;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PolicyInfoRes {
	
	@JsonProperty("PolicyNo")
	private String policyNo;
	
	@JsonProperty("CivilId")
	private String civilId;
	
	@JsonProperty("ChassisNo")
	private String chassisNo;
	
	@JsonProperty("InsuredName")
	private String customerName;
	
	@JsonProperty("Plate_Number")
	private String registraionMark;
	
	
	


}
