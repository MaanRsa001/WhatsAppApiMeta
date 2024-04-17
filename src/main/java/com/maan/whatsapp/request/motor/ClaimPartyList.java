package com.maan.whatsapp.request.motor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClaimPartyList {
	
	@JsonProperty("")
	private List<PartyList> list;

}
