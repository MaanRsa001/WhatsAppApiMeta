package com.maan.whatsapp.claimintimation;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimIntimationEntityId implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -3676098035999832585L;
	
	private String mobileNo;
	
	private Long serialNo;
	
	private String botOptionNo;

}
