package com.maan.whatsapp.entity.whatsapptemplate;

import java.io.Serializable;

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
public class WhatsapptemplatePK implements Serializable {
	private String agency_code;
	private Long stage_code;
	private Long stagesub_code;
	private Long product_id;

}
