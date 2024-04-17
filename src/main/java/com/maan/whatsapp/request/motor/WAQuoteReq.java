package com.maan.whatsapp.request.motor;

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
public class WAQuoteReq {

	@JsonProperty("ChassisNo")
	private String chassisno;

	@JsonProperty("MobileNumber")
	private String mobilenumber;

	@JsonProperty("MobileCode")
	private String mobileCode;

	@JsonProperty("WhatsAppNo")
	private String whatsAppNo;

	@JsonProperty("WhatsAppCode")
	private String whatsAppCode;

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("ProductId")
	private String product_id;

	@JsonProperty("Type")
	private String type;

	@JsonProperty("PlateCharNo")
	private String plateCharNo;

	@JsonProperty("Plate_Char")
	private String plate_char;

	@JsonProperty("Plate_Number")
	private String plate_number;

	@JsonProperty("EntryDate")
	private String entryDate;

	@JsonProperty("WhatsAppId")
	private String whatsappid;

}
