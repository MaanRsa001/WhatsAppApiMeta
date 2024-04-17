package com.maan.whatsapp.response.wati.sendsesfile;

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
public class SendSessionFile {

	private String ok;

	private String result;

	private String ticketStatus;

	private Object message;

	private String info;

}
