package com.maan.whatsapp.response.motor;

import java.util.List;

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
public class PreinspectionDetailsRes {
	
	@JsonProperty("MobileNo")
	private String mobileNo;
	
	@JsonProperty("RegistrationNo")
	private String registrationNo;
	
	@JsonProperty("ChassisNo")
	private String chassisNo;
	
	@JsonProperty("TransactionId")
	private String transactionId;
	
	@JsonProperty("CustomerName")
	private String customerName;
	
	@JsonProperty("Image")
	private List<PreinspectionImageRes> image;
}
