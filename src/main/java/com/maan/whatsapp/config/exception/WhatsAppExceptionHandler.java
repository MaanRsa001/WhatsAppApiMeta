package com.maan.whatsapp.config.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class WhatsAppExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(WhatsAppValidationException.class)
	protected ResponseEntity<Object> handleUserNotFoundException(WhatsAppValidationException ex,
			HttpServletResponse response) {

		Map<String, Object> hresponse = new HashMap<String, Object>();

		hresponse.put("RequestReferenceNo", ex.getRequestReferenceNo());
		hresponse.put("Errors", ex.getErrors());
		hresponse.put("QuoteNo", ex.getQuoteno());
		hresponse.put("Response", ex.getResponse());

		return new ResponseEntity<Object>(hresponse, HttpStatus.FORBIDDEN);
	}

	

}
