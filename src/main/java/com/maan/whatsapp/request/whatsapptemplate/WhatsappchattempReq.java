package com.maan.whatsapp.request.whatsapptemplate;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.whatsapp.response.error.Error;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsappchattempReq {
	@JsonProperty("Messageidtype")
	private String messageidtype;
	@JsonProperty("Messageid")
	private String messageid;
	@JsonProperty("Messagedescen")
	private String messagedescen;
	@JsonProperty("Messagedescar")
	private String messagedescar;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("Effectivedate")
	private String effectivedate;
	@JsonProperty("Entrydate")
	private String entrydate;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("IscommonYN")
	private String iscommonyn;
	@JsonProperty("Commonid")
	private String commonid;
	@JsonProperty("IsButtonMsgYn")
	private String isButtonMsgYn;
	@JsonProperty("HeaderType")
	private String headerType;
	@JsonProperty("HeaderImageUrl")
	private String headerImageUrl;
	@JsonProperty("HeaderImageName")
	private String headerImageName;
	@JsonProperty("Button1")
	private String button1;
	@JsonProperty("Button2")
	private String button2;
	@JsonProperty("Button3")
	private String button3;
	@JsonProperty("Errors")
	private List<Error> errors;
}
