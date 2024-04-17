package com.maan.whatsapp.request.wati.getcont;

import java.util.List;

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

	private String created;
	
	private String whatsappMessageId;
	
	private String templateId;
	
	private String templateName;

	private String conversationId;

	private String ticketId;

	private String text;

	private String type;

	private String data;
	
	private String sourceId;
	
	private String sourceUrl;
	
	private String timestamp;

	private String owner;

	private String eventType;

	private String statusString;

	private String avatarUrl;

	private String assignedId;

	private String operatorName;

	private String operatorEmail;

	private String waId;

	private List<MessageContact> messageContact;

	private String senderName;
	
	private String listReply;
	
	private InteractiveButtonReply interactiveButtonReply;
	
	private String buttonReply;
	
	private String replyContextId;
	
	private String sourceType;

}



