package com.maan.whatsapp.entity.master;

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
public class WAChatRecipientMasterPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String messageid;

	private String parentmessageid;

}
