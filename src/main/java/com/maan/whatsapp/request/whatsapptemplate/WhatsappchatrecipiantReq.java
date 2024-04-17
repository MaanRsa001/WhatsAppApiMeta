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
public class WhatsappchatrecipiantReq {
	@JsonProperty("Messageidtype")
	private String messageidtype;
	@JsonProperty("Messageid")
	private String messageid;
	@JsonProperty("Description")
	private String description;
	@JsonProperty("Parentmessageid")
	private String parentmessageid;
	@JsonProperty("Useroptted_messageid")
	private String useroptted_messageid;
	@JsonProperty("Validationapi")
	private String validationapi;
	@JsonProperty("Apiusername")
	private String apiusername;
	@JsonProperty("Apipassword")
	private String apipassword;
	@JsonProperty("Requeststring")
	private String requeststring;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("Effectivedate")
	private String effectivedate;
	@JsonProperty("Entrydate")
	private String entrydate;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("Isjobyn")
	private String isjobyn;
	@JsonProperty("Isinputyn")
	private String isinputyn;
	@JsonProperty("Inputkey")
	private String inputkey;
	@JsonProperty("Inputvalue")
	private String inputvalue;
	@JsonProperty("BackId")
	private String backid;
	@JsonProperty("IscommonYN")
	private String iscommonyn;
	@JsonProperty("Commonid")
	private String commonid;
	@JsonProperty("Errors")
	private List<Error> errors;
}
