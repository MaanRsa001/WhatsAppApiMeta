package com.maan.whatsapp.request.whatsapp;

import com.maan.whatsapp.response.motor.ButtonMediaReq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ButtonHeaderReq {

	private String type;
	private String text;
	private ButtonMediaReq media;
	
	
	
}
