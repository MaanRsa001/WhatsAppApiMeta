package com.maan.whatsapp.request.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WhatsAppReq {

	@JsonProperty("WhatsAppCode")
	private String whatsappCode;

	@JsonProperty("WhatsAppNo")
	private String whatsappno;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("ProductId")
	private String productid;

	@JsonProperty("AgencyCode")
	private String agencycode;

	@JsonProperty("CurrentStage")
	private String currentStage;

	@JsonProperty("SubStage")
	private String subStage;

	@JsonProperty("StageOrder")
	private String stageOrder;

	@JsonProperty("Type")
	private String type;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("FileYN")
	private String fileYN;

	@JsonProperty("FilePath")
	private String filePath;

	@JsonProperty("IsJobYN")
	private String isJobYN;

	@JsonProperty("IsReplyYN")
	private String isreplyyn;

	@JsonProperty("IsApiCall")
	private String isapicall;

	@JsonProperty("RequestKey")
	private String requestkey;

	@JsonProperty("IsSkipYN")
	private String isskipyn;

	@JsonProperty("IsDocUplYN")
	private String isdocuplyn;

	@JsonProperty("IsValidationApi")
	private String isValidationApi;

	@JsonProperty("ParentMsgID")
	private String parentMsgId;

	@JsonProperty("UserReply")
	private String userReply;
	
	@JsonProperty("StageDesc")
	private String stageDesc;
	
	@JsonProperty("IsResponseYn")
	private String isResponseYn;
	
	@JsonProperty("IsResponseYnSent")
	private String isResponseYnSent;
	
	@JsonProperty("IsButtonMsgYn")
	private String isButtonMsgYn;
	
	@JsonProperty("IsResSaveApi")
	private String isResSaveApi;
	
	@JsonProperty("IsResMsg")
	private String isResMsg;
	
	@JsonProperty("IsResMsgApi")
	private String isResMsgApi;
	
	@JsonProperty("IsTemplateMsg")
	private String isTemplateMsg;
	
	@JsonProperty("FormPageYn")
	private String formPageYn;
	
	@JsonProperty("FormPageUrl")
	private String formPageUrl;
	
}
