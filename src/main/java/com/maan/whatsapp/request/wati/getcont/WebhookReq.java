package com.maan.whatsapp.request.wati.getcont;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookReq {

	private String id;
	
	private String whatsappMessageId;
	
	private String templateId;
	
	private String templateName;

	private String conversationId;

	private String text;

	private String type;

	private String data;
	
	private String timestamp;

	private String waId;

	private String senderName;
	
	private String buttonReply;
	
	private String phoneNumberId;
	
	private String mimeType;
	
	private String imageId;
	
	private String interactiveType;
	
	private String buttonTitle;
	
	private String displayMobileNo;
}



