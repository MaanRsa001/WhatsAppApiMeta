package com.maan.whatsapp.meta;

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
public class Value {

	private String  messaging_product ;
	
	private Metadata metadata;
	
	private List<ContactsMeta> contacts;
	
	private List<Messages> messages;
	
	
}
