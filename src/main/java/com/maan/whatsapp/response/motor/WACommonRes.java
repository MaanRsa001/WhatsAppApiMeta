package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WACommonRes {
	
	@JsonProperty("Message")
	private String message;
	
	@JsonProperty("Response")
	private Object response;

}
