package com.maan.whatsapp.request.wati.getcont;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageContact {
	
	private String addresses;
	
	private String emails;
	
	private Name name;
	
	private List<Phones> phones;
	

}

