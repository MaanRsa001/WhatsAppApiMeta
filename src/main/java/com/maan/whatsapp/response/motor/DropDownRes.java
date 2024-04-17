package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DropDownRes {
	
	@JsonProperty("Code")
	private String code;
	@JsonProperty("Description_en")
	private String description_en;
	@JsonProperty("Description_Ar")
	private String description_ar;
	@JsonProperty("Param1")
	private String param1;
	@JsonProperty("Coreappcode")
	private String coreappcode;
	
}
