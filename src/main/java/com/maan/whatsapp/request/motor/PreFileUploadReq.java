package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PreFileUploadReq {
	
	@JsonProperty("OriginalFileName")
	private String originalFileName;
	@JsonProperty("FileName")
	private String fileName;
	@JsonProperty("TranId")
	private String tranId;
	@JsonProperty("Base64")
	private String base64;
}
