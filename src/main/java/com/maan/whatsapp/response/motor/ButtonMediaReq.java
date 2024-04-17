package com.maan.whatsapp.response.motor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ButtonMediaReq {

	private String url;
	private String fileName;
}
