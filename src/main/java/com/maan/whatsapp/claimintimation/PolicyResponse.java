package com.maan.whatsapp.claimintimation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyResponse {
	
	@JsonProperty("Succcess Respose")
	private List<SucccessRespose> succcessRespose;
	
	@JsonProperty("Faile Response")
	private FaileResponse faileResponse;
	

}
