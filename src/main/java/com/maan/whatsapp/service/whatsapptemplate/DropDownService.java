package com.maan.whatsapp.service.whatsapptemplate;

import java.util.List;

import com.maan.whatsapp.response.motor.DropDownRes;

public interface DropDownService {

	List<DropDownRes> getrequestkeylist();
	
	List<DropDownRes> getchatmsgidlist();
	
	List<DropDownRes> getuserlist();
	
	List<DropDownRes> getproductlist();







}
