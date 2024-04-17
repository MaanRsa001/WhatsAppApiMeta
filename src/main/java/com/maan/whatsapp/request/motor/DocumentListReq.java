package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentListReq {
	

	@JsonProperty("DocNo")
	private String docNo;
	@JsonProperty("DocumentName")
	private String documentName;
	@JsonProperty("Claimrefno")
	private String claimrefno;
	@JsonProperty("LossTypeid")
	private String losstypeid;
	@JsonProperty("Partyno")
	private String partyno;
	@JsonProperty("Response")
	private String response;


}
