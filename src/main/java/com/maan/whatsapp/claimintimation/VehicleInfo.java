package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleInfo {
	
	@JsonProperty("Vehicletypedesc")
	private String Vehicletypedesc;
	
	@JsonProperty("Vehiclemodeldesc")
	private String Vehiclemodeldesc;
	
	@JsonProperty("Platenocharacter")
	private String Platenocharacter;
	
	@JsonProperty("ChassisNo")
	private String ChassisNo;
	
	@JsonProperty("Suminsured")
	private String Suminsured;
	
	@JsonProperty("Seating")
	private String Seating;
	
	@JsonProperty("VechRegNo")
	private String VechRegNo;
	
	@JsonProperty("Manufactureyear")
	private String Manufactureyear;
	
	

}
