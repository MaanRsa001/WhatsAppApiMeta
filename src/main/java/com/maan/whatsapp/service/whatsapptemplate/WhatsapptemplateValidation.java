package com.maan.whatsapp.service.whatsapptemplate;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.whatsapp.repository.whatsapptemplate.WhatschatreciptemplateRepository;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchatrecipiantReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchattempReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsapptemplateReq;
import com.maan.whatsapp.response.error.Error;

@Service
public class WhatsapptemplateValidation {
	
	@Autowired
	private WhatschatreciptemplateRepository whatschatreciprepo;
	
	public List<Error> getchatparentsavevalidate(WhatsappchattempReq req){
		List<Error> list = new ArrayList<Error>();
		try{
			
			String type   = req.getMessageidtype();
			String msgdescen = req.getMessagedescen();
			String status =  req.getStatus();
			String effectivedate = req.getEffectivedate();
			if(StringUtils.isBlank(type))
				list.add(new Error("Please Enter Message ID Type","MessageDesc","4"));
			if(StringUtils.isBlank(msgdescen))
				list.add(new Error("Please Enter Message Description","MessageDesc","1"));
			if(StringUtils.isBlank(status))
				list.add(new Error("Please Select Status","Status","2"));
			if(StringUtils.isBlank(effectivedate))
				list.add(new Error("Please Select Effective Date","Effectivedate","3"));		
		}catch(Exception e){
			
		
		}
		
		return list;
	}
	
	
	public List<Error> getchattemplateparentsavevalidate(WhatsappchatrecipiantReq req){
		List<Error> list = new ArrayList<Error>();
		try{
			
			String msgid = req.getMessageid();
			String type   = req.getMessageidtype();
			String desc = req.getDescription();
			String status =  req.getStatus();
			String isjobyn =  req.getIsjobyn();
			String useroptcode = req.getUseroptted_messageid();
			String effectivedate = req.getEffectivedate();
			String inputyn = StringUtils.isBlank(req.getIsinputyn()) ? "" : req.getIsinputyn();
			String inputkey = StringUtils.isBlank(req.getInputkey()) ? "" : req.getInputkey();
			String inputvalue = StringUtils.isBlank(req.getInputvalue()) ? "" : req.getInputvalue();
			if(StringUtils.isBlank(type))
				list.add(new Error("Please Enter Message ID Type","MessageDesc","4"));
			if(StringUtils.isBlank(desc))
				list.add(new Error("Please Enter Message Description","MessageDesc","1"));
			if(StringUtils.isBlank(status))
				list.add(new Error("Please Select Status","Status","2"));
			if(StringUtils.isBlank(effectivedate))
				list.add(new Error("Please Select Effective Date","Effectivedate","3"));	
			if(StringUtils.isBlank(isjobyn))
				list.add(new Error("Please Select IsJob","IsJobYN","5"));
			if(StringUtils.isBlank(useroptcode))
				list.add(new Error("Please Enter User OptCode","Useroptcode","6"));
			else if(StringUtils.isBlank(msgid) && whatschatreciprepo.optcount(useroptcode,req.getParentmessageid()) != 0)
				list.add(new Error("Opt UserID Please try another one","Useroptcode","6"));
			if(StringUtils.isNotBlank(inputyn) && inputyn.equals("Y")){
				if(StringUtils.isBlank(inputkey))
					list.add(new Error("Please Enter Input key","Inputkey","7"));
				if(StringUtils.isBlank(inputvalue))
					list.add(new Error("Please Enter input Value","Inputvalue","8"));
			}
		}catch(Exception e){
			
		
		}
		
		return list;
	}

