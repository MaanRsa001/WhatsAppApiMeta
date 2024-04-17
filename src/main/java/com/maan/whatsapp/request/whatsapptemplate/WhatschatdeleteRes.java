package com.maan.whatsapp.request.whatsapptemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatschatdeleteRes {
	
	@JsonProperty("Messageid")
	private String messageid;
	@JsonProperty("Description")
	private String description;
	@JsonProperty("Optioncount")
	private String optioncount;
}
