package com.maan.whatsapp.insurance;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SearchPreInsPectionReq {

	@JsonProperty("SearchType")
	private String searchType;
	
	@JsonProperty("SearchValue")
	private String searchValue;

}
