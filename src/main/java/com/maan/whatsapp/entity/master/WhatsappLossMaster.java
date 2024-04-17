package com.maan.whatsapp.entity.master;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "WHATSAPP_LOSSDOCUMENT_MASTER")
public class WhatsappLossMaster {
	
	@EmbeddedId
	private WhatsappLossMasterPk pk;
	
	@Column(name = "DOC_NAME")
	private String docname;
	@Column(name = "STATUS")
	private String status;

}
