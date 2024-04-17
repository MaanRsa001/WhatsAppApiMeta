package com.maan.whatsapp.request.wati.getcont;

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
public class ContactsParamReq {

	private String name;

	private String operator;

	private String value;

}
