package com.maan.whatsapp.entity.whatsapp;

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
@Table(name = "WHATSAPP_REQUEST_DETAIL")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class WhatsappRequestDetail {

	@EmbeddedId
	private WhatsappRequestDetailPK reqDetPk;

	private String message;

	private String file_path;

	private String isread;

	private String issent;

	private Date entry_date;

	private Date request_time;

	private Date response_time;

	private String status;

	private String remarks;

	private String file_yn;

	private Long stage_order;

	private String wa_response;

	private String wa_messageid;

	private String wa_filepath;

	private Long whatsappid;

	private String sessionid;

	private String userreply;

	private String wausermessageid;

	private String isjobyn;

	private String isreplyyn;

	private String isapicall;

	private String isprocesscompleted;

	@Column(name = "REQUEST_KEY")
	private String requestkey;

	private String isskipyn;

	private String isdocuplyn;

	private String wa_userfilepath;

	private String locwa_userfilepath;

	private String isskipped;

	private String isvalidationapi;

	private String isvalid;

	private String validationmessage;

	@Column(name = "REQUEST_STRING")
	private String requeststring;
	
	@Column(name = "STAGE_DESC")
	private String stageDesc;
	
	@Column(name = "ISRES_YN")
	private String isReponseYn;
	
	@Column(name = "ISRES_YN_SENT")
	private String isResponseYnSent;
	
	@Column(name = "IS_BUTTON_MSG")
	private String isButtonMsg;
	
	@Column(name = "SAVE_API_RES")
	private String saveApiRes;
	
	@Column(name = "API_MESSAGE_TEXT")
	private String apiMessageText;
	
	@Column(name = "ISRES_SAVEAPI")
	private String isResSaveApi;
	
	@Column(name = "ISRES_MSG")
	private String isResMsg;
	
	@Column(name = "ISRES_MSG_API")
	private String isResMsgApi;
	
	@Column(name = "ISTEMPLATE_MSG")
	private String isTemplateMsg;
	
	@Column(name = "FORMPAGE_YN")
	private String formpageYn;
	
	@Column(name = "FORMPAG_URL")
	private String formpageUrl;
	

}
