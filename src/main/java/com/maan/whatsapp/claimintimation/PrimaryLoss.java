package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrimaryLoss {
	
	@JsonProperty("Code")
	private String Code;
	
	@JsonProperty("CodeDesc")
	private String CodeDesc;

}
