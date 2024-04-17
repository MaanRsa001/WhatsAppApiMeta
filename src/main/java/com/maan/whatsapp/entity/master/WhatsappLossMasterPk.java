package com.maan.whatsapp.entity.master;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class WhatsappLossMasterPk implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "LOSS_ID")
	private Long lossId;
	@Column(name = "DOC_ID")
	private String docId;

}
