package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyInfo {
	

	@JsonProperty("Contactpername")
	private String Contactpername;
	
	@JsonProperty("PolicyNo")
	private String PolicyNo;
	
	@JsonProperty("PolicyFrom")
	private String PolicyFrom;
	
	@JsonProperty("PolicyTo")
	private String PolicyTo;
	
	@JsonProperty("Civilid")
	private String Civilid;
	
	@JsonProperty("Product")
	private String Product;

}
