package com.maan.whatsapp.entity.master;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "WHATSAPP_MESSAGE_MASTER")
@DynamicInsert
@DynamicUpdate
public class WAMessageMaster {

	@Id
	private String messageid;

	private String messagedescen;

	private String messagedescar;

	private String status;

	private Date effectivedate;

	private Date entrydate;

	private String remarks;

	private String iscommonmsg;

	private String commonmsgid;
	
	@Column(name = "CLAIM_MESSAGE")
	private String claimMessage;
	
	@Column(name = "CLAIM_MESSAGE_YN")
	private String claimMessageYn;

	@Column(name = "MSG_TYPE")
	private String msgType;

	@Column(name = "IMAGE_URL")
	private String imageUrl;

	@Column(name = "IMAGE_NAME")
	private String imageName;

	@Column(name = "MSG_FOOTER")
	private String msgFooter;

	@Column(name = "MSG_HEADER")
	private String msgHeader;

	@Column(name = "MSG_BUTTON1")
	private String msgButton1;
	
	@Column(name = "MSG_BUTTON2")
	private String msgButton2;
	
	@Column(name = "MSG_BUTTON3")
	private String msgButton3;
	
	@Column(name = "IS_BUTTONMSG")
	private String isButtonMsg;
	
	@Column(name = "MSG_BUTTON_SW1")
	private String msgButtonSW1;
	
	@Column(name = "MSG_BUTTON_SW2")
	private String msgButtonSw2;
	
	@Column(name = "MSG_BUTTON_SW3")
	private String msgButtonSw3;
	


}
