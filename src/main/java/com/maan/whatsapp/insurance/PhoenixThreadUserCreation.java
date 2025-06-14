package com.maan.whatsapp.insurance;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class PhoenixThreadUserCreation extends Thread{

Logger log = LogManager.getLogger(getClass());
	
	private String request;
	
	private String url ;
	
    private String type;
	
	ObjectMapper mapper = new ObjectMapper();
	
	Gson objectPrint = new Gson();
	
	private String merchantRefNo;
	
	
	private Map<String,String> documentInfoMap;
	
	PhoenixThreadUserCreation(String request,String url,Map<String, String> documentInfoMap){
        this.request=request;
		
		this.url=url;
	}
	
	public PhoenixThreadUserCreation(String request, String url,
			String type,String merchantRefNo,Map<String, String> documentInfoMap) {
        this.request=request;
		
		this.url=url;
		
		this.type=type;

		this.merchantRefNo=merchantRefNo;
		
		this.documentInfoMap=documentInfoMap;
	}
}
