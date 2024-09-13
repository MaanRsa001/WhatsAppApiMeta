package com.maan.whatsapp.claimintimation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.activation.FileTypeMap;

@RestController
public class WhatsappLogoController {
	
	
	
	@GetMapping("/whatsapp/logo")
	public ResponseEntity<byte[]> whatsappLogo() throws IOException{
		File file = ResourceUtils.getFile("classpath:images/BimaChamp.jpg");
	    return ResponseEntity.ok()
	            .header("Content-Disposition", "attachment; filename=" +file.getName())
	            .contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file)))
	            .body(Files.readAllBytes(file.toPath()));
	}

}
