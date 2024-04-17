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
public class MediaRes {

	private String id;

	private String mimeType;

	private String caption;

}
