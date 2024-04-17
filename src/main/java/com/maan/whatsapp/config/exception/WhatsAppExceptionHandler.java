package com.maan.whatsapp.config.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.maan.whatsapp.response.error.Error;

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

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		Map<String, Object> body = new HashMap<String, Object>();

		BindingResult br = ex.getBindingResult();
		List<FieldError> list = br.getFieldErrors();

		List<Error> errors = new ArrayList<Error>();

		for (int i = 0; i < list.size(); i++) {
			FieldError objerr = list.get(i);

			errors.add(new Error(objerr.getDefaultMessage(), objerr.getField(), Integer.valueOf(i + 1).toString()));
		}

		body.put("Errors", errors);

		return new ResponseEntity<>(body, headers, HttpStatus.FORBIDDEN);
	}

}
