package com.maan.whatsapp.insurance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.service.common.CommonService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
public class AsyncProcessThread {
	
	Logger log = LogManager.getLogger(AsyncProcessThread.class);
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private CommonService cs;
	

	private OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS).build();

	private MediaType mediaType = MediaType.parse("application/json");
	
	@Value("${wh.policyholder.api}")
	private String policyHolderApi;
	
	@Value("${wh.section.api}")
	private String sectionApi;
	
	@Value("${wh.bodytype.api}")
	private String bodyTypeApi;
	
	@Value("${wh.vehicleusage.api}")
	private String vehicleUsageApi;

	
	@Async
	public CompletableFuture<List<Map<String,String>>> getPolicyHolderId(String token){
		try {
		

			Map<String,String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("PolicyTypeId", "1");
			
			String request =mapper.writeValueAsString(req);
			String api =policyHolderApi;
			String response =callEwayApi(api, request,token);
			
			log.info("getPolicyHolderId ||"+ response);
			Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
			List<Map<String,String>> apiData =viewCalcRes.get("Result")==null?null:
				(List<Map<String,String>>)viewCalcRes.get("Result");
		
			List<Map<String,String>> returnRes =new ArrayList<Map<String,String>>();
			
			apiData.forEach(p ->{
				Map<String,String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title",p.get("CodeDesc").toString());
				returnRes.add(r);
			});
			
			
			return CompletableFuture.completedFuture(returnRes);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}
	
	@SuppressWarnings("unchecked")
	public String callEwayApi(String url, String request,String token) {
		String apiReponse = "";
		try {
			Response response = null;
			RequestBody apiReqBody = RequestBody.create(request, mediaType);
			Request apiReq = new Request.Builder().addHeader("Authorization", "Bearer " + token).url(url)
					.post(apiReqBody).build();
			response = httpClient.newCall(apiReq).execute();
			apiReponse = response.body().string();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return apiReponse;
	}
	
	
	@Async
	public CompletableFuture<List<Map<String,String>>> getSection(String token){
		try {
			

			Map<String,String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("ProductId", "5");
			
			String request =mapper.writeValueAsString(req);
			String api =sectionApi;
			String response =callEwayApi(api, request,token);
			
			log.info("getSection ||"+ response);
			Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
			List<Map<String,String>> apiData =viewCalcRes.get("Result")==null?null:
				(List<Map<String,String>>)viewCalcRes.get("Result");
		
			List<Map<String,String>> returnRes =new ArrayList<Map<String,String>>();
			
			apiData.forEach(p ->{
				Map<String,String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title",p.get("CodeDesc").toString());
				returnRes.add(r);
			});
			
			
			return CompletableFuture.completedFuture(returnRes);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}
	
	
	@Async
	public CompletableFuture<List<Map<String,String>>> getBodyType(String token){
		try {
			
			Map<String,String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			
			String request =mapper.writeValueAsString(req);
			String api =bodyTypeApi;
			String response =callEwayApi(api, request,token);
			
			log.info("getBodyType ||"+ response);
			Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
			List<Map<String,String>> apiData =viewCalcRes.get("Result")==null?null:
				(List<Map<String,String>>)viewCalcRes.get("Result");
		
			List<Map<String,String>> returnRes =new ArrayList<Map<String,String>>();
			
			int count =1;
			for(Map<String,String> p :apiData) {
				
				Map<String,String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title",p.get("CodeDesc").toString());
				returnRes.add(r);
				count++;
				
				//if(count==20)
					//break;
			}
			
			
			
			
			return CompletableFuture.completedFuture(returnRes);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}
	
	
	
	public List<Map<String,String>> getVehicleUsage(String token,String sectionId){
		try {
			
			Map<String,String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("SectionId", sectionId);
			
			String request =mapper.writeValueAsString(req);
			
			log.info("getVehicleUsagea api reuest||"+ request);

			String api =vehicleUsageApi;
			String response =callEwayApi(api, request,token);
			
			log.info("getVehicleUsage api response ||"+ response);
			Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
			List<Map<String,String>> apiData =viewCalcRes.get("Result")==null?null:
				(List<Map<String,String>>)viewCalcRes.get("Result");
		
			List<Map<String,String>> returnRes =new ArrayList<Map<String,String>>();
			
			apiData.forEach(p ->{
				Map<String,String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title",p.get("CodeDesc").toString());
				returnRes.add(r);
			});
			
			
			return returnRes;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getEwayToken() {
		try {
			Map<String, Object> tokReq = new HashMap<String, Object>();
			tokReq.put("LoginId", "guest");
			tokReq.put("Password", "Admin@01");
			tokReq.put("ReLoginKey", "Y");
			
			Response response = null;
		//	log.info("Token Request ==> "+tokReq.toString());
			String tokenJsonReq = new Gson().toJson(tokReq);
			String tokenApi = cs.getwebserviceurlProperty().getProperty("wh.eway.token.api");
			//log.info("Token Api URL ==> "+tokenApi);
			RequestBody tokenReqBody = RequestBody.create(tokenJsonReq, mediaType);
			Request tokenReq = new Request.Builder().url(tokenApi).post(tokenReqBody).build();
			response = httpClient.newCall(tokenReq).execute();
			String obj = response.body().string();
			Map<String, Object> tokenRes = mapper.readValue(obj, Map.class);
			Map<String, Object> tokenObj = tokenRes.get("Result") == null ? null
					: (Map<String, Object>) tokenRes.get("Result");
			String token = tokenObj.get("Token") == null ? "" : tokenObj.get("Token").toString();
			//log.info("Token Response ==> "+token);

			return token;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
