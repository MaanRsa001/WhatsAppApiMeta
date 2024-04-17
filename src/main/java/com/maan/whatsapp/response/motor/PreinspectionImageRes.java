package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreinspectionImageRes {
	
	@JsonProperty("ImageName")
	private String imageName;
	
	@JsonProperty("ImagePath")
	private String imagePath;
	
	@JsonProperty("OriginalFileName")
	private String originalFileName;

}
