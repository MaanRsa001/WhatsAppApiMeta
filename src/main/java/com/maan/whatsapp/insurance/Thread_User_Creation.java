package com.maan.whatsapp.insurance;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.maan.whatsapp.claimintimation.ClaimIntimationServiceImpl;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Thread_User_Creation extends Thread {

	Logger log = LogManager.getLogger(getClass());
	
	private String request;
	
	private String url ;
	
	private ClaimIntimationServiceImpl service;
	
	private String type;
	
	ObjectMapper mapper = new ObjectMapper();
	
	Gson objectPrint = new Gson();
	
	private String merchantRefNo;
	
	
	private Map<String,String> documentInfoMap;
	
	Thread_User_Creation(String request,String url,ClaimIntimationServiceImpl service, Map<String, String> documentInfoMap){
		
		this.request=request;
		
		this.url=url;
		
		this.service=service;
	}
	

	public Thread_User_Creation(String request, String url,
			ClaimIntimationServiceImpl service, String type,String merchantRefNo
			,Map<String, String> documentInfoMap) {
		this.request=request;
		
		this.url=url;
		
		this.service=service;
		
		this.type=type;

		this.merchantRefNo=merchantRefNo;
		
		this.documentInfoMap=documentInfoMap;
	}


	@Override
	public void run() {
		try {
			String whatsappApi =documentInfoMap.get("whatsappApiButton");
			String whatsappAuth =documentInfoMap.get("whatsappAuth");
			String documentApi =documentInfoMap.get("motorPolicyDocumentApi");
			String whatsappCode =documentInfoMap.get("whatsappCode");
			String whatsappNo =documentInfoMap.get("whatsappNo");
			String quoteNo =documentInfoMap.get("quoteNo");
			String tiraPostApi=documentInfoMap.get("tiraPostApi");

			log.info("USER THREAD REQUEST || "+documentInfoMap);
			
			if("PAYMENT_TRIGGER".equalsIgnoreCase(type)) {
				int oneMinutesSeconds =60000;
				int fiveMinuteSeconds = 7 * oneMinutesSeconds;
				String response ="";
				for(int i =10000 ;i<=fiveMinuteSeconds;i+=10000) {
					
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				
					response =service.callEwayApi(url, request);
					
					log.info("paymentStatusCheckRes -"+i+"+" +response);
					
					String paymentStatus ="";
					try {	
						Map<String,Object>	checkStatusRes =mapper.readValue(response, Map.class);
						String checkStatus1 =(String)checkStatusRes.get("message");
					    JsonObject checkStatus = objectPrint.fromJson(checkStatus1, JsonObject.class);
						//List<Map<String,Object>> paymentData =(List<Map<String,Object>>)[0];
					    JsonArray jsonElement = (JsonArray) checkStatus.get("data");
					    JsonObject paymentData =(JsonObject) jsonElement.get(0);
						paymentStatus = paymentData.get("payment_status").getAsString();  //paymentData.get(0).get("payment_status").toString();
						
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					
					
					log.info("MerchantReference : "+merchantRefNo+" || paymentStatus" +paymentStatus);

					if("COMPLETED".equalsIgnoreCase(paymentStatus)) {
						
						
						Map<String,String> tiraPost =new HashMap<>();
						tiraPost.put("QuoteNo", quoteNo);
						String tiraPostReq =objectPrint.toJson(tiraPost);
						log.info("TIRA POST API CALLING ....."+quoteNo);
						response =service.callEwayApi(tiraPostApi, tiraPostReq);
						log.info("TIRA POST API CALLING RESPONSE ....."+quoteNo);
					
						log.info("payment status || COMPLETED = "+paymentStatus+"");
						
						String encodeQuoteNo =new String(Base64.getEncoder().encodeToString(quoteNo.getBytes()));
						
						String policyDocumentUrl =documentApi+encodeQuoteNo;
						
						log.info("policyDocumentUrl ||"+policyDocumentUrl);
						
						Map<String,Object> documentMap =new HashMap<String,Object>();
						Map<String,Object> header =new HashMap<String,Object>();
						header.put("type", "Document");
						header.put("text", "Alliance Insurance Corporation Limited");
						Map<String,Object> media =new HashMap<String,Object>();
						media.put("url", policyDocumentUrl);
						media.put("fileName", "PolicyDocument.pdf");
						header.put("media", media);
						
						documentMap.put("header", header);
						documentMap.put("body", "*Alliance Insurance Corporation Limited*\n\nAbove we have attached your policy document & click download it");
						documentMap.put("footer", " ");
						Map<String,Object> buttons =new HashMap<String,Object>();
						buttons.put("text", "Main Menu");
						List<Map<String,Object>> listButton =new ArrayList<>();
						listButton.add(buttons);
						documentMap.put("buttons", listButton);
						
						String documentReq = objectPrint.toJson(documentMap);
						
						OkHttpClient okhttp = new OkHttpClient.Builder()
								.readTimeout(30, TimeUnit.SECONDS)
								.build();
						
						RequestBody body = RequestBody.create(documentReq, MediaType.parse("application/json"));
						
						String url ="https://live-server-103813.wati.io/api/v1"+whatsappApi.replace("{whatsappNumber}",whatsappCode+whatsappNo);
						
						Request requestBuilder = new Request.Builder()
								.url(url)
								.addHeader("Authorization", whatsappAuth)
								.post(body)
								.build();

						Response res =null;
						String responseString="";
						try {
							res =okhttp.newCall(requestBuilder).execute();
							responseString = res.body().string();
						}catch (Exception e) {
							e.printStackTrace();
						}
						
						
						
						break;
						
						
						
					}else if("CANCELLED".equalsIgnoreCase(paymentStatus) ||
							"USERCANCELLED".equalsIgnoreCase(paymentStatus) ||
							"REJECTED".equalsIgnoreCase(paymentStatus)) {
						
						// send payment message to user
						
						log.info("payment status || CANCELLED +USERCANCELLED + REJECTED = "+paymentStatus+"");

						break ;
					}
					
				}
				
				log.info("PAYMENT LOOP TRIGGER END ......." +merchantRefNo);

			}else {
				log.info("User Creation Request || "+request);
				String response =service.callEwayApi(url, request);
				log.info("User Creation Response || "+response);
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		
	}

}
