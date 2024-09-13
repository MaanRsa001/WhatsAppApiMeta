package com.maan.whatsapp.entity.whatsapp;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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
public class WhatsappRequestDetailPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long whatsappcode;

	private Long mobileno;

	private Long quoteno;

	@Column(name = "CURRENT_STAGE")
	private Long currentstage;

	@Column(name = "CURRENT_SUB_STAGE")
	private Long currentsubstage;

	@Column(name = "PRODUCT_ID")
	private Long productid;

}
