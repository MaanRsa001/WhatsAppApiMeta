package com.maan.whatsapp.config.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.maan.whatsapp.response.error.Error;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(HttpStatus.FORBIDDEN)
public class WhatsAppValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	private List<Error> errors;

	private String requestReferenceNo;

	private String quoteno;

	private Object response;

	public WhatsAppValidationException(List<Error> errors) {
		this.errors = errors;
	}

	public WhatsAppValidationException(List<Error> errors, String requestReferenceNo, String quoteno) {
		this.errors = errors;
		this.requestReferenceNo = requestReferenceNo;
		this.quoteno = quoteno;
	}

	public WhatsAppValidationException(List<Error> errors, Object response) {
		this.errors = errors;
		this.response = response;
	}

}
