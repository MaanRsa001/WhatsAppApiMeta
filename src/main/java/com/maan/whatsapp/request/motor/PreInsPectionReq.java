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
public class PreInsPectionReq {
		
	@JsonProperty("ChassisNo")
    private String chassisNo;
	@JsonProperty("RegistrationNo")
    private String registrationNo;
	@JsonProperty("MobileNo")
    private String mobileNo;
	@JsonProperty("Image1")
    private String image1;
	@JsonProperty("Image2")
    private String image2;
	@JsonProperty("Image3")
    private String image3;
	@JsonProperty("Image4")
    private String image4;
	@JsonProperty("Image5")
    private String image5;
	@JsonProperty("Image6")
    private String image6;
	@JsonProperty("Image8")
    private String image8;
	@JsonProperty("Image9")
    private String image9;
	@JsonProperty("Image10")
    private String image10;
	
}
