package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocInsertReq {

	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("Base64File")
	private String base64File;

	@JsonProperty("FilePath")
	private String filePath;

}
