package com.maan.whatsapp.controller.whatsapptemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.whatsapp.response.motor.DropDownRes;
import com.maan.whatsapp.service.whatsapptemplate.DropDownService;

@RestController
@RequestMapping("/dropdown")
public class DropDownController {
	
	@Autowired
	private DropDownService dropDownService;
	
	
	@GetMapping("/requestkeylist")
	private List<DropDownRes> getrequestkeylist() {
	List<DropDownRes> response = dropDownService.getrequestkeylist();
	return response;
	}

	@GetMapping("/chatmsgidlist")
	private List<DropDownRes> getchatmsgidlist() {
	List<DropDownRes> response = dropDownService.getchatmsgidlist();
	return response;
	}
	
	@GetMapping("/usertypelist")
	private List<DropDownRes> getuserlist() {
	List<DropDownRes> response = dropDownService.getuserlist();
	return response;
	}
	
	@GetMapping("/productlist")
	private List<DropDownRes> getproductlist() {
	List<DropDownRes> response = dropDownService.getproductlist();
	return response;
	}
	
}
