package com.maan.whatsapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@RestController
public class PremiaIntegration {
	
	static OkHttpClient okhttp = new OkHttpClient.Builder()
			.readTimeout(30, TimeUnit.SECONDS)
			.build();
	
	static okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json");

	static ObjectMapper objectMapper = new ObjectMapper();
	
	@PostMapping("/premia/integration")
	public List<Map<String,Object>> premiaIntegration(@RequestBody PremiaRequest req) throws IOException {
		List<Map<String,Object>> response = new ArrayList<>();
		
		for(String s : req.getQuoteNoList()) {
			
			Map<String,String> request = new HashMap<>();
			request.put("QuoteNo", s);
			
			Gson json = new Gson();
			okhttp3.RequestBody body = okhttp3.RequestBody.create(JSON,json.toJson(request));

			String url ="https://apps.alliance.co.tz/EwayCommonApiLive/push/integration/quote";
			Request Routingrequest = new Request.Builder()
					.url(url)
					.addHeader("Authorization", "Bearer "+req.getToken())
					.post(body)
					.build();

			Response token_res = okhttp.newCall(Routingrequest).execute();
			
			String responseMsg =token_res.body().string();
			System.out.println(responseMsg);
			Map<String,Object> map = objectMapper.readValue(responseMsg, Map.class);
			response.add(map);
			
		}
		
		
		return response;
						 
	}

}
