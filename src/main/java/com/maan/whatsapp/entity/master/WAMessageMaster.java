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
	
	@Column(name = "FLOW_INDEX_SCREEN_NAME")
	private String flow_index_screen_name;
	
	@Column(name = "FLOW_API_REQUEST")
	private String flowApiRequest;
	
}
