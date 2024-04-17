package com.maan.whatsapp.entity.master;

import java.util.Date;

import javax.persistence.Column;
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

@Entity
@Table(name = "WHATSAPP_TEMPLATE_MASTER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class WhatsappTemplateMaster {

	@EmbeddedId
	private WhatsappTemplateMasterPK tempMasterPk;

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

	@Column(name = "REQUEST_KEY")
	private String requestkey;

	@Column(name = "REQUEST_STRING")
	private String requeststring;

	private String response_keys;

	private String isskipyn;

	private String isdocuplyn;

	private String isvalidationapi;
	
	@Column(name = "RESPONSESTRING_AR")
	private String responseStringAr;
	
	@Column(name = "ERRORRESSTRING_TZS")
	private String errorResponseStrTzs;

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
	
	@Column(name = "ISRES_SAVEAPI")
	private String isResSaveApi;
	
	@Column(name = "ISRES_MSG")
	private String isResMsg;
	
	@Column(name = "ISRES_MSG_API")
	private String isResMsgApi;

	@Column(name = "ISTEMPLATE_MSG")
	private String isTemplateMsg;
	
	@Column(name = "TEMPLATE_NAME")
	private String templateName;
	
	@Column(name = "BUTTON_SW1")
	private String buttonSw1;
	
	@Column(name = "BUTTON_SW2")
	private String buttonSw2;
	
	@Column(name = "BUTTON_SW3")
	private String buttonSw3;
	
	@Column(name = "FORMPAGE_YN")
	private String formpageYn;
	
	@Column(name = "FORMPAG_URL")
	private String formpageUrl;
	
}
