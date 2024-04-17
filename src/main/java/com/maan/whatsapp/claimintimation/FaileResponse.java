package com.maan.whatsapp.claimintimation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class FaileResponse {
	
	@JsonProperty("Errors")
	List<ClaimErrors> Errors;
	

}
