package com.maan.whatsapp.insurance;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;
import com.maan.whatsapp.response.error.Error;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
@PropertySource("classpath:WebServiceUrl.properties")
public class PhoenixInsuranceServiceImpl implements PhoenixInsuranceService{
	
	@Autowired
	private ObjectMapper mapper;
	
	Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	@Lazy
	private PhoenixAsyncProcessThread thread;
	
	@Value("${phoenix.motor.thirdparty.payementApi}")
	private String thirdPartyPatmentApi;
	
	@Value("${phoenix.motor.thirdparty.paymentCheckApi}")
	private String thirdPartyPaymentCheckApi;
	
	@Value("${whatsapp.api}")						
	private String whatsappApi;
	
	@Value("${whatsapp.api.sendSessionMessage}")						
	private String whatsappApiSendSessionMessage;
	
	@Value("${whatsapp.auth}")						
	private String whatsappAuth;
	
	@Value("${whatsapp.api.button}")						
	private String whatsappApiButton;
	
	@Value("${phoenix.tira.post.api}")						
	private String tiraPostApi;
	
	@Value("${phoenix.motor.policy.document}")						
	private String motorPolicyDocumentApi;
	
	@Value("${phoenix.motor.redirect.url}")						
	private String redirectUrl;
	
	@Autowired
	private Gson objectPrint;

	@SuppressWarnings("unchecked")
	@Override
	public String buypolicy(String request) throws WhatsAppValidationException {
		
		List<Error> errorList = new ArrayList<>(2);
		String exception ="",response="";
		
		String decodeStr =new String(Base64.getDecoder().decode(request.getBytes()));
		Map<String, Object> req=null;
		
		try {
			req = mapper.readValue(decodeStr, Map.class);
		} catch (Exception e1) {
			e1.printStackTrace();
			exception=e1.getMessage();
		} 
		
		if(StringUtils.isNotBlank(exception)) {
			errorList.add(new Error(exception, "ErrorMsg", "101"));
		}
		
		if(errorList.size()>0) {
			throw new WhatsAppValidationException(errorList);
		}
		
		log.info("BuyPolicy request in WhatsappApp :"+decodeStr);
		
		String merchantRefNo=req.get("MerchantRefNo")==null?"":req.get("MerchantRefNo").toString();
		String CompanyId=req.get("CompanyId")==null?"":req.get("CompanyId").toString();
		String whatsappCode=req.get("WhatsappCode")==null?"":req.get("WhatsappCode").toString();
		String whatsappNo=req.get("WhtsappNo")==null?"":req.get("WhtsappNo").toString();
		String quoteNo=req.get("QuoteNo")==null?"":req.get("QuoteNo").toString();
		
		String phoenixPaymentReqApi =thirdPartyPatmentApi+merchantRefNo;
		
		log.info("PhoenixPaymentReqApi || " +phoenixPaymentReqApi);
		
		Map<String,String> payMap =new HashMap<>();
		payMap.put("InsuranceId", CompanyId);
		
		String payReq =objectPrint.toJson(payMap);
		response = thread.callNamibiaComApi(phoenixPaymentReqApi, payReq);
		
		log.info("PhoenixPaymentReqApiRes" +response);
		
		String url = thirdPartyPaymentCheckApi+quoteNo;
		
		log.info("PaymentCheck status API || " +url);
		
		OkHttpClient okhttp = new OkHttpClient.Builder()
				.readTimeout(30, TimeUnit.SECONDS)
				.build();
		
		String wattiUrl =whatsappApi+whatsappApiSendSessionMessage;
		
		wattiUrl = wattiUrl.replace("{whatsappNumber}", whatsappCode+whatsappNo);
		wattiUrl = wattiUrl.replace("{pageSize}", "");
		wattiUrl = wattiUrl.replace("{pageNumber}", "");
		wattiUrl = wattiUrl.replace("{messageText}", "*Payment has been initiated.Please check your mobile notification*");
		wattiUrl = wattiUrl.trim();
		
		RequestBody body = RequestBody.create(new byte[0], null);
		
		Request wattiRequest = new Request.Builder()
				.url(wattiUrl)
				.addHeader("Authorization",this.whatsappAuth )
				.post(body)
				.build();

		try {
			okhttp.newCall(wattiRequest).execute().close();;
			
			}catch (Exception e) {
				e.printStackTrace();
		}
		
		
		Map<String,String> documentInfoMap = new HashMap<String,String>();
		documentInfoMap.put("whatsappCode", whatsappCode);
		documentInfoMap.put("whatsappNo", whatsappNo);
		documentInfoMap.put("motorPolicyDocumentApi", motorPolicyDocumentApi);
		documentInfoMap.put("whatsappApiButton", whatsappApiButton);
		documentInfoMap.put("whatsappAuth", this.whatsappAuth);
		documentInfoMap.put("quoteNo", quoteNo);
		documentInfoMap.put("tiraPostApi", tiraPostApi);
		
		PhoenixThreadUserCreation user_Creation = new PhoenixThreadUserCreation(request, url, "PAYMENT_TRIGGER", 
				merchantRefNo, documentInfoMap) ;
		user_Creation.setName("PAYMENT_TRIGGER");
		user_Creation.start();
		
		return redirectUrl;
	}

}
