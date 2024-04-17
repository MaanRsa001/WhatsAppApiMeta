package com.maan.whatsapp.entity.master;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
@Embeddable
public class WhatsappTemplateMasterPK implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	@Column(name = "STAGE_CODE")
	private Long stagecode;

	@Column(name = "STAGESUB_CODE")
	private Long stagesubcode;

	@Column(name = "PRODUCT_ID")
	private Long productid;

	@Column(name = "AGENCY_CODE")
	private String agencycode;

}
