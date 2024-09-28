package com.maan.whatsapp.entity.whatsapp;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "WHATSAPP_CONTACT_DATA")
@DynamicInsert
@DynamicUpdate
public class WhatsappContactData {

	@Id
	private Long whatsappid;

	private Date session_start_time;

	private Date session_end_time;

	private String wa_messageid;

	private Date entry_date;

	private String status;

	private String remarks;

	private String sendername;
	
	@Column(name = "LANGUAGE")
	private String language;	


}
