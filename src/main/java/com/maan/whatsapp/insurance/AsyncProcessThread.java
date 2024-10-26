package com.maan.whatsapp.insurance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.claimintimation.ClaimIntimationServiceImpl;
import com.maan.whatsapp.claimintimation.LosstypeRes;
import com.maan.whatsapp.service.common.CommonService;

import lombok.val;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Component
@PropertySource("classpath:WebServiceUrl.properties")
@Lazy
public class AsyncProcessThread {

	// Logger log = LogManager.getLogger(AsyncProcessThread.class);

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CommonService cs;

	private OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS).build();

	private MediaType mediaType = MediaType.parse("application/json");

	public static Gson printReq = new Gson();

	@Value("${wh.stp.make}")
	private String stpMake;

	@Value("${wh.stp.model}")
	private String stpMakeModel;

	@Value("${wh.policyholder.api}")
	private String policyHolderApi;

	@Value("${wh.section.api}")
	private String sectionApi;

	@Value("${wh.bodytype.api}")
	private String bodyTypeApi;

	@Value("${wh.vehicleusage.api}")
	private String vehicleUsageApi;

	@Value("${wh.customer.title.api}")
	private String customerTitle;

	@Value("${wh.customer.occpation.api}")
	private String occupation;

	@Value("${wh.customer.countryCode.api}")
	private String countryCode;

	@Value("${wh.customer.gender.api}")
	private String gender;

	@Value("${wh.customer.country.api}")
	private String country;

	@Value("${wh.vehcile.color.api}")
	private String vehicleColor;

	@Value("${wh.vehicle.fuelUsed.api}")
	private String vehicleFuelUsed;

	@Value("${wh.vehicle.motorCategory.api}")
	private String motorCategory;

	@Value("${wh.stp.vehicleUsage.api}")
	private String stpVehicleUsage;

	@Value("${wh.stp.bodyType.api}")
	private String stpBodyType;

	@Value("${wh.cq.policytype}")
	private String cqPolicyType;

	@Value("${wh.stp.region}")
	private String stpRegion;

	@Value("${wh.get.reg_no.api}")
	private String wh_get_reg_no_api;

	@Value("${wh.get.insurance.type}")
	private String wh_get_insurance_type_api;
	
	@Value("${wh.get.policy.body.type}")
	private String wh_get_policy_body_type;
	
	@Value("${wh.get.policy.vehicle.usage}")
	private String wh_get_policy_vehicle_usage;

	@Autowired
	private ClaimIntimationServiceImpl claimIntimation;

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getPolicyHolderId(String token) {
		try {

			Map<String, String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("PolicyTypeId", "1");

			String request = mapper.writeValueAsString(req);
			String api = policyHolderApi;
			String response = callEwayApi(api, request, token);

			// log.info("getPolicyHolderId ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	public String callEwayApi(String url, String request, String token) {
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

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getSection(String token) {
		try {

			Map<String, String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("ProductId", "5");

			String request = mapper.writeValueAsString(req);
			String api = sectionApi;
			String response = callEwayApi(api, request, token);

			// log.info("getSection ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getBodyType(String token) {
		try {

			Map<String, String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");

			String request = mapper.writeValueAsString(req);
			String api = bodyTypeApi;
			String response = callEwayApi(api, request, token);

			// log.info("getBodyType ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			int count = 1;
			for (Map<String, String> p : apiData) {

				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
				count++;

				// if(count==20)
				// break;
			}

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	public List<Map<String, String>> getVehicleUsage(String token, String sectionId) {
		try {

			Map<String, String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("SectionId", sectionId);

			String request = mapper.writeValueAsString(req);

			/// log.info("getVehicleUsagea api reuest||"+ request);

			String api = vehicleUsageApi;
			String response = callEwayApi(api, request, token);

			// log.info("getVehicleUsage api response ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return returnRes;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getEwayToken() {
		try {
			Map<String, Object> tokReq = new HashMap<String, Object>();
			tokReq.put("LoginId", "WhatsApp_Uganda_Broker");
			tokReq.put("Password", "Admin@01");
			tokReq.put("ReLoginKey", "Y");

			Response response = null;
			// log.info("Token Request ==> "+tokReq.toString());
			String tokenJsonReq = new Gson().toJson(tokReq);
			String tokenApi = cs.getwebserviceurlProperty().getProperty("wh.eway.token.api");
			// log.info("Token Api URL ==> "+tokenApi);
			RequestBody tokenReqBody = RequestBody.create(tokenJsonReq, mediaType);
			Request tokenReq = new Request.Builder().url(tokenApi).post(tokenReqBody).build();
			response = httpClient.newCall(tokenReq).execute();
			String obj = response.body().string();
			Map<String, Object> tokenRes = mapper.readValue(obj, Map.class);
			Map<String, Object> tokenObj = tokenRes.get("Result") == null ? null
					: (Map<String, Object>) tokenRes.get("Result");
			String token = tokenObj.get("Token") == null ? "" : tokenObj.get("Token").toString();
			// log.info("Token Response ==> "+token);

			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCustomerTitle(String request, String token) {
		try {

			String api = this.customerTitle;
			String response = callEwayApi(api, request, token);

			// log.info("getCustomerTitle ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCustomerOccupation(String request, String token) {
		try {

			String api = this.occupation;
			String response = callEwayApi(api, request, token);

			// log.info("getCustomerOccupation ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCustomerCountryCode(String request, String token) {
		try {

			String api = this.countryCode;
			String response = callEwayApi(api, request, token);

			// log.info("getCustomerCountryCode ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCustomerGender(String request, String token) {
		try {

			String api = this.gender;
			String response = callEwayApi(api, request, token);

			// log.info("getCustomerGender ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCustomerCountry(String request, String token) {
		try {

			String api = this.country;
			String response = callEwayApi(api, request, token);

			// log.info("getCustomerCountry ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getFuelType(String request, String token) {
		try {

			String api = this.vehicleFuelUsed;
			String response = callEwayApi(api, request, token);

			// log.info("getFuelType ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getColor(String request, String token) {
		try {

			String api = this.vehicleColor;
			String response = callEwayApi(api, request, token);

			// log.info("getColor ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			List<Map<String,String>> list = returnRes.stream().limit(200).collect(Collectors.toList());
			
			return CompletableFuture.completedFuture(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCountryCode(String request, String token) {
		try {

			String api = this.countryCode;
			String response = callEwayApi(api, request, token);

			// log.info("getCountryCode ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getSTPBodyType(String request, String token) {
		try {

			String api = this.stpBodyType;
			String response = callEwayApi(api, request, token);

			// log.info("getSTPBodyType ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getSTPVehicleUsage(String request, String token) {
		try {

			String api = this.stpVehicleUsage;
			String response = callEwayApi(api, request, token);

			// log.info("getSTPVehicleUsage ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("CodeDesc").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getMotorCategory(String request, String token) {
		try {

			String api = this.motorCategory;
			String response = callEwayApi(api, request, token);

			// log.info("getMotorCategory ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getManuFactureYear() {
		try {

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();
			List<Long> list = LongStream.rangeClosed(1990, 2024).boxed().sorted(Comparator.reverseOrder())
					.collect(Collectors.toList());

			list.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.toString());
				r.put("title", p.toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> policyType(String token) {
		try {

			Map<String, String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100002");
			req.put("BranchCode", "01");
			req.put("ProductId", "5");

			String request = mapper.writeValueAsString(req);
			String api = cqPolicyType;
			String response = callEwayApi(api, request, token);

			// log.info("policyType ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCustomerRegion(String token) {
		try {

			String api = this.stpRegion;

			Map<String, Object> request = new HashMap<String, Object>();
			request.put("CountryId", "UGA");

			String response = callEwayApi(api, mapper.writeValueAsString(request), token);

			// slog.info("getCustomerRegion ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getSTPModel(String bodyType, String make, String token) {
		try {

			String api = this.stpMakeModel;

			Map<String, Object> model_req = new HashMap<String, Object>();
			model_req.put("BodyId", bodyType);
			model_req.put("InsuranceId", "100002");
			model_req.put("BranchCode", "01");
			model_req.put("MakeId", make);

			String response = callEwayApi(api, mapper.writeValueAsString(model_req), token);

			// slog.info("getCustomerRegion ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getStpMake(String token, String bodyType) {
		try {

			String api = this.stpMake;

			Map<String, Object> make_req = new HashMap<String, Object>();
			make_req.put("BodyId", bodyType);
			make_req.put("InsuranceId", "100002");
			make_req.put("BranchCode", "01");

			String response = callEwayApi(api, mapper.writeValueAsString(make_req), token);

			// slog.info("getCustomerRegion ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getLossTypes(String token, String insuranceId, String product) {
		try {

			Map<String, Object> lossType = new HashMap<String, Object>();
			lossType.put("InsuranceId", insuranceId);
			lossType.put("PolicytypeId", product);
			lossType.put("Status", "Y");

			String api = cs.getwebserviceurlProperty().getProperty("get.loss.type");
			String request = mapper.writeValueAsString(lossType);
			String response = claimIntimation.callClaimApi(token, api, request);
			LosstypeRes lossRes = mapper.readValue(response, LosstypeRes.class);

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			if (lossRes.getPrimary().isEmpty()) {
				Map<String, String> emptyRes = new HashMap<String, String>();
				emptyRes.put("id", "0");
				emptyRes.put("title", "No Record Found");
				returnRes.add(emptyRes);
			} else {
				lossRes.getPrimary().forEach(p -> {
					Map<String, String> r = new HashMap<>();
					r.put("id", p.getCode());
					r.put("title", p.getCodeDesc());
					returnRes.add(r);
				});

			}

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getCauseOfLoss(String token, String insuranceId,
			String divnCode) {
		try {

			Map<String, Object> caseOfLossReq = new HashMap<String, Object>();
			caseOfLossReq.put("InscompanyId", insuranceId);
			caseOfLossReq.put("CclProdCode", divnCode);

			String request = mapper.writeValueAsString(caseOfLossReq);
			String api = cs.getwebserviceurlProperty().getProperty("get.cause.of.loss");

			String response = claimIntimation.callClaimApi(token, api, request);

			List<Map<String, Object>> causeOfLoss = mapper.readValue(response, List.class);
			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			if (causeOfLoss.isEmpty()) {
				Map<String, String> emptyRes = new HashMap<String, String>();
				emptyRes.put("id", "0");
				emptyRes.put("title", "No Record Found");
				returnRes.add(emptyRes);
			} else {
				causeOfLoss.forEach(p -> {
					Map<String, String> r = new HashMap<>();
					r.put("id", p.get("CclCauseLossCode") == null ? "" : p.get("CclCauseLossCode").toString());
					r.put("title", p.get("CclCauseLossDesc") == null ? "" : p.get("CclCauseLossDesc").toString());
					returnRes.add(r);
				});

			}

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	public CompletableFuture<List<Map<String, String>>> getInsuranceType(String token) {
		try {

			String api = this.wh_get_insurance_type_api;

			Map<String, Object> insurance_req = new HashMap<String, Object>();

			insurance_req.put("InsuranceId", "100019");
			insurance_req.put("BranchCode", "55");
			insurance_req.put("ProductId", "5");


			String response = callEwayApi(api, mapper.writeValueAsString(insurance_req), token);

			// slog.info("getCustomerRegion ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);

	}

	public CompletableFuture<List<Map<String, String>>> getInsuranceClass( String token) {
		try {

			Map<String, String> req = new HashMap<String, String>();
			req.put("InsuranceId", "100019");
			req.put("BranchCode", "55");
			req.put("ProductId", "5");
			req.put("LoginId", "ugandabroker3");

			String request = mapper.writeValueAsString(req);
			String api = cqPolicyType;
			String response = callEwayApi(api, request, token);

			// log.info("policyType ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString().equals("1")?"COMP":p.get("Code").toString().equals("2")?"TPFT":"TPO");
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);

	}
	

	public CompletableFuture<List<Map<String, String>>> getPolicyBodyType(String request, String token) {
		try {

			String api = this.stpBodyType;
			String response = callEwayApi(api, request, token);

			// log.info("getSTPBodyType ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}

	@Async("EWAYAPI_EXECUTER")
	public CompletableFuture<List<Map<String, String>>> getPolicyVehicleUsage(String request, String token) {
		try {

			String api = this.stpVehicleUsage;
			String response = callEwayApi(api, request, token);

			// log.info("getSTPVehicleUsage ||"+ response);
			Map<String, Object> viewCalcRes = mapper.readValue(response, Map.class);
			List<Map<String, String>> apiData = viewCalcRes.get("Result") == null ? null
					: (List<Map<String, String>>) viewCalcRes.get("Result");

			List<Map<String, String>> returnRes = new ArrayList<Map<String, String>>();

			apiData.forEach(p -> {
				Map<String, String> r = new HashMap<>();
				r.put("id", p.get("Code").toString());
				r.put("title", p.get("CodeDesc").toString());
				returnRes.add(r);
			});

			return CompletableFuture.completedFuture(returnRes);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CompletableFuture.completedFuture(null);
	}


}
