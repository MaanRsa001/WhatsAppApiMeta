package com.maan.whatsapp.entity.master;

import java.io.Serializable;

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
public class WhatsappMessageMenuMasterID implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String messageId;
	
	private Integer optionId;

}
