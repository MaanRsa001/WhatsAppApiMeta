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
	
	@Column(name = "INTERACTIVE_BUTTON_YN")
	private String interactiveButtonYn;
	
	@Column(name = "MESSAGE_TYPE")
	private String messageType;
	
	@Column(name = "MENU_BUTTON_NAME")
	private String menu_button_name;
	
	@Column(name = "MENU_BUTTON_NAME_SW")
	private String menu_button_name_sw;

	@Column(name = "BUTTON_1")
	private String button_1;

	@Column(name = "BUTTON_2")
	private String button_2;

	@Column(name = "BUTTON_3")
	private String button_3;

	@Column(name = "FLOW_ID")
	private String flowId;

	@Column(name = "FLOW_TOKEN")
	private String flowToken;

	@Column(name = "REQUESTDATA_YN")
	private String requestdataYn;
	
	@Column(name = "FLOW_API")
	private String flowApi;
	
	@Column(name = "FLOW_API_AUTH")
	private String flowApiAuth;
	
	@Column(name = "FLOW_API_METHOD")
	private String flowApiMethod;
	
	@Column(name = "FLOW_BUTTON_NAME")
	private String flowButtonName;
	
	@Column(name = "CTA_BUTTON_NAME")
	private String ctaButtonName;
	
	@Column(name = "LOC_BUTTON_NAME")
	private String locButtonName;
	
	@Column(name = "FLOW_BUTTON_NAME_SW")
	private String flowButtonNameSw;
	
	@Column(name = "CTA_BUTTON_NAME_SW")
	private String ctaButtonNameSw;
	
	@Column(name = "LOC_BUTTON_NAME_SW")
	private String locButtonNameSw;
	
	@Column(name = "BUTTON_1_SW")
	private String button_1_sw;

	@Column(name = "BUTTON_2_SW")
	private String button_2_sw;

	@Column(name = "BUTTON_3_SW")
	private String button_3_sw;
	
	@Column(name = "FORMPAGE_YN")
	private String formpageYn;
	
	@Column(name = "FORMPAG_URL")
	private String formpageUrl;
	
	@Column(name = "FLOW_INDEX_SCREEN_NAME")
	private String flow_index_screen_name;
	
	@Column(name = "FLOW_API_REQUEST")
	private String flowApiRequest;
	
	@Column(name = "ISCTA_DYNAMICYN")
	private String isCtaDynamicYn;
	
	@Column(name = "CTA_BUTTONURL")
	private String ctaButtonUrl;
	
	@Column(name = "CTA_BUTTON_KEYS")
	private String ctaButtonKeys;
	
	
	////////////////////////////////
	
	@Column(name = "ISRES_SAVEAPI")
	private String isResSaveApi;
	
	@Column(name = "ISRES_MSG")
	private String isResMsg;
	
	@Column(name = "ISRES_MSG_API")
	private String isResMsgApi;

	
}
