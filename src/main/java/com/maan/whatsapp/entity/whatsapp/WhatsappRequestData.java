package com.maan.whatsapp.entity.whatsapp;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "WHATSAPP_REQUEST_DATA")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class WhatsappRequestData {

	@EmbeddedId
	private WhatsappRequestDataPK reqDataPk;

	private String issessionactive;

	private String isprocesscompleted;

	private Date entry_date;

	private Date lastupdated_time;

	private Date request_time;

	private Date response_time;

	private String status;

	private String remarks;

	private String wa_response;

}
