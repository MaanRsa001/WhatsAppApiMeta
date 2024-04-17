package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimIntimationRes {
	
	@JsonProperty("Response")
	private String response;
	
	@JsonProperty("ErrorDesc")
	private String errorDesc;
	
	@JsonProperty("firstName")
	private String firstName;

}
