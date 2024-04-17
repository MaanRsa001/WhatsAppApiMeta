package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WhFileUploadReq {

	@JsonProperty("TranId")
	private String tranId;
	@JsonProperty("FileName")
	private String fileName;
	@JsonProperty("Extension")
	private String extension;
	@JsonProperty("Base64Image")
	private String base64Image;
	@JsonProperty("OriginalFileName")
	private String originalFileName;
	
}
