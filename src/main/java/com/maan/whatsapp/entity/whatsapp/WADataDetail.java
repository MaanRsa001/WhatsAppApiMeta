package com.maan.whatsapp.entity.whatsapp;

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
@Table(name = "WHATSAPP_DATA_DETAIL")
@DynamicInsert
@DynamicUpdate
public class WADataDetail {

	@EmbeddedId
	private WADataDetailPK waddPk;

	private String parentmessageid;

	private String usermessageid;

	private String userreply;

	private String messagecontent;

	private String wausermessageid;

	private String sessionid;

	private String apivalidationresponse;

	private String status;

	private Date entrydate;

	private String remarks;

	private String isjobyn;

	private String isinput;

	private String input_value;

	private String request_key;

	private String userreply_msg;

}
