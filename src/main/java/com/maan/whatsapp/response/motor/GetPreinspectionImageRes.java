package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPreinspectionImageRes {
	
	@JsonProperty("TotalCount")
	private String totalCount;
	@JsonProperty("EntryDate")
	private String entryDate;

}
