package com.maan.whatsapp.response.wati.sendsesmsg;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessageResponse {
	
	private String messaging_product;
	
	private List<MetaSendMsgContacts> contacts;
	
	private List<MetaSendMsgRes> messages;
}
