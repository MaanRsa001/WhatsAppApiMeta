package com.maan.whatsapp.controller.motor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.whatsapp.request.motor.DocInsertReq;
import com.maan.whatsapp.service.motor.MotorService;

@RestController
@RequestMapping("/api")
public class MotorController {

	@Autowired
	private MotorService motSer;

	@PostMapping("/doc/save")
	public DocInsertReq getFilePath(@RequestBody DocInsertReq request) {
		DocInsertReq response = motSer.getFilePath(request);
		return response;
	}

}
