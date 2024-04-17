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
public class WhatsapptemplateReq {
	
	@JsonProperty("Agencycode")
	private String agencycode;
	@JsonProperty("Stagecode")
	private String stagecode;
	@JsonProperty("Stagesubcode")
	private String stagesubcode;
	@JsonProperty("Productid")
	private String productid;
	@JsonProperty("Stagedesc")
	private String stagedesc;
	@JsonProperty("Stagesubdesc")
	private String stagesubdesc;
	@JsonProperty("Messagecontenten")
	private String messagecontenten;
	@JsonProperty("Messagecontentar")
	private String messagecontentar;
	@JsonProperty("Messageregardsen")
	private String messageregardsen;
	@JsonProperty("Messageregardsar")
	private String messageregardsar;
	@JsonProperty("Fileyn")
	private String fileyn;
	@JsonProperty("Entrydate")
	private String entrydate;
	@JsonProperty("Status")
	private String status;
	@JsonProperty("Remarks")
	private String remarks;
	@JsonProperty("Filepath")
	private String filepath;
	@JsonProperty("Stageorder")
	private String stageorder;
	@JsonProperty("Ischatyn")
	private String ischatyn;
	@JsonProperty("Isreplyyn")
	private String isreplyyn;
	@JsonProperty("Isapicall")
	private String isapicall;
	@JsonProperty("Apiurl")
	private String apiurl;
	@JsonProperty("Apiauth")
	private String apiauth;
	@JsonProperty("Apimethod")
	private String apimethod;
	@JsonProperty("Responsestring")
	private String responsestring;
	@JsonProperty("Errorrespstring")
	private String errorrespstring;
	@JsonProperty("Requestkey")
	private String requestkey;
	@JsonProperty("Isskipyn")
	private String isskipyn;
	@JsonProperty("Docuploadyn")
	private String docuploadyn;
	@JsonProperty("Requeststring")
	private String requeststring;
	@JsonProperty("Responsestringar")
	private String responsestringar;
	@JsonProperty("Apivalidationyn")
	private String apivalidationyn;
	@JsonProperty("Errors")
	private List<Error> errors;
	
	@JsonProperty("IsApiResYn")
	private String isApiResYn;
	@JsonProperty("IsButtonMsgYn")
	private String isButtonMsgYn;
	@JsonProperty("HeaderType")
	private String headerType;
	@JsonProperty("HeaderText")
	private String headerText;
	@JsonProperty("HeaderImageUrl")
	private String headerImageUrl;
	@JsonProperty("HeaderImageName")
	private String headerImageName;
	@JsonProperty("MsgBody")
	private String msgBody;
	@JsonProperty("MsgFooter")
	private String msgFooter;
	@JsonProperty("Button1")
	private String button1;
	@JsonProperty("Button2")
	private String button2;
	@JsonProperty("Button3")
	private String button3;
	
}
