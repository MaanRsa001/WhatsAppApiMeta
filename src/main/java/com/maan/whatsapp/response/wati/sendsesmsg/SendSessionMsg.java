package com.maan.whatsapp.response.wati.sendsesmsg;

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
public class SendSessionMsg {

	private String ok;

	private String result;

	private String ticketStatus;

	private Object message;

	private String info;

}
