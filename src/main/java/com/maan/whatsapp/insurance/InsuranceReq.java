package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsuranceReq {
	
	@JsonProperty("RegistrationNo")
	private String regisrationNo;
	
	@JsonProperty("IdType")
	private String idType;
	
	@JsonProperty("IdNumber")
	private String IdNumber;
	
	@JsonProperty("SumInsured")
	private String sumInsured;
	
	@JsonProperty("CustomerName")
	private String CustomerName;

	@JsonProperty("Type")
	private String type;

	@JsonProperty("mobileNo")
	private String mobileNo;
	
	
	@JsonProperty("TypeofInsurance")
	private String typeofInsurance;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("MotorUsageId")
	private String motorUsageId;
	
	@JsonProperty("ClaimType")
	private String ClaimType;
	
	@JsonProperty("WhatsAppCode")
	private String whatsAppCode;
	
	@JsonProperty("WhatsAppNo")
	private String whatsAppNo;
	
	@JsonProperty("AirtelPayNo")
	private String airtelPayNo;


}
