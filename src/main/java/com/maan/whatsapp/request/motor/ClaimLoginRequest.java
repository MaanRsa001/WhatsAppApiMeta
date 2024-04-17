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
public class ClaimLoginRequest {
	
	@JsonProperty("BranchCode")
	private String BranchCode;
	@JsonProperty("InsuranceId")
	private String InsuranceId;
	@JsonProperty("LoginType")
	private String loginType;
	@JsonProperty("Password")
	private String Password;
	@JsonProperty("RegionCode")
	private String regionCode;
	@JsonProperty("UserId")
	private String userId;
	@JsonProperty("WhatsappYN")
	private String whatsappYN;
	
}
