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
	
	private String messageId;

	private String messageType;
	
	private String button_1;
	
	private String button_2;
	
	private String button_3;
	
	private String flow_button_name;
	
	private String cta_button_name;
	
	private String flowId;
	
	private String flowToken;
	
	private String flowApi;
	
	private String flowApiAuth;
	
	private String flowApiMethod;
	
	private String location_button_name;
	
	private String flow_requestdata_yn;
	
	private String menu_button_name;
	
	private String parentMsgId;
	
	private String userReply;
	
	private String isjobyn;

	private String apiData;
	
	private String interactiveYn;
	
	private String flow_index_screen_name;
	
	private String flow_api_request;
	
	private String isCtaDynamicYn;
	
	private String ctaButtonUrl;
	
	private String ctaButtonKeys;
	
	
}
