package com.maan.whatsapp.entity.whatsapptemplate;

import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Entity
@Table(name = "WHATSAPP_CHATRECIPIENT_MASTER")
@DynamicInsert
@DynamicUpdate
public class WhatsappChatrecipiantMessageMaster {
	
	@EmbeddedId
	private WhatsappChatrecipiantMessageMasterpk whatschatpk;
	private String description;
	private Long useroptted_messageid;
	private String validationapi;
	private String apiusername;
	private String apipassword;
	private String requeststring;
	private String status;
	private Date effectivedate;
	private Date entrydate;
	private String remarks;
	private String isjobyn;
	private String isinput;
	private String input_value;
	private String request_key;
	private String iscommonmsg;
	private String commonmsgid;
}
