package com.maan.whatsapp.entity.whatsapptemplate;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = "WHATSAPP_MESSAGE_MASTER")
@DynamicInsert
@DynamicUpdate
public class WhatsappMessageMaster {
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
	
}
