package com.maan.whatsapp.response.wati.sendsesfile;

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
public class MessageFileRes {

	private String whatsappMessageId;

	private String localMessageId;

	private String text;

	private MediaRes media;

	private String messageContact;

	private String location;

	private String type;

	private String time;

	private String status;

	private String statusString;

	private String isOwner;

	private String isUnread;

	private String ticketId;

	private String avatarUrl;

	private String assignedId;

	private String operatorName;

	private String replyContextId;

	private String sourceType;

	private String failedDetail;

	private String id;

	private String created;

	private String conversationId;

}
