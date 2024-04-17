package com.maan.whatsapp.response.error;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {

	@JsonProperty("TimeStamp")
	private Date timestamp = new Date();

	@JsonProperty("Field")
	private String field;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("Code")
	private String code;

	public Error(String message, String field, String code) {
		super();

		this.message = message;
		this.field = field;
		this.code = code;
	}

}
