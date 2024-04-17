package com.maan.whatsapp.controller.wati;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.whatsapp.service.wati.WatiService;

@RestController
@RequestMapping("/wati")
public class WatiController {

	@Autowired
	private WatiService watiSer;

	@GetMapping("/update/MsgStatus")
	public String updateMsgStatus() {
		String response = watiSer.updateMsgStatus();
		return response;
	}

	@GetMapping("/send/sessionMsg")
	public String sendSessionMsg() {
		String response = watiSer.sendSessionMsg();
		return response;
	}

	@GetMapping("/send/sessionMsg/{whatsappid}")
	public String sendSessionMsg(@PathVariable("whatsappid") Long waid) {
		String response = watiSer.sendSessionMsg(waid);
		return response;
	}

	@GetMapping("/store/waFile")
	public String storeWAFile(@RequestParam("wafile") String wafile) {
		String response = watiSer.storeWAFile(wafile);
		return response;
	}

}