	public List<Error> getmainmplatesavevalidate(WhatsapptemplateReq req){
		List<Error> list = new ArrayList<Error>();
		try{
			
			String product   = req.getProductid();
			String agencycode = req.getAgencycode();
			String stagedesc = req.getStagedesc();
			String substage  = req.getStagesubdesc();
			String remarks  = req.getRemarks();
			String fileyn = req.getFileyn();
			String filepath = req.getFilepath();
			String apicallyn = req.getIsapicall();
			String apiurl = req.getApiurl();
			String apiauth = req.getApiauth();
			String apimenthod = req.getApimethod();
			String responsestr = req.getResponsestring();
			String errorresponsestr = req.getErrorrespstring();
			String messagecontenten = req.getMessagecontenten();
			String messageregards = req.getMessageregardsen();
			String isButtonMsg =StringUtils.isBlank(req.getIsButtonMsgYn())?"":req.getIsButtonMsgYn();
			String key=req.getRequestkey();
		    if(StringUtils.isBlank(product))
				list.add(new Error("Please Select Product","Product","1"));
		    if(StringUtils.isBlank(agencycode))
				list.add(new Error("Please Select Usertype","Usertype","2"));
		    if(StringUtils.isBlank(stagedesc))
				list.add(new Error("Please Enter Stage Description","Stagedesc","3"));
		    if(StringUtils.isBlank(substage))
				list.add(new Error("Please Enter Sub-Stage Description","Substage","4"));
		    if(StringUtils.isBlank(remarks))
				list.add(new Error("Please Enter Remarks","Remarks","5"));
		    if(StringUtils.isNotBlank(fileyn) && fileyn.equals("Y") &&  StringUtils.isBlank(filepath))
				list.add(new Error("Please Enter File Path","Filepath","6"));
		    if(StringUtils.isNotBlank(apicallyn) && apicallyn.equals("Y") &&  StringUtils.isBlank(apiurl))
				list.add(new Error("Please Enter Apiurl","apiurl","7"));		    
		    if(StringUtils.isNotBlank(apicallyn) && apicallyn.equals("Y") &&  StringUtils.isBlank(apiauth))
				list.add(new Error("Please Enter Apiauth","apiauth","8"));
		    if(StringUtils.isNotBlank(apicallyn) && apicallyn.equals("Y") &&  StringUtils.isBlank(apimenthod))
				list.add(new Error("Please Enter Api method","apimenthod","9"));
		    /*if(StringUtils.isNotBlank(apicallyn) && apicallyn.equals("Y") &&  StringUtils.isBlank(responsestr))
				list.add(new Error("Please Enter Response string","responsestr","10"));
		    if(StringUtils.isNotBlank(apicallyn) && apicallyn.equals("Y") &&  StringUtils.isBlank(errorresponsestr))
				list.add(new Error("Please Enter Error Response String","errorresponsestr","11"));
		    if(StringUtils.isNotBlank(apicallyn) && apicallyn.equals("Y") &&  StringUtils.isBlank(key))
				list.add(new Error("Please Enter Requestkey","Requestkey","12"));*/
		    if(StringUtils.isBlank(messagecontenten))
				list.add(new Error("Please Enter Message Content ENG","messagecontenten","13"));
		    if(StringUtils.isBlank(messageregards))
				list.add(new Error("Please Enter Message Regards ENG","messageregards","14"));
		    
		    if("Y".equals(isButtonMsg)) {
		    	
		    	if(StringUtils.isBlank(req.getMsgBody())) {
		    		list.add(new Error("Message Body cannot be empty ","messageregards","14"));
		    	}else if(req.getMsgBody().length()>1024) {
		    		list.add(new Error("Message Body cannot be greater than 1024 letters ","messageregards","14"));
		    	}
		    	if(StringUtils.isNotBlank(req.getMsgFooter())) {
		    		if(req.getMsgFooter().length()>60) {
			    		list.add(new Error("Message Footer cannot be greater than 60 letters ","messageregards","14"));
		    		}
		    	}
		    	if(StringUtils.isBlank(req.getHeaderType())) {
		    		list.add(new Error("Message HeaderType cannot be empty","messageregards","14"));
		    	}if(StringUtils.isBlank(req.getHeaderText())) {
		    		list.add(new Error("Message HeaderText cannot be empty","messageregards","14"));
		    	}else if(req.getHeaderText().length()>1024) {
		    		list.add(new Error("Message Body cannot be greater than 60 letters ","messageregards","14"));
		    	}
		    	
		    	if(StringUtils.isBlank(req.getButton1())) {
		    		list.add(new Error("Please enter button1","messageregards","14"));
		    	}else if(StringUtils.isNotBlank(req.getButton1())) {
		    		if(req.getButton1().length()>20) {
			    		list.add(new Error("Button1 cannot be grater than 20 letters","messageregards","14"));
		    		}
		    	}
		    	if(StringUtils.isNotBlank(req.getButton2())) {
		    		if(req.getButton2().length()>20) {
			    		list.add(new Error("Button2 cannot be grater than 20 letters","messageregards","14"));
		    		}
		    	}
		    	if(StringUtils.isNotBlank(req.getButton3())) {
		    		if(req.getButton3().length()>20) {
			    		list.add(new Error("Button3 cannot be greater than 20 letters ","messageregards","14"));
		    		}
		    	}
		    	if("Image".equalsIgnoreCase(req.getHeaderType())) {
		    		
		    		if(StringUtils.isBlank(req.getHeaderImageName())) {
			    		list.add(new Error("Please enter ImageName","messageregards","14"));
		    		}if(StringUtils.isBlank(req.getHeaderImageUrl())) {
			    		list.add(new Error("Please enter ImageUrl : Image url should be as public link or hosted link..!","messageregards","14"));
		    		}
		    	}
		    	
		    }
			
		}catch(Exception e){
			
		
		}
		
		return list;
		
	}

}
