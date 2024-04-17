package com.maan.whatsapp.service.whatsapptemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.whatsapp.repository.whatsapptemplate.WhatschattemplateRepository;
import com.maan.whatsapp.response.motor.DropDownRes;

@Service
public class DropDownServiceImpl implements DropDownService {
	
	private Logger log = LogManager.getLogger(getClass());
	
	
	@Autowired
	private WhatschattemplateRepository quoteRepo;

	@Override
	public List<DropDownRes> getrequestkeylist() {
	try {
	List<DropDownRes> response=new ArrayList<>();
	List<Map<String, Object>> list = quoteRepo.getrequestkeylist();
	log.info("getchatmsgidlist Size: "+list.size());
	for(int i=0;i<list.size();i++) {
	DropDownRes res=DropDownRes.builder()
	.code(list.get(i).get("CODE")==null?"":list.get(i).get("CODE").toString())
	.description_en(list.get(i).get("CODEDESC")==null?"":list.get(i).get("CODEDESC").toString())	
	.build();
	response.add(res);
	}
	return response;
	}catch (Exception e) {
	log.error(e);
	}
	return null;
	}
	
	@Override
	public List<DropDownRes> getchatmsgidlist() {
	try {
	List<DropDownRes> response=new ArrayList<>();
	List<Map<String, Object>> list = quoteRepo.getchatmsgidlist();
	log.info("getchatmsgidlist Size: "+list.size());
	for(int i=0;i<list.size();i++) {
	DropDownRes res=DropDownRes.builder()
	.code(list.get(i).get("CODE")==null?"":list.get(i).get("CODE").toString())
	.description_en(list.get(i).get("CODEDESC")==null?"":list.get(i).get("CODEDESC").toString())	
	.build();
	response.add(res);
	}
	return response;
	}catch (Exception e) {
	log.error(e);
	}
	return null;
	}
	
	
	@Override
	public List<DropDownRes> getuserlist() {
	try {
	List<DropDownRes> response=new ArrayList<>();
	List<Map<String, Object>> list = quoteRepo.getuserlist();
	log.info("getuserlist Size: "+list.size());
	for(int i=0;i<list.size();i++) {
	DropDownRes res=DropDownRes.builder()
	.code(list.get(i).get("CODE")==null?"":list.get(i).get("CODE").toString())
	.description_en(list.get(i).get("CODEDESC")==null?"":list.get(i).get("CODEDESC").toString())	
	.build();
	response.add(res);
	}
	return response;
	}catch (Exception e) {
	log.error(e);
	}
	return null;
	}
	
	@Override
	public List<DropDownRes> getproductlist() {
	try {
	List<DropDownRes> response=new ArrayList<>();
	List<Map<String, Object>> list = quoteRepo.getproductlist();
	log.info("getproductlist Size: "+list.size());
	for(int i=0;i<list.size();i++) {
	DropDownRes res=DropDownRes.builder()
	.code(list.get(i).get("CODE")==null?"":list.get(i).get("CODE").toString())
	.description_en(list.get(i).get("CODEDESC")==null?"":list.get(i).get("CODEDESC").toString())	
	.build();
	response.add(res);
	}
	return response;
	}catch (Exception e) {
	log.error(e);
	}
	return null;
	}

}
