package com.maan.whatsapp.response.wati.getmsg;

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
public class GetMessageRes {

	private String result;

	private String info;

	private MessageRes messages;

	private LinkRes link;

}
