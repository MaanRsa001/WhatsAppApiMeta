package com.maan.whatsapp.response.wati.getmsg;

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
public class MessageItems {

	private String replySourceMessage;

	private String text;

	private String type;

	private String data;

	private String timestamp;

	private String owner;

	private String statusString;

	private String avatarUrl;

	private String assignedId;

	private String operatorName;

	private String localMessageId;

	private String failedDetail;

	private String contacts;

	private String id;

	private String created;

	private String conversationId;

	private String ticketId;

	private String eventType;

	private String eventDescription;

	private String actor;

	private String assignee;

	private String topicName;

}
