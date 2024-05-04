package com.maan.whatsapp.meta;

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
public class Messages {

	private ContextMeta  context;
	
	private String from;
	
	private String id;
	
	private String timestamp;
	
	private String type;
	
	private TextMeta text;
	
	private Interactive interactive;
	
	private Location location;
	
	private Image image;
	
	
	
}

