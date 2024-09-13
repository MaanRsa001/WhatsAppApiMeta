package com.maan.whatsapp.entity.master;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Embeddable
public class WhatsappClaimDocumentSetupPk implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	@Column(name = "MOBILE_NO")
	private Long mobNo;
	@Column(name = "TRAN_ID")
	private Long  tranId;
	@Column(name = "DOC_ID")
	private Long docId;
	
}
