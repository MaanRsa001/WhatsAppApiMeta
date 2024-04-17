package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentJsonFormatReq {
	
	@JsonProperty("LossId")
	private String lossId;
	@JsonProperty("PartyNo")
	private String partyNo;
	@JsonProperty("ClaimNo")
	private String claimNo;
	@JsonProperty("DocTypeId")
	private String doctTypeId;
	@JsonProperty("Description")
	private String description;
	@JsonProperty("FileName")
	private String fileName;
	@JsonProperty("file")
	private String file;
	@JsonProperty("InsuranceId")
	private String InsuranceId;
	

}
