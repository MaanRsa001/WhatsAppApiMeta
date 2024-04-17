package com.maan.whatsapp.request.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class WASaveReq {

	@JsonProperty("Type")
	private String type;

	@JsonProperty("WhatsAppCode")
	private String whatsAppCode;

	@JsonProperty("WhatsAppNo")
	private String whatsAppno;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("ProductId")
	private String productid;

}
