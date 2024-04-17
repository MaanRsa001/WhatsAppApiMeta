package com.maan.whatsapp.service.motor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.whatsapp.repository.master.WAChatRecipientMasterRepo;
import com.maan.whatsapp.request.motor.RequestData;
import com.maan.whatsapp.response.error.Error;

@Component
public class WhatsappInputValidation {
	
	private Logger log = LogManager.getLogger(getClass());

	@Autowired
	private WAChatRecipientMasterRepo chatRepo;
	@Autowired
	private ObjectMapper objectMapper;
	
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	public List<Error> whatsuppValidation(String request, String id) throws JsonMappingException, JsonProcessingException {
    	List<Error> list = new ArrayList<Error>();
		Pattern spPat = Pattern.compile("[^a-zA-Z0-9 ]");
		
		RequestData data = new RequestData();
		if(StringUtils.isNotBlank(request)) {
			String messageId ="";
			data =objectMapper.readValue(request.toString(), RequestData.class);
			Map<String,Object> object=chatRepo.getParentMessageId(id);
			String parentId =object.get("PARENTMESSAGEID")==null?"":object.get("PARENTMESSAGEID").toString();
			log.info("OptionParentId=====>"+parentId);
			if(StringUtils.isNotBlank(parentId)) {
				Map<String,Object> object1=chatRepo.getParentMessageId(parentId);
				 messageId =object1.get("USEROPTTED_MESSAGEID")==null?"":object1.get("USEROPTTED_MESSAGEID").toString();
			}
			log.info("OptionParentIdMessageId=====>"+messageId);
			// option 11 Claim intimation validation ----Start
			if(messageId.equalsIgnoreCase("11")) {
				String value ="";
				if(data.getPolicyNumber()!=null) {
					value =data.getPolicyNumber();
					Pattern spPat1=Pattern.compile("[P]{1}[-\\.\\s]\\w{6}[-\\.\\s]\\w{5}[-\\.\\s]\\w{2}[-\\.\\s]\\w{5}");
					Matcher spPat5 = spPat1.matcher(value);
					 if(!spPat5.find())  {
						 
						 list.add(new Error ("Please Enter Valid PolicyNumber Format:","MobileNo","101"));

					 }	 
				}else if(data.getChassisNo()!=null) {
					value=data.getChassisNo();
					Matcher spMat = spPat.matcher(value);
					if(spMat.find()) {
						list.add(new Error("Please Enter chassis number without Special Character", "MobileNo", "12"));
					}
					
				}else {
					value =data.getPolicyLossDate();
					if(StringUtils.isBlank(value)) {
						list.add(new Error ("Please enter accident","MobileNo","101"));
					}else {
						boolean status =validateCurrentDate(value);
						if(!status) {
							list.add(new Error ("Future date will be not allowed ","MobileNo","101"));
					}	else {
							if(!isValidDate(value)) {
								list.add(new Error ("Please enter valid date format dd/mm/yyyy","MobileNo","101"));
							}
						}
					}
				}		
		}
		
		}
		return list;
	}

	 private boolean validateCurrentDate(String value) {
		String accidentDate =value;
		boolean status= true;
	    try
	    	{
		    	Date enteredDate = sdf.parse(accidentDate);
		    	Date currentDate = new Date();      
		    	if(enteredDate.after(currentDate)){
		    		status = false;
		    	}
	    }catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    }	   
	
	    return status;
	 }
	 
	 public boolean isValidDate(String inDate) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setLenient(false);
			try {
				dateFormat.parse(inDate);
			} catch (Exception e) {e.printStackTrace();return false;
			}
			return true;
		}
	 
	 
	}
	
	

	
