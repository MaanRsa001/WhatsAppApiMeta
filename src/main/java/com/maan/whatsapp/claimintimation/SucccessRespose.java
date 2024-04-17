package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucccessRespose {
	
	@JsonProperty("VehicleInfo")
	private VehicleInfo vehicleInfo;
	
	@JsonProperty("PolicyInfo")
	private PolicyInfo policyInfo;

}
