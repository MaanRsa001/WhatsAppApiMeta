package com.maan.whatsapp.request.whatsapp;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhatsAppButtonReq {
	
	private ButtonHeaderReq header;
	private String body ;
	private String footer;
	private List<ButtonsNameReq> buttons; 
   
}
