package com.maan.whatsapp.claimintimation;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PreInspectionReq {

	@JsonProperty("RegistrationNo")
	private String registraionNo;
	
	@JsonProperty("ChassisNo")
	private String ChassisNo;
	
	@JsonProperty("mobileNo")
	private String mobileNo;
	
	@JsonProperty("Image1")
	private String image1;
	
	@JsonProperty("Image2")
	private String image2;
	
	@JsonProperty("Image3")
	private String Image3;
	
	@JsonProperty("Image4")
	private String Image4;
	
	@JsonProperty("Image5")
	private String image5;
	
	@JsonProperty("Image6")
	private String image6;
	
	@JsonProperty("Image7")
	private String image7;
	
	@JsonProperty("ImageName1")
	private String imageName1;
	
	@JsonProperty("ImageName2")
	private String imageName2;
	
	@JsonProperty("ImageName3")
	private String imageName3;
	
	@JsonProperty("ImageName4")
	private String imageName4;
	
	@JsonProperty("ImageName5")
	private String imageName5;
	
	@JsonProperty("ImageName6")
	private String imageName6;
	
	@JsonProperty("ImageName7")
	private String imageName7;
}
