package com.maan.whatsapp.entity.master;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(WhatsappMessageMenuMasterID.class)
@Entity
@Table(name = "whatsapp_message_menu_master")
public class WhatsappMessageMenuMaster {

	@Id
	@Column(name = "MESSAGE_ID")
	private String messageId;
	
	@Id
	@Column(name = "OPTION_ID")
	private Integer optionId;
	
	@Column(name = "OPTION_NO")
	private Integer optionNo;
	
	@Column(name = "OPTION_TITLE")
	private String optionTitle;
	
	@Column(name = "OPTION_DESC")
	private String optionDesc;
	
	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	
}
