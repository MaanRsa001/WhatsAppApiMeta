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
public class WAWatiReq {

	private String msg;

	private String issent;

	private String wamsgId;

	private String waresponse;

	private String filepath;

	private String wafilepath;

	private String waid;

	private String sessionid;
	
	private String isButtonMsg;
	
	private String msgType;
	
	private String imageUrl;
	
	private String imageName;
	
	private String msgFooter;
	
	private String msgHeader;
	
	private String button1;
	
	private String button2;
	
	private String button3; 

	@JsonProperty("ParentMsgID")
	private String parentMsgId;

	@JsonProperty("MessageID")
	private String messageId;

	@JsonProperty("UserReply")
	private String userReply;

	@JsonProperty("IsJobYn")
	private String isjobyn;
	
	private String isTemplateMsg;
	


}
