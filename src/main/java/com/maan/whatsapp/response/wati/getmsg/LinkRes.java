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
public class LinkRes {

	private String prevPage;

	private String nextPage;

}
