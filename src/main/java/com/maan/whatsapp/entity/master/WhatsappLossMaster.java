package com.maan.whatsapp.entity.master;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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
