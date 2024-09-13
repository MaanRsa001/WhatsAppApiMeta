package com.maan.whatsapp.entity.whatsapptemplate;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
@Table(name = "WHATSAPP_TEMPLATE_MASTER")
@DynamicInsert
@DynamicUpdate
public class Whatsapptemplate {
	@EmbeddedId
	private WhatsapptemplatePK Whatsapptemplatepk;
		private String stage_desc;
	private String stagesub_desc;
	private String message_content_en;
	private String message_content_ar;
	private String message_regards_en;
	private String message_regards_ar;
	private String file_yn;
	private Date entry_date;
	private String status;
	private String remarks;
	private String file_path;
	private Long stage_order;
	private String ischatyn;
	private String isreplyyn;
	private String isapicall;
	private String apiurl;
	private String apiauth;
	private String apimethod;
	private String responsestring;
	private String errorrespstring;
	private String request_key;
	private String request_string;
	private String isskipyn;
	private String isdocuplyn;
	private String isvalidationapi;
	private String responsestring_ar;
	
	@Column(name = "ISRES_YN")
	private String isReponseYn;
	
	@Column(name = "IS_BUTTON_MSG")
	private String isButtonMsg;
	
	@Column(name = "BUTTON1")
	private String button1;
	
	@Column(name = "BUTTON2")
	private String button2;
	
	@Column(name = "BUTTON3")
	private String button3;
	
	@Column(name = "IMAGE_URL")
	private String imageUrl;
	
	@Column(name = "IMAGE_NAME")
	private String imageName;
	
	@Column(name = "MSG_TYPE")
	private String msgType;
	
	@Column(name = "BUTTON_HEADER")
	private String buttonHeader;
	
	@Column(name = "BUTTON_FOOTER")
	private String buttonFooter;
	
	@Column(name = "BUTTON_BODY")
	private String buttonBody;

}
