package com.maan.whatsapp.metacontroller;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.auth.basic.ImageDecryptionService;
import com.maan.whatsapp.claimintimation.ClaimIntimationRepository;
import com.maan.whatsapp.claimintimation.ClaimIntimationServiceImpl;
import com.maan.whatsapp.claimintimation.InalipaIntimatedTable;
import com.maan.whatsapp.claimintimation.InalipaIntimatedTableRepository;
import com.maan.whatsapp.entity.master.PreinspectionDataDetail;
import com.maan.whatsapp.entity.master.PreinspectionImageDetail;
import com.maan.whatsapp.insurance.AsyncProcessThread;
import com.maan.whatsapp.insurance.InsuranceServiceImpl;
import com.maan.whatsapp.repository.whatsapp.PreInspectionDataDetailRepo;
import com.maan.whatsapp.repository.whatsapp.PreInspectionDataImageRepo;
import com.maan.whatsapp.service.common.CommonService;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@PropertySource("classpath:WebServiceUrl.properties")
public class WhatsapppFlowServiceImpl implements WhatsapppFlowService{
	
	Logger log = LogManager.getLogger(WhatsapppFlowServiceImpl.class);
	
	ObjectMapper mapper = new ObjectMapper();
	
	public static Gson printReq =new Gson();
	
	@Autowired
	private CommonService cs;
	
	@Autowired
	private ClaimIntimationServiceImpl apicall;
	
	@Autowired
	private AsyncProcessThread thread;
	
	@Autowired
	private InsuranceServiceImpl insurance;
	
	@Autowired
	private ClaimIntimationRepository repository;

	@Autowired
	private PreInspectionDataDetailRepo preInsDataRepo;
	
	@Autowired
	private PreInspectionDataImageRepo pidiRepo;
	
	@Autowired
	private InalipaIntimatedTableRepository inalipaIntiRepo;
	
	@Autowired
	private ImageDecryptionService imageDecrypt;
	
	private static OkHttpClient okhttp = new OkHttpClient.Builder()
			.readTimeout(30, TimeUnit.SECONDS)
			.build();

	
	
	@Value("${wh.stp.make}")
	private String stpMake;
	
	@Value("${wh.stp.model}")
	private String stpMakeModel;
	
	@Value("${wh.stp.region}")
	private String stpRegion;
	
	@Value("${wh.get.ewaydata.api}")
	private String wh_get_ewaydata_api;
	
	@Value("${python.image.token.api}")
	private String python_image_token_api;
	
	@Value("${python.image.validate.api}")
	private String python_image_validate_api;
	
	@Value("${python.image.token.username}")
	private String python_image_token_username;
	
	@Value("${python.image.token.password}")
	private String python_image_token_password;
	
	@Value("${wh.get.reg_no.api}")
	private String wh_get_reg_no_api;
	
	@Value("${wh.save.vehicle.info.api}")
	private String wh_save_vehicle_info_api;
	
	@Value("${wh.save.motor.details.api}")
	private String wh_save_motor_details_api;
	
	@Value("${wh.cq.policytype}")
	private String wh_cq_policytype;
	
		
	private static List<Map<String,String>> IMAGE_SKIP_OPTION = new ArrayList<>();
	
	private static List<Map<String,String>> PRE_DROPDOWN_DATA = new ArrayList<>();

	private static List<Map<String,String>> SAMPLE_DATA = new ArrayList<>();
	
	static{
		Map<String,String> object_1 = new HashMap<String, String>();
		object_1.put("id", "Y");
		object_1.put("title", "Skip");
		IMAGE_SKIP_OPTION.add(object_1);
		
		Map<String,String> object_2 = new HashMap<String, String>();
		object_2.put("id", "1");
		object_2.put("title", "Registration Number");
		PRE_DROPDOWN_DATA.add(object_2);
		
		Map<String,String> object_3 = new HashMap<String, String>();
		object_3.put("id", "2");
		object_3.put("title", "Chassis Number");
		PRE_DROPDOWN_DATA.add(object_3);
		
		Map<String,String> object_4 = new HashMap<String, String>();
		object_4.put("id", "00000");
		object_4.put("title", "--No Record Found--");
		SAMPLE_DATA.add(object_4);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String claimIntimation(Map<String, Object> request) {
		String response ="";
		try {
		
			Map<String,Object> data =(Map<String,Object>) request.get("data");
			String version =request.get("version")==null?"":request.get("version").toString();
			String screen_name =request.get("screen")==null?"":request.get("screen").toString();			
			String component_action =data.get("component_action")==null?"":data.get("component_action").toString();
			String flow_token =request.get("flow_token")==null?"":request.get("flow_token").toString();

			Map<String,Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);
			
			if("VALIDATE_REGISTRATION_NO".equalsIgnoreCase(component_action)) {
				
				Map<String,Object> map = new HashMap<String, Object>();
				
				String claim_inputType =data.get("claim_input_type")==null?"":data.get("claim_input_type").toString();
				String inputdata =data.get("inputdata")==null?"":data.get("inputdata").toString();
				String api="";
				if("1".equals(claim_inputType)) {
					map.put("ChassisNo", inputdata);
					map.put("InsuranceId", "100002");
					api = cs.getwebserviceurlProperty().getProperty("get.policy.details.bychassisNo");
				}else if("2".equals(claim_inputType)) {
					api = cs.getwebserviceurlProperty().getProperty("get.policy.details.bypolicyno");
					map.put("QuotationPolicyNo", inputdata);
					map.put("InsuranceId", "100002");
				}
				
				
			
				String reg_request = printReq.toJson(map);
				
				response =apicall.callApi(api, reg_request);
				log.info("Claim Intimation response " + response);
	
				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				List<Map<String, Object>> claimList = mapper.readValue(response, List.class);

				List<Map<String, Object>> errorList = claimList.isEmpty()?null:(List<Map<String, Object>>) claimList.get(0)
						.get("Errors")==null?Collections.EMPTY_LIST:(List<Map<String, Object>>) claimList.get(0)
								.get("Errors");
				
				if(errorList==null || !errorList.isEmpty() ) {
					Map<String,String> error =new HashMap<String, String>();
					
					if("1".equals(claim_inputType)) {
						error.put("inputdata", "RegistrationNo not found");
					}else if("2".equals(claim_inputType)){
						error.put("inputdata", "PolicyNumber not found");
					}
					
					Map<String,Object> response_data =new HashMap<String, Object>();
					response_data.put("error_messages", error);
					return_res.put("data", response_data);
					
					response=this.mapper.writeValueAsString(return_res);
					return response;
				}else {
					
					Map<String, Object> policy_data = (Map<String, Object>) claimList.get(0).get("PolicyInfo");
					String policyNo =policy_data.get("PolicyNo")==null?"":	policy_data.get("PolicyNo").toString();	
					String customerName =policy_data.get("Contactpername") == null ? "" : policy_data.get("Contactpername").toString();
					String InsuranceId =policy_data.get("InsuranceId") == null ? "" : policy_data.get("InsuranceId").toString();
					String branchCode =policy_data.get("BranchCode") == null ? "" : policy_data.get("BranchCode").toString();
					String regionCode =policy_data.get("RegionCode") == null ? "" : policy_data.get("RegionCode").toString();
					String product =policy_data.get("Product") == null ? "" : policy_data.get("Product").toString();
					String divn_code=policy_data.get("ProductCode") == null ? "" : policy_data.get("ProductCode").toString();

					List<Map<String,String>> list_of_vehicle =claimList.stream().map(p ->{
						Map<String,String> vehicle_det = new HashMap<>();
						
						Map<String, Object> vd = (Map<String, Object>) p.get("VehicleInfo");
					
						String vehicle_model_desc =	vd.get("Vehiclemodeldesc") == null ? ""
								: vd.get("Vehiclemodeldesc").toString();
					
						String chassis_no =	vd.get("ChassisNo") == null ? ""
							: vd.get("ChassisNo").toString();
						
						vehicle_det.put("id", chassis_no);
						vehicle_det.put("title", chassis_no + " || "+vehicle_model_desc+"");
						
						return vehicle_det;
					}).collect(Collectors.toList());
					
					Map<String,Object> navigate_screen =new HashMap<String, Object>();
					Map<String,Object> error_message =new HashMap<String, Object>();
					error_message.put("", "");
					navigate_screen.put("registrationNo", inputdata);
					navigate_screen.put("policyNumber", policyNo);
					navigate_screen.put("customer_name","Customer Name : "+customerName+"");
					navigate_screen.put("PolicyNumberText", "Policy Number : "+policyNo+"");
					navigate_screen.put("claim_input_type", claim_inputType);
					navigate_screen.put("list_of_vehicles", list_of_vehicle);
					navigate_screen.put("InsuranceId", InsuranceId);
					navigate_screen.put("branchCode", branchCode);
					navigate_screen.put("regionCode", regionCode);
					navigate_screen.put("product", product);
					navigate_screen.put("divn_code", divn_code);
					navigate_screen.put("error_messages", error_message);
					
					return_res.put("screen", "ACCIDENT_DETAILS");
					return_res.put("data", navigate_screen);
					response =this.mapper.writeValueAsString(return_res);
					
					return response ;
					
				}
				
			}else if("CLAIM_INPUT_TYPE".equalsIgnoreCase(component_action)) {
								
				String claim_inputType =data.get("claim_input_type")==null?"":data.get("claim_input_type").toString();
				Map<String,Object> inputTypeRes =new HashMap<String, Object>();
				inputTypeRes.put("isInputDataReq", true);
				inputTypeRes.put("isVisibleInputData", true);
				if("1".equals(claim_inputType)) 
					inputTypeRes.put("inputdata_lable_name", "Registration Number");
				else if("2".equals(claim_inputType)) 
					inputTypeRes.put("inputdata_lable_name", "Policy Number");
					
				
				
				return_res.put("data", inputTypeRes);
				
				response =this.mapper.writeValueAsString(return_res);
				
				return response;
			}else if("ACCIDENT_VALIDATION".equalsIgnoreCase(component_action)) {
				
				String vehicle =data.get("vehicle")==null?"":data.get("vehicle").toString();
				String accident_date =data.get("accident_date")==null?"":data.get("accident_date").toString();
				String accident_time =data.get("accident_time")==null?"":data.get("accident_time").toString();
				String contact_no =data.get("contact_no")==null?"":data.get("contact_no").toString();
				String registrationNo =data.get("registrationNo")==null?"":data.get("registrationNo").toString();
				String policyNumber =data.get("policyNumber")==null?"":data.get("policyNumber").toString();
				String customer_name =data.get("customer_name")==null?"":data.get("customer_name").toString();
				String insuranceId =data.get("InsuranceId")==null?"":data.get("InsuranceId").toString();
				String branchCode =data.get("branchCode")==null?"":data.get("branchCode").toString();
				String regionCode =data.get("regionCode")==null?"":data.get("regionCode").toString();
				String product =data.get("product")==null?"":data.get("product").toString();
				String divn_code =data.get("divn_code")==null?"":data.get("divn_code").toString();

				Map<String,String> error_message =new HashMap<String, String>();
				
				
				if(!contact_no.matches("[0-9]+")) {
					error_message.put("contact_no", "Please enter valid mobile");
				}
				else if(!contact_no.matches("0?[0-9]{9}")) {
					error_message.put("contact_no", "MobileNo should be 9 digits");
				}
				if(!accident_time.matches("[0-9]{2}:[0-9]{2}")) {
					error_message.put("accident_time", "Enter valid time format");
				}
				
				if(error_message.size()>0) {
					Map<String,Object> error_message_res =new HashMap<>();
					error_message_res.put("error_messages", error_message);
					return_res.put("data", error_message_res);
					
					response =this.mapper.writeValueAsString(return_res);
					
					return response ;
				}else {
					
					String tokenApi = cs.getwebserviceurlProperty().getProperty("token.api");

					String token =apicall.getClaimToken(tokenApi);
					
					CompletableFuture<List<Map<String,String>>> loss_type=this.thread.getLossTypes(token, insuranceId, product);
					CompletableFuture<List<Map<String,String>>> casue_of_loss=this.thread.getCauseOfLoss(token, insuranceId, divn_code);
					CompletableFuture.allOf(loss_type,casue_of_loss).join();
					
					List<String> list =Arrays.asList("Driver","Owner");
					List<Map<String,String>> drivenBy =list.stream()
							.map(p ->{								
								Map<String,String> map = new HashMap<>();
								map.put("id", p);
								map.put("title", p);
								return map;
							}).collect(Collectors.toList());
					
					Map<String,Object> object  =new HashMap<>();
					
					Map<String,Object> error_message_3 =new HashMap<String, Object>();
					error_message_3.put("", "");
					object.put("vehicle", vehicle);
					object.put("accident_date",accident_date);
					object.put("accident_time", accident_time);
					object.put("contact_no",contact_no);
					object.put("registrationNo", registrationNo);
					object.put("policyNumber", policyNumber);
					object.put("customer_name", customer_name);
					object.put("InsuranceId", insuranceId);
					object.put("branchCode", branchCode);
					object.put("regionCode", regionCode);
					object.put("loss_type", loss_type.get());
					object.put("product", product);
					object.put("divn_code", divn_code);
					
					object.put("casue_of_loss", casue_of_loss.get());
					object.put("responsible_personof_accident", drivenBy);
					object.put("error_messages", error_message_3);
					
					return_res.put("screen", "LOSS_LOCATION");
					return_res.put("data", object);
					response =this.mapper.writeValueAsString(return_res);
					
					return response ;
				}
			}else if("LOSSTYPE_VALIDATION".equalsIgnoreCase(component_action)) {
				
               String loss_type =data.get("loss_type")==null?"":data.get("loss_type").toString();
               String cause_of_loss =data.get("cause_of_loss")==null?"":data.get("cause_of_loss").toString();
               String accident_person =data.get("accident_person")==null?"":data.get("accident_person").toString();
               String accident_desc =data.get("accident_desc")==null?"":data.get("accident_desc").toString();
               String vehicle =data.get("vehicle")==null?"":data.get("vehicle").toString();
               String accident_date =data.get("accident_date")==null?"":data.get("accident_date").toString();
               String accident_time =data.get("accident_time")==null?"":data.get("accident_time").toString();
               String contact_no =data.get("contact_no")==null?"":data.get("contact_no").toString();
               String registrationNo =data.get("registrationNo")==null?"":data.get("registrationNo").toString();
               String policyNumber =data.get("policyNumber")==null?"":data.get("policyNumber").toString();
               String customer_name =data.get("customer_name")==null?"":data.get("customer_name").toString();
               String InsuranceId =data.get("InsuranceId")==null?"":data.get("InsuranceId").toString();
               String branchCode =data.get("branchCode")==null?"":data.get("branchCode").toString();
               String regionCode =data.get("regionCode")==null?"":data.get("regionCode").toString();
               String product =data.get("product")==null?"":data.get("product").toString();
               String divn_code =data.get("divn_code")==null?"":data.get("divn_code").toString();
				
               Map<String,Object> extension_message_response =new HashMap<String, Object>();
               Map<String,Object> params =new HashMap<String, Object>();
               Map<String,Object> param_map =new HashMap<String, Object>();

               params.put("loss_type", loss_type);
               params.put("cause_of_loss", cause_of_loss);
               params.put("accident_person", accident_person);
               params.put("accident_desc", accident_desc);
               params.put("vehicle", vehicle);
               params.put("accident_date", accident_date);
               params.put("accident_time", accident_time);
               params.put("contact_no", contact_no);
               params.put("registrationNo", registrationNo);
               params.put("policyNumber", policyNumber);
               params.put("customer_name", customer_name);
               params.put("InsuranceId", InsuranceId);
               params.put("branchCode", branchCode);
               params.put("regionCode", regionCode);
               params.put("product", product);
               params.put("divn_code", divn_code);
               
               param_map.put("params", params);
               extension_message_response.put("extension_message_response", param_map);
				
				
               return_res.put("screen", "SUCCESS");
               return_res.put("data", extension_message_response);
				
               response =printReq.toJson(return_res);
               
               return response;
	
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}


	@Override
	public String createShortTermPolicy(Map<String, Object> request) {
		String response ="";
		String api_request="";
		String api_response="";
		try {
			Map<String,Object> data =(Map<String,Object>) request.get("data");
			String version =request.get("version")==null?"":request.get("version").toString();
			String screen_name =request.get("screen")==null?"":request.get("screen").toString();			
			String component_action =data.get("component_action")==null?"":data.get("component_action").toString();
			String flow_token =request.get("flow_token")==null?"":request.get("flow_token").toString();

			Map<String,Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);
			
			String sample_data ="[ {\"id\": \"0\", \"title\": \"--SELECT--\"}]";
			String error_messages_1 =" {\"id\": \"\", \"\": \"\"}";
			List<Map<String,Object>> list =mapper.readValue(sample_data, List.class);
			
			String token =this.thread.getEwayToken();
			
			Map<String,String> input_validation =new HashMap<>();
			if("VEHILCE_VALIDATION".equalsIgnoreCase(component_action)) {
				String chassis_no =data.get("chassis_no")==null?"":data.get("chassis_no").toString().trim();
				String body_type =data.get("body_type")==null?"":data.get("body_type").toString().trim();
				String make =data.get("make")==null?"":data.get("make").toString().trim();
				String model =data.get("model")==null?"":data.get("model").toString().trim();
				String engine_capacity =data.get("engine_capacity")==null?"":data.get("engine_capacity").toString().trim();
				String manufacture_year =data.get("manufacture_year")==null?"":data.get("manufacture_year").toString().trim();
				String fuel_used =data.get("fuel_used")==null?"":data.get("fuel_used").toString().trim();
				String vehicle_color =data.get("vehicle_color")==null?"":data.get("vehicle_color").toString().trim();
				String vehicle_usage =data.get("vehicle_usage")==null?"":data.get("vehicle_usage").toString().trim();
				String seating_capacity =data.get("seating_capacity")==null?"":data.get("seating_capacity").toString().trim();
				String isbroker =data.get("isbroker")==null?"":data.get("isbroker").toString().trim();
				String broker_loginid =data.get("broker_loginid")==null?"":data.get("broker_loginid").toString().trim();
				
				String title =data.get("title")==null?"":data.get("title").toString();
				String customer_name =data.get("customer_name")==null?"":data.get("customer_name").toString();
				String mobile_no =data.get("mobile_no")==null?"":data.get("mobile_no").toString();
				String email =data.get("email")==null?"":data.get("email").toString();
				String address =data.get("address")==null?"":data.get("address").toString();
				String region =data.get("region")==null?"":data.get("region").toString();
				String country_code =data.get("country_code")==null?"":data.get("country_code").toString();

				 //validation message text should be not graterthan 30 characters.
				
				if(chassis_no.length()<5) {
					input_validation.put("chassis_no", "Minimum 5 characters required");
				}
				else if(!chassis_no.matches("[a-zA-Z0-9]+")) {
					input_validation.put("chassis_no", "Special characters not allowed");
				}
				
				if(!engine_capacity.matches("[0-9]+")) {
					input_validation.put("engine_capacity", "digits only are allowed");
				}if("1".equalsIgnoreCase(isbroker)) {					
					
					Map<String,String> request_map = new HashMap<String, String>();
					request_map.put("Type", "LOGIN_ID_CHECK");
					request_map.put("LoginId",broker_loginid);
					String ewayValidationApi =wh_get_ewaydata_api;					
					api_response =thread.callEwayApi(ewayValidationApi, mapper.writeValueAsString(request_map),token);
					Map<String,Object> map =mapper.readValue(api_response, Map.class);
					Boolean status =(Boolean)map.get("IsError");
					
					if(status)
						input_validation.put("broker_loginid", "broker loginid not found");
				}
				
				// seating capacity input validation
				if(!seating_capacity.matches("[0-9]+")) {
					input_validation.put("seating_capacity", "Only digits are allowed");
				}else {
					Map<String,String> request_map = new HashMap<String, String>();
					request_map.put("Type", "SEAT_CAPACITY");
					request_map.put("SeatingCapacity",seating_capacity);
					request_map.put("InsuranceId", "100002");
					request_map.put("BranchCode","01");
					request_map.put("BodyType",body_type);
					String ewayValidationApi =wh_get_ewaydata_api;					
					api_response =thread.callEwayApi(ewayValidationApi,mapper.writeValueAsString(request_map),token);
					
					Map<String,Object> validation_map =mapper.readValue(api_response, Map.class);				
					Boolean status =(Boolean)validation_map.get("IsError");					
					if(status) {
						Map<String,Object> seat_map =(Map<String,Object>) validation_map.get("Result");
						String seats =seat_map.get("Seating Capacity").toString();
						input_validation.put("seating_capacity", "should be under "+seats+" or equal ");
					}
				}
			
				// checking validation data
				if(!input_validation.isEmpty() && input_validation.size()>0) {
					
					Map<String,String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "01");
					request_map.put("InsuranceId", "100002");
					request_map.put("BodyId", body_type);
					request_map.put("MakeId", make);
					
					String request_1 =printReq.toJson(request_map);
					
					CompletableFuture<List<Map<String,String>>> fuel_type_e =thread.getFuelType(request_1,token);
					CompletableFuture<List<Map<String,String>>> color_e =thread.getColor(request_1,token);
					CompletableFuture<List<Map<String,String>>> manufacture_year_e =thread.getManuFactureYear();
					CompletableFuture<List<Map<String,String>>> body_type_e =thread.getSTPBodyType(request_1,token);
					CompletableFuture<List<Map<String,String>>> vehicle_usage_e =thread.getSTPVehicleUsage(request_1,token);
					CompletableFuture<List<Map<String,String>>> make_e =thread.getStpMake(token, body_type);
					CompletableFuture<List<Map<String,String>>> model_e =thread.getSTPModel(body_type,make,token);
					

					CompletableFuture.allOf(fuel_type_e,color_e,manufacture_year_e,
							body_type_e,vehicle_usage_e,make_e,model_e).join();
					
					Map<String,Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("body_type", body_type_e.get().isEmpty()?SAMPLE_DATA:body_type_e.get());
					error_messages.put("make", make_e.get().isEmpty()?SAMPLE_DATA:make_e.get());
					error_messages.put("model", model_e.get().isEmpty()?SAMPLE_DATA:model_e.get());
					error_messages.put("manufacture_year", manufacture_year_e.get().isEmpty()?list:manufacture_year_e.get());
					error_messages.put("fuel_used", fuel_type_e.get().isEmpty()?SAMPLE_DATA:fuel_type_e.get());
					error_messages.put("vehicle_color", color_e.get().isEmpty()?SAMPLE_DATA:color_e.get());
					error_messages.put("vehicle_usage", vehicle_usage_e.get().isEmpty()?SAMPLE_DATA:vehicle_usage_e.get());
					
					error_messages.put("title", title);
					error_messages.put("customer_name", customer_name);
					error_messages.put("mobile_no", mobile_no);
					error_messages.put("email", email);
					error_messages.put("address", address);
					error_messages.put("region", region);
					error_messages.put("country_code", country_code);
					
					return_res.put("data", error_messages);
					
					response =printReq.toJson(return_res);
					return response;
					
				}else {
					
					Map<String,Object> extension_message_response =new HashMap<String, Object>();
					Map<String,Object> params =new HashMap<String, Object>();
					Map<String,Object> param_map =new HashMap<String, Object>();
	
					params.put("chassis_no", chassis_no);
					params.put("body_type", body_type);
					params.put("make", make);
					params.put("model", model);
					params.put("engine_capacity", engine_capacity);
					params.put("manufacture_year", manufacture_year);
					params.put("fuel_used", fuel_used);
					params.put("vehicle_color", vehicle_color);
					params.put("vehicle_usage", vehicle_usage);
					params.put("seating_capacity", seating_capacity);
					params.put("isbroker", isbroker);
					params.put("broker_loginid", broker_loginid);
					params.put("title", title);
					params.put("customer_name", customer_name);
					params.put("mobile_no", mobile_no);
					params.put("email", email);
					params.put("address", address);
					params.put("region", region);
					params.put("country_code", country_code);
					params.put("flow_token", flow_token);

					param_map.put("params", params);
					extension_message_response.put("extension_message_response", param_map);
					
					
					return_res.put("screen", "SUCCESS");
					return_res.put("data", extension_message_response);
					
					response =printReq.toJson(return_res);
					
				}
			
				}if("ISBROKER".equalsIgnoreCase(component_action)) {
					
					String is_broker =data.get("isBroker")==null?"":data.get("isBroker").toString().trim();
					Map<String,Boolean> enableLogin =new HashMap<String, Boolean>();
					if("1".equalsIgnoreCase(is_broker)) {
						enableLogin.put("isVisibleBrokerLoginId", true);
						enableLogin.put("isMandatoryBrokerLoginId", true);
					}else if("2".equalsIgnoreCase(is_broker)){
						enableLogin.put("isVisibleBrokerLoginId", false);
						enableLogin.put("isMandatoryBrokerLoginId", false);
					}
					
					return_res.put("data", enableLogin);
					response =printReq.toJson(return_res);
					return response;
					
				
				}else if ("MAKE".equalsIgnoreCase(component_action)) {
					
					String body_type =data.get("body_type")==null?"":data.get("body_type").toString().trim();
					List<Map<String,String>> data_list = new ArrayList<Map<String,String>>();
					
					if(StringUtils.isNotBlank(body_type)) {
						String api =this.stpMake;
						
						Map<String,Object> region_req =new HashMap<String, Object>();
						region_req.put("BodyId", body_type);
						region_req.put("InsuranceId", "100002");
						region_req.put("BranchCode", "01");
						
						api_request =printReq.toJson(region_req);
						
						api_response =thread.callEwayApi(api, api_request,token);
						
						Map<String,Object> region_obj =mapper.readValue(api_response, Map.class);
						List<Map<String,Object>> result =(List<Map<String,Object>>)region_obj.get("Result");
						
						data_list= result.stream().map(p ->{
							Map<String,String> map = new HashMap<>();
							map.put("id", p.get("Code")==null?"":p.get("Code").toString());
							map.put("title", p.get("CodeDesc")==null?"":p.get("CodeDesc").toString());
							return map;
						}).collect(Collectors.toList());
					
					}else {
						data_list =SAMPLE_DATA;
					}
					Map<String,Object> make_list =new HashMap<String, Object>();
					make_list.put("make", data_list);
					
					return_res.put("data", make_list);
					response =printReq.toJson(return_res);
					return response;
		
				}else if("MODEL".equalsIgnoreCase(component_action)) {
					
					String body_type =data.get("body_type")==null?"":data.get("body_type").toString().trim();
					String make =data.get("make")==null?"":data.get("make").toString().trim();
					List<Map<String,String>> data_list = new ArrayList<Map<String,String>>();
					
					if(!"00000".equals(make) && StringUtils.isNotBlank(make) ) {
					
						String api =this.stpMakeModel;
						
						Map<String,Object> region_req =new HashMap<String, Object>();
						region_req.put("BodyId", body_type);
						region_req.put("InsuranceId", "100002");
						region_req.put("BranchCode", "01");
						region_req.put("MakeId", make);
						
						api_request =printReq.toJson(region_req);
						
						api_response =thread.callEwayApi(api, api_request,token);
						
						Map<String,Object> region_obj =mapper.readValue(api_response, Map.class);
						List<Map<String,Object>> result =(List<Map<String,Object>>)region_obj.get("Result");
						
						data_list= result.stream().map(p ->{
							Map<String,String> map = new HashMap<>();
							map.put("id", p.get("Code")==null?"":p.get("Code").toString());
							map.put("title", p.get("CodeDesc")==null?"":p.get("CodeDesc").toString());
							return map;
						}).collect(Collectors.toList());
					}else {
						data_list=SAMPLE_DATA;
					}
					
					Map<String,Object> make_list =new HashMap<String, Object>();
					make_list.put("model", data_list);
					
					return_res.put("data", make_list);
					response =printReq.toJson(return_res);
					return response;
		
		
			}else if("CUSTOMER_VALIDATION".equalsIgnoreCase(component_action)) {
				
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name =data.get("customer_name")==null?"":data.get("customer_name").toString().trim();
				String mobile_no =data.get("mobile_no")==null?"":data.get("mobile_no").toString().trim();
				String email =data.get("email")==null?"":data.get("email").toString().trim();
				String address =data.get("address")==null?"":data.get("address").toString().trim();
				String region =data.get("region")==null?"":data.get("region").toString();
				String country_code =data.get("country_code")==null?"":data.get("country_code").toString().trim();

				
			if(!customer_name.matches("[a-zA-Z ]+")) {
				input_validation.put("customer_name", "Please enter valid name");
			}if(!mobile_no.matches("[0-9]+")) {
				input_validation.put("mobile_no", "Please enter valid mobile");
			}
			else if(!mobile_no.matches("0?[0-9]{9}")) {
				input_validation.put("mobile_no", "MobileNo should be 9 digits");
			}
			
			
			if(input_validation.size()>0) {
				
				Map<String,String> request_map = new HashMap<String, String>();
				request_map.put("BranchCode", "01");
				request_map.put("InsuranceId", "100002");
				
				String request_1 =printReq.toJson(request_map);
				
				
				CompletableFuture<List<Map<String,String>>> title_1 =thread.getCustomerTitle(request_1,token);
				CompletableFuture<List<Map<String,String>>> country_code_1 =thread.getCustomerCountryCode(request_1,token);
				CompletableFuture<List<Map<String,String>>> region_1 =thread.getCustomerRegion(token);
			
				CompletableFuture.allOf(title_1,country_code_1,region_1).join();
				
				Map<String,Object> error_messages = new HashMap<String, Object>();
				error_messages.put("error_messages", input_validation);
				error_messages.put("title", title_1.get().isEmpty()?SAMPLE_DATA:title_1.get());
				error_messages.put("countryCode", country_code_1.get().isEmpty()?SAMPLE_DATA:country_code_1.get());
				error_messages.put("region", region_1.get().isEmpty()?SAMPLE_DATA:region_1.get());
				return_res.put("action", "data_exchange");
				return_res.put("data", error_messages);
				
				response =printReq.toJson(return_res);
				
			}else {
				
				Map<String,String> request_map = new HashMap<String, String>();
				request_map.put("BranchCode", "01");
				request_map.put("InsuranceId", "100002");
				
				String request_1 =printReq.toJson(request_map);
				
				CompletableFuture<List<Map<String,String>>> fuel_type =thread.getFuelType(request_1,token);
				CompletableFuture<List<Map<String,String>>> color =thread.getColor(request_1,token);
				CompletableFuture<List<Map<String,String>>> manufacture_year =thread.getManuFactureYear();
				CompletableFuture<List<Map<String,String>>> body_type =thread.getSTPBodyType(request_1,token);
				CompletableFuture<List<Map<String,String>>> vehicle_usage =thread.getSTPVehicleUsage(request_1,token);
				CompletableFuture<List<Map<String,String>>> motor_category =thread.getMotorCategory(request_1,token);
			

				CompletableFuture.allOf(fuel_type,color,manufacture_year,
						body_type,vehicle_usage).join();
				
				Map<String,Object> map_vehicle = new HashMap<String, Object>();
				map_vehicle.put("title", title);
				map_vehicle.put("customer_name", customer_name);
				map_vehicle.put("mobile_no", mobile_no);
				map_vehicle.put("email", email);
				map_vehicle.put("address", address);
				map_vehicle.put("region", region);
				map_vehicle.put("country_code", country_code);
				map_vehicle.put("fuelUsed",  fuel_type.get().isEmpty()?list:fuel_type.get());
				map_vehicle.put("bodyType",  body_type.get().isEmpty()?list:body_type.get());
				map_vehicle.put("make", list);
				map_vehicle.put("model", list);
				map_vehicle.put("manufactureYear",  manufacture_year.get().isEmpty()?list:manufacture_year.get());
				map_vehicle.put("vehicleColor", color.get().isEmpty()?list:color.get());
				map_vehicle.put("vehicleUsage",  vehicle_usage.get().isEmpty()?list:vehicle_usage.get());
				map_vehicle.put("isVisibleBrokerLoginId", false);
				map_vehicle.put("isMandatoryBrokerLoginId", false);
				map_vehicle.put("error_messages", mapper.readValue(error_messages_1, Map.class));
				return_res.put("data", map_vehicle);
				return_res.put("screen", "VEHICLE_DETAILS");
				response =printReq.toJson(return_res);
				
								
			}
			
				return response;
				
				
			}
		
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	//@PostConstruct
	public void getCustomerDropdown() {
		try {
			
			String token =this.thread.getEwayToken();
			Map<String,String> request_map = new HashMap<String, String>();
			request_map.put("BranchCode", "01");
			request_map.put("InsuranceId", "100002");
			
			String request =printReq.toJson(request_map);
			
			log.info("Customer api start time is : "+new Date());
			
			CompletableFuture<List<Map<String,String>>> title =thread.getCustomerTitle(request,token);
			CompletableFuture<List<Map<String,String>>> occupation =thread.getCustomerOccupation(request,token);
			CompletableFuture<List<Map<String,String>>> country_code =thread.getCustomerCountryCode(request,token);
			CompletableFuture<List<Map<String,String>>> gender =thread.getCustomerGender(request,token);
			CompletableFuture<List<Map<String,String>>> country =thread.getCustomerCountry(request,token);
			
			CompletableFuture.allOf(title,occupation,country_code,gender,country).join();
			
			Map<String,String> response =new HashMap<String, String>();
			response.put("title", printReq.toJson(title.get()));
			response.put("occupation", printReq.toJson(occupation.get()));
			response.put("countryCode", printReq.toJson(country_code.get()));
			response.put("gender", printReq.toJson(gender.get()));
			response.put("country", printReq.toJson(country.get()));
						
			log.info("Customer api End time is : "+new Date());
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	
	public void vehicleDropdown() {
		try {
			
			String token =this.thread.getEwayToken();
			Map<String,String> request_map = new HashMap<String, String>();
			request_map.put("BranchCode", "01");
			request_map.put("InsuranceId", "100002");
			
			String request =printReq.toJson(request_map);
			
			
			CompletableFuture<List<Map<String,String>>> fuel_type =thread.getFuelType(request,token);
			CompletableFuture<List<Map<String,String>>> color =thread.getColor(request,token);
			CompletableFuture<List<Map<String,String>>> manufacture_year =thread.getManuFactureYear();
			CompletableFuture<List<Map<String,String>>> body_type =thread.getSTPBodyType(request,token);
			CompletableFuture<List<Map<String,String>>> vehicle_usage =thread.getSTPVehicleUsage(request,token);
			CompletableFuture<List<Map<String,String>>> motor_category =thread.getMotorCategory(request,token);
			
			
			CompletableFuture.allOf(fuel_type,color,manufacture_year,body_type,vehicle_usage,motor_category).join();
			
			
			Map<String,String> response =new HashMap<String, String>();
			response.put("fuelUsed", printReq.toJson(fuel_type.get()));
			response.put("vehicleColor", printReq.toJson(color.get()));
			response.put("manufactureYear", printReq.toJson(manufacture_year.get()));
			response.put("bodyType", printReq.toJson(body_type.get()));
			response.put("vehicleUsage", printReq.toJson(vehicle_usage.get()));
			response.put("motorCategory", printReq.toJson(motor_category.get()));
			
			
			log.info("Vehicle api End time is : "+new Date());

			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}


	@Override
	public Map<String, Object> quotation_flow_screen_data() {
		try {
		
			Map<String,Object> data = insurance.getWhatsappFlowMaster();
			
			Map<String,Object> flow_action_payload =new HashMap<String, Object>();
			//flow_action_payload.put("version", "3.1");
			flow_action_payload.put("screen", "MOTOR_QUOTATION");
			flow_action_payload.put("data", data);
			
			return flow_action_payload;
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String createVehicleQuotation(Map<String, Object> request) {
		String response ="";
		try {
			Map<String,Object> data =(Map<String,Object>) request.get("data");
			String version =request.get("version")==null?"":request.get("version").toString();
			String screen_name =request.get("screen")==null?"":request.get("screen").toString();			
			String component_action =data.get("component_action")==null?"":data.get("component_action").toString();
			String flow_token =request.get("flow_token")==null?"":request.get("flow_token").toString();

			Map<String,Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);
			
			if("HIDE_SUMINSURED".equalsIgnoreCase(component_action)) {
				String policy_type =data.get("policyType")==null?"":data.get("policyType").toString().trim();;
				Map<String,Object> hide_suminsured = new HashMap<String, Object>();
				if("1".equals(policy_type) || "2".equals(policy_type)) {
					hide_suminsured.put("disble_suminsured_field", true);
					hide_suminsured.put("isSuminsuredRequired", true);
				}else {
					hide_suminsured.put("disble_suminsured_field", false);
					hide_suminsured.put("isSuminsuredRequired", false);
				}
				
				return_res.put("data", hide_suminsured);
				
				response =printReq.toJson(return_res);
				return response;
				
			}else if("VEHICLE_USAGE".equalsIgnoreCase(component_action)) {
				
				String sectionId =data.get("sectionName")==null?"":data.get("sectionName").toString().trim();
				if(StringUtils.isNotBlank(sectionId)) {
					String token =thread.getEwayToken();
					List<Map<String,String>> vehiUsage =thread.getVehicleUsage(token, sectionId);
					
					Map<String,Object> vehicle_usage =new HashMap<String, Object>();
					vehicle_usage.put("vehicleUsage", vehiUsage);
					
					return_res.put("data", vehicle_usage);
					
					response =printReq.toJson(return_res);
				}else {
					response =printReq.toJson(SAMPLE_DATA);
				}
				return response;
				
			}else if("INPUT_VALIDATION".equals(component_action)) {
				
				String idType =data.get("idType")==null?"":data.get("idType").toString().trim();
				String customerName =data.get("customerName")==null?"":data.get("customerName").toString().trim();
				String idNumber =data.get("idNumber")==null?"":data.get("idNumber").toString().trim();
				String sumInsured =data.get("sumInsured")==null?"0":data.get("sumInsured").toString().trim();
				String registrationNo =data.get("registrationNo")==null?"":data.get("registrationNo").toString().trim();
				String policyType =data.get("policyType")==null?"":data.get("policyType").toString().trim();
				String sectionName =data.get("sectionName")==null?"":data.get("sectionName").toString().trim();
				String bodyType =data.get("bodyType")==null?"":data.get("bodyType").toString().trim();
				String vehicleUsage =data.get("vehicleUsage")==null?"":data.get("vehicleUsage").toString().trim();
				String claimyn =data.get("claimyn")==null?"":data.get("claimyn").toString().trim();

				Map<String,Object> input_validation =new HashMap<String, Object>();
				
				
				if(!customerName.matches("[a-zA-Z ]+")) {
					input_validation.put("customerName", "Special letters not allowed");
				}
				
				
				Map<String,Object> tiraMap=insurance.checkRegistrationWithTira(registrationNo);
				if(tiraMap==null) {
					input_validation.put("registrationNo", "Tira data not found");

				}else {
					String errorMessage =tiraMap.get("ErrorMessage")==null?"":tiraMap.get("ErrorMessage").toString();
					if(StringUtils.isNotBlank(errorMessage)) {
						
						input_validation.put("registrationNo", "Policy is not expiry");
					}
				}
				
				if(input_validation.size()>0) {
					Map<String,Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
									
					return_res.put("action", "data_exchange");
					return_res.put("data", error_messages);
					
					response =printReq.toJson(return_res);
					
				}else {
					Map<String,Object> extension_message_response =new HashMap<String, Object>();
					Map<String,Object> params =new HashMap<String, Object>();
					Map<String,Object> param_map =new HashMap<String, Object>();
	
					params.put("idType", idType);
					params.put("customerName", customerName);
					params.put("idNumber", idNumber);
					params.put("sumInsured", sumInsured);
					params.put("registrationNo", registrationNo);
					params.put("policyType", policyType);
					params.put("sectionName", sectionName);
					params.put("bodyType", bodyType);
					params.put("vehicleUsage", vehicleUsage);
					params.put("claimyn", claimyn);
					params.put("flow_token", flow_token);
					
					param_map.put("params", params);
					extension_message_response.put("extension_message_response", param_map);
						
					return_res.put("screen", "SUCCESS");
					return_res.put("data", extension_message_response);
					
					response =printReq.toJson(return_res);
					
				}

				return response;
			}
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return "";
		
	}


	@Override
	public Map<String, Object> stp_flow_screen_data() {
		try {
			
			String token =this.thread.getEwayToken();
			Map<String,String> request_map = new HashMap<String, String>();
			request_map.put("BranchCode", "01");
			request_map.put("InsuranceId", "100002");
			
			String request =printReq.toJson(request_map);
			
			log.info("Vehicle api Start time is : "+new Date());

			
			/*CompletableFuture<List<Map<String,String>>> fuel_type =thread.getFuelType(request,token);
			CompletableFuture<List<Map<String,String>>> color =thread.getColor(request,token);
			CompletableFuture<List<Map<String,String>>> manufacture_year =thread.getManuFactureYear();
			CompletableFuture<List<Map<String,String>>> body_type =thread.getSTPBodyType(request,token);
			CompletableFuture<List<Map<String,String>>> vehicle_usage =thread.getSTPVehicleUsage(request,token);
			CompletableFuture<List<Map<String,String>>> motor_category =thread.getMotorCategory(request,token);*/
			
			CompletableFuture<List<Map<String,String>>> title =thread.getCustomerTitle(request,token);
			CompletableFuture<List<Map<String,String>>> country_code =thread.getCustomerCountryCode(request,token);
			CompletableFuture<List<Map<String,String>>> region =thread.getCustomerRegion(token);
		
			
		//	CompletableFuture.allOf(,color,manufacture_year,
				//	body_type,vehicle_usage,motor_category,title,country_code,region).join();
			
			CompletableFuture.allOf(title,country_code,region).join();
			
			
			
			Map<String,String> error_message =new HashMap<String, String>();
			error_message.put("", "");
			
			Map<String,Object> data =new HashMap<String, Object>();
			data.put("title",title.get());
			data.put("countryCode", country_code.get());
			data.put("region",region.get());
			data.put("error_messages",error_message);		
			log.info("Vehicle api End time is : "+mapper.writeValueAsString(data));	
			
			Map<String,Object> flow_action_payload =new HashMap<String, Object>();
			flow_action_payload.put("screen", "CUSTOMER_DETAILS");
			flow_action_payload.put("data", data);

			return flow_action_payload;
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Map<String, Object> claimIntimateScreenData() {
		try {
			
			Map<String, Object> error_msg = new HashMap<String, Object>();
			error_msg.put("", "");
			
			Map<String, String> policyNo = new HashMap<String, String>();
			Map<String, String> regNo = new HashMap<String, String>();
			regNo.put("id", "1");
			regNo.put("title", "Registration Number");
			policyNo.put("id", "2");
			policyNo.put("title", "PolicyNumber");
			
			List<Map<String,String>> claimInputOpt =new ArrayList<>();
			claimInputOpt.add(policyNo);
			claimInputOpt.add(regNo);
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			InputStream is =this.getClass().getResourceAsStream("/images/claimintimation.bin");
			
			String image_url  = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
			
			data.put("imageUrl", image_url);
			data.put("image_width", 500);
			data.put("image_height", 200);
			data.put("is_image_visible", true);
			data.put("is_text_visible", true);
			data.put("error_messages", error_msg);
			data.put("isVisibleRegNo", false);
			data.put("isVisiblePolicyNo", false);
			data.put("isReqRegNo", false);
			data.put("isReqPolicyNo", false);
			data.put("claim_input_type", claimInputOpt);
			
			Map<String,Object> payload =new HashMap<String, Object>();
			payload.put("screen", "WELCOME_SCREEN");
			payload.put("data", data);
			
			return payload;
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String inalipaClaimIntimation(Map<String, Object> request) {
		String response = "";
		try {
			Map<String,Object> data =(Map<String,Object>) request.get("data");
			String version =request.get("version")==null?"":request.get("version").toString();
			String screen_name =request.get("screen")==null?"":request.get("screen").toString();			
			String component_action =data.get("component_action")==null?"":data.get("component_action").toString();
			String flow_token =request.get("flow_token")==null?"":request.get("flow_token").toString();

			Map<String,Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);
			
             if("INALIPA_CLAIM_VALIDATION".equalsIgnoreCase(component_action)) {
            	 
            	 String claim_type =data.get("claim_type")==null?"":data.get("claim_type").toString();
            	 String mobile_no =data.get("mobile_no")==null?"":data.get("mobile_no").toString();
            	 String timestamp =data.get("accident_date")==null?"":data.get("accident_date").toString();
            	 
            	 Date accident_date = new Date(Long.valueOf(timestamp));
            	 Date system_date = new Date();
            	 String format_date =this.cs.formatdatewithouttime(accident_date);
            	 LocalDate local_date = accident_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            	//  validate existing claim are there or not
            	 
            	  
	            Map<String,Object> validation = new HashMap<String, Object>();

 				if(validation.size()==0) {
 					if(accident_date.after(system_date)) {
 						validation.put("accident_date", "Future date does not allow");
		            }
		            if(!mobile_no.matches("0?[0-9]{9}")) {
		            	validation.put("mobile_no", "9 digits only allowed");
		            }     
 				}
 					
	 			Map<String,Object> resultMap = new HashMap<String, Object>();
	 			String policyNo ="";
				Long mobileNo =0L;
 				if(validation.isEmpty()) {
 						
 					String url = cs.getwebserviceurlProperty().getProperty("eway.claimDetails.api");
 					LinkedHashMap<String, Object> map = new LinkedHashMap<>();
 					map.put("MobileNo", mobile_no);
 					map.put("AccidentDate", format_date);
 					map.put("ClaimType", claim_type);
 					log.info("Api Call URL ==> "+url);
 					log.info("Api Call Request ==> "+printReq.toJson(map));
 					String claim_request = printReq.toJson(map);
 					String claim_response =apicall.callEwayApi(url, claim_request);

 					Map<String,Object> result = null;
 	 				try {
 	 					result = mapper.readValue(claim_response, Map.class);
 	 				} catch (Exception e) {
 	 					e.printStackTrace();
 	 				}
 	 						 					 
 	 				if("SUCCESS".equalsIgnoreCase(result.get("Message").toString())) {
 	 					List<Map<String,Object>> resultList = (List<Map<String, Object>>) result.get("Response");
 	 					resultMap = resultList.get(0);
 	 					policyNo = resultMap.get("PolicyNo")==null?"":resultMap.get("PolicyNo").toString();
 	 					mobileNo = resultMap.get("MobileNo")==null?null:Long.parseLong(resultMap.get("MobileNo").toString());
 	 					List<InalipaIntimatedTable>	exitorNot = inalipaIntiRepo.getExistsClaimDetails(policyNo,mobileNo,local_date);
 	 						
 	 					if(!exitorNot.isEmpty()) {
 	 						validation.put("mobile_no", "claim exits already");
 	 		            	validation.put("accident_date",  "claim exits already");
 	 					}
 	 					}else {
 		            		 validation.put("mobile_no", "No record found");
 		            		 validation.put("accident_date",  "No record found");
 	 					}
 	         	   
 	 					log.info("Inalipa claim response :: "+mapper.writeValueAsString(resultMap));
 				}
 					
            	 if(validation.size()>0) {
            		 Map<String,Object> error_messages = new HashMap<String, Object>();  
            		 error_messages.put("error_messages", validation);
            		 error_messages.put("claim_type", getInalipaClaimTypes()); 
            		 return_res.put("data", error_messages);
            		 response =this.mapper.writeValueAsString(return_res);
            		 return response ;
            	 }else {   
            		 
            		Date policyStartDate =resultMap.get("InceptionDate")==null?null:new SimpleDateFormat("dd/MM/yyyy").parse(resultMap.get("InceptionDate").toString());
					Date policyEndDate =resultMap.get("ExpiryDate")==null?null:new SimpleDateFormat("dd/MM/yyyy").parse(resultMap.get("ExpiryDate").toString());
					String claim_type_desc ="1".equals(claim_type)?"Death Claim":"2".equals(claim_type)?"Partial Injury":"Permanent & Total Disability";	
 					String claimRefMax = repository.getInalipaClamRefMax();
 					InalipaIntimatedTable ina =new InalipaIntimatedTable();
 					InalipaIntimatedTable in = InalipaIntimatedTable.builder()
 								.policyNo(policyNo)
 								.mobileNo(mobileNo)
 								.intimatedMobileNo(Integer.valueOf(mobileNo.toString()))
 								.policyStartDate(policyStartDate)
 								.policyEndDate(policyEndDate)
 								.intimatedDate(new Date())
 								.ClaimType(claim_type_desc)
 								.accidentDate(accident_date)
 								.claimNo(claimRefMax)
 								.claimId(claim_type)
 								.build();
 					ina =inalipaIntiRepo.save(in);
 						
 					
           
            		 Map<String,Object> param = new HashMap<String, Object>();
            		 param.put("claim_text", "Claim Number : "+ina.getClaimNo());
            		 param.put("claim_no",ina.getClaimNo());
            		 param.put("accident_date", "Accident Date : "+format_date);
            		 param.put("policy_start_date","PolicyStartDate : "+cs.formatdatewithouttime(ina.getPolicyStartDate()));
            		 param.put("policy_end_date", "PolicyStartDate : "+cs.formatdatewithouttime(ina.getPolicyEndDate()));
            		 param.put("mobile_no", "Mobile Number : "+ina.getIntimatedMobileNo().toString());
            		 param.put("claim_type", claim_type_desc);
            		 param.put("policy_no", "Policy Number : "+ina.getPolicyNo());
            		 param.put("remarks", "Please note above claim information or take screenshot for future purpose");
            			
 					 return_res.put("screen", "INALIPA_CLAIM_RESPONSE");
 					 return_res.put("data", param);
            		 
 					return  response =cs.reqPrint(return_res);
            	 }
             }
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Object getInalipaClaimTypes() {
		try {
			
			Map<String,String> map =new HashMap<String, String>();
			map.put("1", "Death Claim");
			map.put("2", "Partial Injury");
			map.put("3", "Permanent & Total Disability");
			
			 List<Map<String,String>> list  = new ArrayList<Map<String,String>>();
			
			map.entrySet().forEach(p ->{
				Map<String,String> data =new HashMap<String, String>();
				data.put("id", p.getKey());
				data.put("title", p.getValue());
				list.add(data);
			});
			
			return list;
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public Map<String, Object> InalipaIntimateScreenData() {
		try {
		
			Map<String,Object> data = new HashMap<String, Object>();
			data.put("claim_type", getInalipaClaimTypes());
			
			Map<String,Object> flow_action_payload =new HashMap<String, Object>();
			flow_action_payload.put("screen", "INALIPA_CLAIM_INTIMATION");
			flow_action_payload.put("data", data);
			
			return flow_action_payload;
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public String preinspectionUpload(Map<String, Object> request) {
		String response ="";
		try {
			
			String screen_name =request.get("screen").toString();
			String version =request.get("version")==null?"":request.get("version").toString();
			Map<String,Object> map = new HashMap<String,Object>();
			Map<String,Object> data =request.get("data")==null?null:(Map<String,Object>)request.get("data");
			String component_action=data.get("component_action")==null?"":data.get("component_action").toString();
			String upload_type=data.get("upload_type")==null?"image":data.get("upload_type").toString();

			if("WELCOME_SCREEN".equalsIgnoreCase(component_action)) {
				
				String mobile_no=data.get("mobile_no")==null?"":data.get("mobile_no").toString().trim();
				String inputdata=data.get("inputdata")==null?"":data.get("inputdata").toString().trim();
				String input_type=data.get("input_type")==null?"":data.get("input_type").toString().trim();

				Map<String,String> validation = new HashMap<>();
				if("1".equals(input_type)) {
					if(inputdata.length()<5) {
						validation.put("inputdata", "Minimum 5 characters required");
					}else if(!StringUtils.isAlphanumeric(inputdata)) {
						validation.put("inputdata", "Special characters not allowed");
					}
				}else if("2".equals(input_type)) {
					if(inputdata.length()<5) {
						validation.put("inputdata", "Minimum 5 characters required");
					}else if(inputdata.matches("^[a-zA-Z0-9]+")) {
							validation.put("inputdata", "Special characters not allowed");
					}
				}
				Map<String,Object> flow_data = new HashMap<String, Object>();

				if(validation.size()>0) {
					flow_data.put("error_messages", validation);						
					map.put("screen", screen_name);
					map.put("version", version);
					map.put("data", flow_data);
					
				}else {
					
					if("image".equalsIgnoreCase(upload_type)) {
						
						PreinspectionDataDetail pdd =insertPreinspectionData(input_type,inputdata,mobile_no,"VEHICLE_IMAGES");
						flow_data.put("title", "Upload Registration Card Image");
						flow_data.put("label", "Upload Registration Card Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", pdd.getTranId().toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
						
							
						map.put("screen", "REGISTRATION_CARD");
						map.put("version", version);
						map.put("data", flow_data);
					}else if("document".equalsIgnoreCase(upload_type)) {
						
						PreinspectionDataDetail pdd =insertPreinspectionData(input_type,inputdata,mobile_no,"KYC_DOCUMENTS");
						flow_data.put("title", "Upload KYC Documents");
						flow_data.put("label", "Upload KYC Documents");
						flow_data.put("description", "Please browse your document from folder and upload it");
						flow_data.put("upload_transaction_no", pdd.getTranId().toString());
						flow_data.put("footer_label", "Upload");
						
							
						map.put("screen", "KYC_DOCUMENT_UPLOAD");
						map.put("version", version);
						map.put("data", flow_data);
					}
				}
					
				return response = mapper.writeValueAsString(map);
				
			}else if("REGISTRATION_CARD".equalsIgnoreCase(screen_name)) {
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());
				String image_skip =data.get("skip_image")==null?"N":data.get("skip_image").toString().trim();
				if("Y".equals(image_skip)) {
					
					insertPreinspectionData(upload_transaction_no,"Skip","Registration Card","Skip",101);

					Map<String,Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Upload Speedo Meter Image");
					flow_data.put("label", "Upload Speedo Meter Image");
					flow_data.put("description", "Please take a photo or browse your folder to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					flow_data.put("skip_image", IMAGE_SKIP_OPTION);
					
					map.put("screen", "SPEEDO_METER");
					map.put("version", version);
					map.put("data", flow_data);
					
					return response = mapper.writeValueAsString(map);
					
				}else if("N".equals(image_skip)) {
				
					List<Map<String,Object>> listImage =data.get("registration_card_image")==null?Collections.EMPTY_LIST:
						(List<Map<String,Object>>)data.get("registration_card_image");			
				
					Map<String,Object> image = listImage.get(0);
				
					String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
					String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
						
					String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path=file_path+file_name;					
					
					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);					
					FileUtils.writeByteArrayToFile(file, imageArray);
					Map<String,Object> flow_data = new HashMap<String, Object>();
				
					Boolean validation_status =false;//validateImageFile("vehicle_exterior",file);
					if(validation_status) {
						flow_data.put("title", "Upload Registration Card Image");
						flow_data.put("label", "Upload Registration Card Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
				
						Map<String,String> validation = new HashMap<String, String>();
						validation.put("registration_card_image", "Not Acceptable : The uploaded image is not a registration card.");
						flow_data.put("error_messages", validation);
						
						map.put("screen", screen_name);
						map.put("version", version);
						map.put("data", flow_data);
						
						if(file.exists())
							file.delete();
					}else {	
						
						insertPreinspectionData(upload_transaction_no,file_path,"Registration Card",file_name,101);
						flow_data.put("title", "Upload Speedo Meter Image");
						flow_data.put("label", "Upload Speedo Meter Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
											
						map.put("screen", "SPEEDO_METER");
						map.put("version", version);
						map.put("data", flow_data);
					}
					
					
					//log.info("Vehicle front :: "+file_path);
					return response = mapper.writeValueAsString(map);
				}
				
			}if("SPEEDO_METER".equalsIgnoreCase(screen_name)) {
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());
				String image_skip =data.get("skip_image")==null?"N":data.get("skip_image").toString().trim();
				if("Y".equals(image_skip)) {
					
					insertPreinspectionData(upload_transaction_no,"Skip","Speedo Meter Image","Skip",102);

					Map<String,Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Upload Chassis Number Image");
					flow_data.put("label", "Upload Chassis Number Image");
					flow_data.put("description", "Please take a photo or browse your folder to upload an image");
					flow_data.put("upload_transaction_no",upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					flow_data.put("skip_image", IMAGE_SKIP_OPTION);
					
					map.put("screen", "CHASSIS_NUMBER");
					map.put("version", version);
					map.put("data", flow_data);
					
					return response = mapper.writeValueAsString(map);
					
				}else if("N".equals(image_skip)) {
			
					List<Map<String,Object>> listImage =data.get("speedo_meter_image")==null?Collections.EMPTY_LIST:
						(List<Map<String,Object>>)data.get("speedo_meter_image");			
				
					Map<String,Object> image = listImage.get(0);
				
					String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
					String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
						
					String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path=file_path+file_name;					
					
					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);					
					FileUtils.writeByteArrayToFile(file, imageArray);		
					Map<String,Object> flow_data = new HashMap<String, Object>();
										
					Boolean validation_status =false;//validateImageFile("speedometer",file);
					if(validation_status) {
						Map<String,String> validation = new HashMap<String, String>();
						validation.put("speedo_meter_image", "Not Acceptable : The uploaded image is not a speedometer.");
						
						flow_data.put("error_messages", validation);
						flow_data.put("title", "Upload Speedo Meter Image");
						flow_data.put("label", "Upload Speedo Meter Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
							
						map.put("screen", screen_name);
						map.put("version", version);
						map.put("data", flow_data);
						
						if(file.exists())
							file.delete();
						
					}else {	
									
						insertPreinspectionData(upload_transaction_no,file_path,"Speedo Meter Image",file_name,102);
						flow_data.put("title", "Upload Chassis Number Image");
						flow_data.put("label", "Upload Chassis Number Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
						map.put("screen", "CHASSIS_NUMBER");
						map.put("version", version);
						map.put("data", flow_data);
					}
							
					
					//log.info("Vehicle front :: "+file_path);
					return response = mapper.writeValueAsString(map);
				}
				
			}else if("CHASSIS_NUMBER".equalsIgnoreCase(screen_name)) {
			
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());
				String image_skip =data.get("skip_image")==null?"N":data.get("skip_image").toString().trim();
				if("Y".equals(image_skip)) {
					
					
					insertPreinspectionData(upload_transaction_no,"Skip","Chassis Number Image","Skip",103);

					
					Map<String,Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Vehicle Front Side Image");
					flow_data.put("label", "Upload Vehicle Front Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					
					map.put("screen", "VEHICLE_FRONT");
					map.put("version", version);
					map.put("data", flow_data);
					
					return response = mapper.writeValueAsString(map);
					
				}else if("N".equals(image_skip)) {
			
					List<Map<String,Object>> listImage =data.get("chassis_number_image")==null?Collections.EMPTY_LIST:
						(List<Map<String,Object>>)data.get("chassis_number_image");			
				
					Map<String,Object> image = listImage.get(0);
				
					String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
					String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
						
					String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path=file_path+file_name;					
					
					byte imageArray[] = imageDecrypt.decryptMedia(image);					
					File file = new File(file_path);						
					FileUtils.writeByteArrayToFile(file, imageArray);						
				
					Map<String,Object> flow_data = new HashMap<String, Object>();
					
					Boolean validation_status =false;//validateImageFile("vehicle_exterior",file);

					
					if(validation_status) {
						Map<String,String> validation = new HashMap<String, String>();
						validation.put("chassis_number_image", "Not Acceptable : The uploaded image is not a chassisnumber.");
						
						flow_data.put("error_messages", validation);
						flow_data.put("title", "Upload Chassis Number Image");
						flow_data.put("label", "Upload Chassis Number Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
						map.put("screen", screen_name);
						map.put("version", version);
						map.put("data", flow_data);
						
						if(file.exists())
							file.delete();
					}else {	
						
						insertPreinspectionData(upload_transaction_no,file_path,"Chassis Number Image",file_name,103);
						flow_data.put("title", "Vehicle Front Side Image");
						flow_data.put("label", "Upload Vehicle Front Side Image");
						flow_data.put("description", "Please take a photo to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");											
						map.put("screen", "VEHICLE_FRONT");
						map.put("version", version);
						map.put("data", flow_data);
						
					}
					
						
					//log.info("Vehicle front :: "+file_path);
					return response = mapper.writeValueAsString(map);
				}
				
			}
			else if("VEHICLE_FRONT".equalsIgnoreCase(screen_name)) {
			
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String,Object>> listImage =data.get("vehicle_front")==null?Collections.EMPTY_LIST:
					(List<Map<String,Object>>)data.get("vehicle_front");			
			
				Map<String,Object> image = listImage.get(0);
			
				String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
				String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
					
				String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path=file_path+file_name;					
				
				byte imageArray[] = imageDecrypt.decryptMedia(image);				
				File file = new File(file_path);					
				FileUtils.writeByteArrayToFile(file, imageArray);	
				Map<String,Object> flow_data = new HashMap<String, Object>();
								
				Boolean validation_status = false;//validateImageFile("vehicle_exterior",file);
				if(validation_status) {
					Map<String,String> validation = new HashMap<String, String>();
					validation.put("vehicle_front", "Not Acceptable : The uploaded image is not a vehicle image.");
					flow_data.put("error_messages", validation);
					
					flow_data.put("title", "Vehicle Front Side Image");
					flow_data.put("label", "Upload Vehicle Front Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");	
					map.put("screen", screen_name);
					map.put("version", version);
					map.put("data", flow_data);
					
					if(file.exists())
						file.delete();
				}else {	
									
					insertPreinspectionData(upload_transaction_no,file_path,"Front Vehicle Image",file_name,104);
					flow_data.put("title", "Vehicle Back Side Image");
					flow_data.put("label", "Upload Vehicle Back Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					
					map.put("screen", "VEHICLE_BACK");
					map.put("version", version);
					map.put("data", flow_data);
					
				}
									
				//log.info("Vehicle front :: "+file_path);
				return response = mapper.writeValueAsString(map);
				
			}else if("VEHICLE_BACK".equalsIgnoreCase(screen_name)) {
			
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String,Object>> listImage =data.get("vehicle_back")==null?Collections.EMPTY_LIST:
					(List<Map<String,Object>>)data.get("vehicle_back");			
			
				Map<String,Object> image = listImage.get(0);
			
				String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
				String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
					
				String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path=file_path+file_name;					
				
				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);					
				FileUtils.writeByteArrayToFile(file, imageArray);					
			
				Map<String,Object> flow_data = new HashMap<String, Object>();
							
				Boolean validation_status = false;//validateImageFile("vehicle_exterior",file);
				if(validation_status) {
					Map<String,String> validation = new HashMap<String, String>();
					validation.put("vehicle_back", "Not Acceptable : The uploaded image is not a vehicle image.");
					
					flow_data.put("error_messages", validation);
					flow_data.put("title", "Vehicle Back Side Image");
					flow_data.put("label", "Upload Vehicle Back Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
				
					map.put("screen", screen_name);
					map.put("version", version);
					map.put("data", flow_data);
					
					if(file.exists())
						file.delete();
				}else {	
					
					insertPreinspectionData(upload_transaction_no,file_path,"Back Vehicle Image",file_name,105);
					flow_data.put("title", "Vehicle Right Side Image");
					flow_data.put("label", "Upload Vehicle Right Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					
					map.put("screen", "VEHICLE_RIGHT");
					map.put("version", version);
					map.put("data", flow_data);
					
				}
				
						
				//log.info("Vehicle Back :: "+file_path);
				
				return response = mapper.writeValueAsString(map);
				

			}else if("VEHICLE_RIGHT".equalsIgnoreCase(screen_name)) {
			
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());
				
				List<Map<String,Object>> listImage =data.get("vehicle_right")==null?Collections.EMPTY_LIST:
					(List<Map<String,Object>>)data.get("vehicle_right");			
			
				Map<String,Object> image = listImage.get(0);
			
				String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
				String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
					
				String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path=file_path+file_name;					
				
				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);					
				FileUtils.writeByteArrayToFile(file, imageArray);					
						
				Map<String,Object> flow_data = new HashMap<String, Object>();
					
				Boolean validation_status = false;//validateImageFile("vehicle_exterior",file);
				if(validation_status) {
					Map<String,String> validation = new HashMap<String, String>();
					validation.put("vehicle_right", "Not Acceptable : The uploaded image is not a vehicle image.");
					
					flow_data.put("error_messages", validation);
					flow_data.put("title", "Vehicle Right Side Image");
					flow_data.put("label", "Upload Vehicle Right Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					
					map.put("screen", screen_name);
					map.put("version", version);
					map.put("data", flow_data);
					
					if(file.exists())
						file.delete();
				}else {	
					
					insertPreinspectionData(upload_transaction_no,file_path,"Side Vehicle Image",file_name,106);
					flow_data.put("title", "Vehicle Left Side Image");
					flow_data.put("label", "Upload Vehicle Left Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");				
				
					map.put("screen", "VEHICLE_LEFT");
					map.put("version", version);
					map.put("data", flow_data);				
					
				}
				
				//log.info("Vehicle Right :: "+file_path);

				return response = mapper.writeValueAsString(map);
				
				
			}else if("VEHICLE_LEFT".equalsIgnoreCase(screen_name)) {
			
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());
				
				List<Map<String,Object>> listImage =data.get("vehicle_left")==null?Collections.EMPTY_LIST:
					(List<Map<String,Object>>)data.get("vehicle_left");			
			
				Map<String,Object> image = listImage.get(0);
			
				String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
				String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
					
				String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path=file_path+file_name;					
				
				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);				
				FileUtils.writeByteArrayToFile(file, imageArray);					
			
				Map<String,Object> flow_data = new HashMap<String, Object>();
				
				//log.info("Vehicle Right :: "+file_path);
				Boolean validation_status = false;//validateImageFile("vehicle_exterior",file);
				if(validation_status) {
					Map<String,String> validation = new HashMap<String, String>();
					validation.put("vehicle_right", "Not Acceptable : The uploaded image is not a vehicle image.");
					
					flow_data.put("error_messages", validation);
					flow_data.put("title", "Vehicle Left Side Image");
					flow_data.put("label", "Upload Vehicle Left Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");				
				
					map.put("screen", screen_name);
					map.put("version", version);
					map.put("data", flow_data);
					
					if(file.exists())
						file.delete();
				}else {	
					
					insertPreinspectionData(upload_transaction_no,file_path,"Bottom Vehicle Image",file_name,107);
					flow_data.put("header", "Alliance Insurance Corportation Limted");
					flow_data.put("response_text", "Your documents has been received. Thank your for using alliance bot");
					flow_data.put("title", "Thank you....!");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());

					map.put("screen", "END_SCREEN");
					map.put("version", version);
					map.put("data", flow_data);	
					
				}
				
				
														
				return response = mapper.writeValueAsString(map);
				

			}else if("INPUT_TYPE".equalsIgnoreCase(component_action)) {
				
				String inputType =data.get("input_type")==null?"":data.get("input_type").toString();
				Map<String,Object> inputTypeRes =new HashMap<String, Object>();
				inputTypeRes.put("isInputDataReq", true);
				inputTypeRes.put("isVisibleInputData", true);
				if("1".equals(inputType)) 
					inputTypeRes.put("inputdata_lable_name", "Registration Number");
				else if("2".equals(inputType)) 
					inputTypeRes.put("inputdata_lable_name", "Chassis Number");
					
						
				map.put("screen", screen_name);
				map.put("version", version);
				map.put("data", inputTypeRes);
								
				response =this.mapper.writeValueAsString(map);
				
				return response;
			}else if("KYC_DOCUMENT_UPLOAD".equalsIgnoreCase(component_action)) {
				
				Long upload_transaction_no =data.get("upload_transaction_no")==null?null:Long.valueOf(data.get("upload_transaction_no").toString());
				
				List<Map<String,Object>> listImage =data.get("kyc_documents")==null?Collections.EMPTY_LIST:
					(List<Map<String,Object>>)data.get("kyc_documents");			
			
				log.info("KYC_DOCUMENT_UPLOAD document count is "+listImage.size()+"");
				
				for(Map<String,Object> image : listImage) {
					
					String file_name =image.get("file_name")==null?"":image.get("file_name").toString().trim();
					String media_id =image.get("media_id")==null?"":image.get("media_id").toString();
						
					String file_path =cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path=file_path+file_name;					
					
					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);
					
					FileUtils.writeByteArrayToFile(file, imageArray);
					
					insertPreinspectionData(upload_transaction_no,file_path,file_name,file_name,500);
				}
				
				Map<String,Object> flow_data = new HashMap<String, Object>();
				flow_data.put("header", "Alliance Insurance Corportation Limted");
				flow_data.put("response_text", "Your documents has been received. Thank your for using alliance bot");
				flow_data.put("title", "Thank you....!");
				flow_data.put("upload_transaction_no", upload_transaction_no.toString());


				
				map.put("screen", "END_SCREEN");
				map.put("version", version);
				map.put("data", flow_data);
				
				return response = mapper.writeValueAsString(map);
				
				
			}

			
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> preinspectionScreenData(String mobile_no) {
		try {
			Map<String,Object> flow_data = new HashMap<String, Object>();
			flow_data.put("title", "Welcome");
			flow_data.put("label", "Upload Vehicle Front Image");
			flow_data.put("input_type", PRE_DROPDOWN_DATA);
			flow_data.put("mobile_no", mobile_no);
			flow_data.put("isVisibleInputData",false);
			flow_data.put("isInputDataReq", false);
			flow_data.put("inputdata_lable_name", "Testing");

			Map<String,Object> flow_action_payload =new HashMap<String, Object>();
			flow_action_payload.put("screen", "WELCOME_SCREEN");
			flow_action_payload.put("data", flow_data);
			
			System.out.println(mapper.writeValueAsString(flow_action_payload));
			
			return flow_action_payload;
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	private PreinspectionDataDetail insertPreinspectionData(String input_type,String input_data,String mobile_no,String docType) {
		try {
			Long tranId =preInsDataRepo.getTranId();
			PreinspectionDataDetail pdd =PreinspectionDataDetail.builder()
					.registrationNo("1".equals(input_type)?input_data:null)
					.chassisNo("2".equals(input_type)?input_data:null)
					.entry_date(new Date())
					.status("Y")
					.tranId(tranId)
					.mobileNo(mobile_no)
					.documnetType(docType)
					.build();
			
			PreinspectionDataDetail pddsave=preInsDataRepo.save(pdd);
			return pddsave;
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	private void insertPreinspectionData(Long tran_id,String file_path,String image_name,String file_name,Integer doc_id) {
		try {
			PreinspectionImageDetail pmd =PreinspectionImageDetail.builder()
					.tranId(tran_id)
					.imageFilePath(file_path)
					.imageName(image_name)
					.entry_date(new Date())
					.status("Y")
					.originalFileName(file_name)
					.exifImageStatus("VALID")
					.docId(doc_id)
					.build();
			
			pidiRepo.save(pmd);
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	
	
	public Boolean validateImageFile(String file_type,File file) {
		try {					
			String responseString ="";
			Response response =null;		
			
			// for geting bearer token		
		    RequestBody token_body = new FormBody.Builder()
		            .add("username", python_image_token_username)
		            .add("password", python_image_token_password)
		            .build();
			
			Request token_request = new Request.Builder()
					.url(python_image_token_api)
	                .addHeader("Content-Type", "application/x-www-form-urlencoded")
					.post(token_body)
					.build();
			
			response = okhttp.newCall(token_request).execute();
			responseString = response.body().string();	
			
			@SuppressWarnings("unchecked")
			Map<String,Object> token_result =mapper.readValue(responseString, Map.class);
			String authorization ="Bearer "+token_result.get("access_token").toString();
					
	        // Create multipart body
	        MultipartBody requestBody = new MultipartBody.Builder()
	                .setType(MultipartBody.FORM)
	                .addFormDataPart("file_type", file_type)
	                .addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg")))
	                .build();

	        // Create request
	        Request image_request = new Request.Builder()
	                .url(python_image_validate_api)
	                .post(requestBody)
	                .addHeader("Authorization",authorization)
	                .build();
			
	        response = okhttp.newCall(image_request).execute();
			responseString = response.body().string();
			
			@SuppressWarnings("unchecked")
			Map<String,Object> image_result = mapper.readValue(responseString, Map.class);
			String response_code =image_result.get("Status").toString();
			System.out.println(mapper.writeValueAsString(image_result));

			if("200".equals(response_code)) {
				return false;
			}else {
				return true;
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String shortTermPolicy(Map<String, Object> request) {
		String response = "";
		String api_request = "";
		String api_response = "";
		
		try {
			Map<String,Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version")== null ? "" : request.get("version").toString();
			String screen = request.get("screen")==null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action")==null ? "" : data.get("component_action").toString();
			String flow_token = request.get("flow_token")==null ? "" : request.get("flow_token").toString();
			
			Map<String,Object> return_response = new HashMap<String, Object>();
			return_response.put("version", version);
			return_response.put("screen", screen);
			
			String sample_data = "[ {\"id\": \"0\", \"title\": \"--SELECT--\"} ]";
			String error_messages_1 =" {\"id\": \"\", \"\": \"\"}";
			List<Map<String,Object>> list = mapper.readValue(sample_data, List.class);
			
			String token =this.thread.getEwayToken();

			Map<String,String> input_validation = new HashMap<>();
			if("VEHILCE_VALIDATION".equalsIgnoreCase(component_action)) {
				String chassis_number = data.get("chassis_number")==null ? "" : data.get("chassis_number").toString().trim();
				String body_type = data.get("body_type")==null ? "" : data.get("body_type").toString().trim();
				String registration_number = data.get("registration_number")==null ? "" : data.get("registration_number").toString().trim();
				String engine_number = data.get("engine_number")==null ? "" : data.get("engine_number").toString().trim();
				String vehicle_make = data.get("vehicle_make")==null ? "" : data.get("vehicle_make").toString().trim();
				String vehicle_model = data.get("vehicle_model")==null ? "" : data.get("vehicle_model").toString().trim();
				String engine_capacity = data.get("engine_capacity")==null ? "" : data.get("engine_capacity").toString().trim();
				String manufacture_year = data.get("manufacture_year")==null ? "" : data.get("manufacture_year").toString().trim();
				String fuel_used = data.get("fuel_used")==null ? "" : data.get("fuel_used").toString().trim();
				String motor_category = data.get("motor_category")==null ? "" : data.get("motor_category").toString().trim();
				String vehicle_color = data.get("vehicle_color")==null ? "" : data.get("vehicle_color").toString().trim();
				String vehicle_usage = data.get("vehicle_usage")==null ? "" : data.get("vehicle_usage").toString().trim();
				String seating_capacity = data.get("seating_capacity")==null ? "" : data.get("seating_capacity").toString().trim();
				String tare_weight = data.get("tare_weight")==null ? "" : data.get("tare_weight").toString().trim();
				String gross_weight = data.get("gross_weight")==null ? "" : data.get("gross_weight").toString().trim();
				String no_of_axle = data.get("no_of_axle")==null ? "" : data.get("no_of_axle").toString().trim();
				String axle_distance = data.get("axle_distance")==null ? "" : data.get("axle_distance").toString().trim();
				
				
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name = data.get("customer_name")==null ? "" : data.get("customer_name").toString().trim();
				String country_code = data.get("country_code")==null ? "" : data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number")==null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id")==null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address")==null ? "" : data.get("address").toString().trim();
				String region = data.get("region")==null ? "" : data.get("region").toString().trim();
				
				// validation message must not greater than 30 characters.
				
				if(chassis_number.length()<5) {
					input_validation.put("chassis_number", "Minimum characters required.");
				}else if(!chassis_number.matches("[a-zA-Z0-9]+")) {
					input_validation.put("chassis_number", "Special characters not allowed.");
				}
				if(!registration_number.matches("[a-zA-Z0-9]+")) {
					input_validation.put("registration_number", "Special characters not allowed.");
				}
				if(!engine_capacity.matches("[0-9]+")) {
					input_validation.put("engine_capacity", "digits only allowed");
				}
				if(!engine_number.matches("[0-9]+")) {
					input_validation.put("engine_number", "digits only allowed");
				}
				if(!tare_weight.matches("[0-9]+")) {
					input_validation.put("tare_weight", "digits only allowed");
				}
				else if(!tare_weight.matches("[0-9]+")) {
					input_validation.put("tare_weight", "digits only allowed");
				}
				if(!gross_weight.matches("[0-9]+")) {
					input_validation.put("gross_weight", "digits only allowed");
				}
				else if(!gross_weight.matches("[0-9]+")) {
					input_validation.put("gross_weight", "digits only allowed");
				}
				if(!no_of_axle.matches("[0-9]+")) {
					input_validation.put("no_of_axle", "digits only allowed");
				}
				if(!axle_distance.matches("[0-9]+")) {
					input_validation.put("axle_distance", "digits only allowed");
				}
				if(!seating_capacity.matches("[0-9]+")) {
					input_validation.put("seating_capacity", "digits only allowed");
				}/*else {
					Map<String,String> request_map = new HashMap<String, String>();
					request_map.put("Type", "SeatingCapacity");
					request_map.put("SeatingCapacity", seating_capacity );
					request_map.put("InsuranceId", "100019");
					request_map.put("BranchCode", "55");
					request_map.put("BodyType",body_type );
					String ewayValidation=wh_get_ewaydata_api;
					api_response=thread.callEwayApi(ewayValidation, mapper.writeValueAsString(request_map), token);
					Map<String,Object> map = mapper.readValue(api_response, Map.class);
					Boolean status = (Boolean) map.get("IsError");
					if(status) {
						Map<String,Object> seat_map =(Map<String,Object>) map.get("Result");
						String seats =seat_map.get("SeatingCapacity").toString();
						input_validation.put("seating_capacity", "should be under "+seats+" or equal ");
					}
				*/
				
				// checking validation data
				if(!input_validation.isEmpty() && input_validation.size()>0) {
					Map<String,String> request_map= new HashMap<String,String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					request_map.put("BodyId", body_type);
					request_map.put("MakeId", vehicle_make);
					
					String request_1 = printReq.toJson(request_map);
					
					CompletableFuture<List<Map<String,String>>> fuel_type_e =thread.getFuelType(request_1,token);
					CompletableFuture<List<Map<String,String>>> color_e =thread.getColor(request_1,token);
					CompletableFuture<List<Map<String,String>>> manufacture_year_e =thread.getManuFactureYear();
					CompletableFuture<List<Map<String,String>>> body_type_e =thread.getSTPBodyType(request_1,token);
					CompletableFuture<List<Map<String,String>>> vehicle_usage_e =thread.getSTPVehicleUsage(request_1,token);
					CompletableFuture<List<Map<String,String>>> make_e =thread.getStpMake(token, body_type);
					//CompletableFuture<List<Map<String,String>>> model_e =thread.getSTPModel(body_type,vehicle_make,token);
					
					CompletableFuture.allOf(fuel_type_e,color_e,manufacture_year_e,
							body_type_e,vehicle_usage_e,make_e).join();
					
					Map<String,Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("body_type", body_type_e.get().isEmpty() ? SAMPLE_DATA : body_type_e.get());
					error_messages.put("body_make", make_e.get().isEmpty() ? SAMPLE_DATA : make_e.get());
					error_messages.put("vehicle_model", vehicle_model);
					error_messages.put("manufacture_year", manufacture_year_e.get().isEmpty() ? SAMPLE_DATA : manufacture_year_e.get());
					error_messages.put("fuel_used", fuel_type_e.get().isEmpty() ? SAMPLE_DATA : fuel_type_e.get());
					error_messages.put("vehicle_usage", vehicle_usage_e.get().isEmpty() ? SAMPLE_DATA : vehicle_usage_e.get());
					error_messages.put("vehicle_color", color_e.get().isEmpty() ? SAMPLE_DATA : color_e.get());
					
					error_messages.put("title", title);
					error_messages.put("customer_name", customer_name);
					error_messages.put("country_code", country_code);
					error_messages.put("mobile_number", mobile_number);
					error_messages.put("email_id", email_id);
					error_messages.put("address", address);
					error_messages.put("region", region);
					error_messages.put("axle_distance", axle_distance);
					error_messages.put("no_of_axle", no_of_axle);
					error_messages.put("gross_weight", gross_weight);
					error_messages.put("tare_weight", tare_weight);
					error_messages.put("registration_number", registration_number);
					error_messages.put("engine_number", engine_number);
					error_messages.put("chassis_number", chassis_number);
					error_messages.put("engine_capacity", engine_capacity);
					
					return_response.put("data", error_messages);
					response=printReq.toJson(return_response);
					return response;
				}
				else {
					Map<String,Object> map_policy = new HashMap<String, Object>();
					Map<String,String> save_details =new HashMap<String, String>();
					
                   
					
					save_details.put("Insuranceid", "100019");
					save_details.put("BranchCode", "55");
					save_details.put("AxelDistance", axle_distance);
					save_details.put("Chassisnumber", chassis_number);
					save_details.put("Color", vehicle_color);
					save_details.put("CreatedBy", "ugandabroker3");
					save_details.put("EngineNumber", engine_number);
					save_details.put("FuelType",fuel_used );
					save_details.put("Grossweight", gross_weight);
					save_details.put("ManufactureYear", manufacture_year);
					save_details.put("MotorCategory", motor_category);
					save_details.put("Motorusage", vehicle_usage);
					save_details.put("NumberOfAxels", no_of_axle);
					save_details.put("OwnerCategory", "1");
					save_details.put("Registrationnumber", registration_number);
					save_details.put("ResEngineCapacity", engine_capacity);
					save_details.put("ResOwnerName", "Testing");
					save_details.put("ResStatusCode", "Y");
					save_details.put("ResStatusDesc", "None");
					save_details.put("SeatingCapacity", seating_capacity);
					save_details.put("Tareweight", tare_weight);
					save_details.put("Vehcilemodel", vehicle_model);
					save_details.put("VehicleType", body_type);
					save_details.put("Vehiclemake", vehicle_make);
					save_details.put("RegistrationDate", new Date().toString());
					
					
					
					String saveVehicle=wh_save_vehicle_info_api;
					api_response=thread.callEwayApi(saveVehicle, mapper.writeValueAsString(save_details), token);
					Map<String,Object> map = mapper.readValue(api_response, Map.class);
					String status = map.get("Message").toString();
					if("Success".equalsIgnoreCase(status)) {
						
						Map<String,String> request_map= new HashMap<String,String>();
						request_map.put("BranchCode", "55");
						request_map.put("InsuranceId", "100019");
						
						String request_1 = printReq.toJson(request_map);
						
						CompletableFuture<List<Map<String,String>>> insurance_type_1 =thread.getInsuranceType(body_type,vehicle_make,token);
						CompletableFuture<List<Map<String,String>>> insurance_class_1 =thread.getInsuranceClass(token);
						CompletableFuture<List<Map<String,String>>> body_type_policy_1 =thread.getSTPBodyType(request_1,token);
						CompletableFuture<List<Map<String,String>>> vehicle_usage_policy_1 =thread.getSTPVehicleUsage(request_1,token);
						
						
						CompletableFuture.allOf(insurance_type_1,insurance_class_1,body_type_policy_1,vehicle_usage_policy_1).join();
						
						/*map_policy.put("chassis_number", chassis_number);
						map_policy.put("body_type", body_type);
						map_policy.put("registration_number", registration_number);
						map_policy.put("engine_number", engine_number);
						map_policy.put("vehicle_make", vehicle_make);
						map_policy.put("engine_capacity", engine_capacity);
						map_policy.put("manufacture_year", manufacture_year);
						map_policy.put("fuel_used", fuel_used);
						map_policy.put("vehicle_model", vehicle_model);
						map_policy.put("motor_category", motor_category);
						map_policy.put("vehicle_color", vehicle_color);
						map_policy.put("vehicle_usage", vehicle_usage);
						map_policy.put("seating_capacity", seating_capacity);
						map_policy.put("tare_weight", tare_weight);
						map_policy.put("gross_weight", gross_weight);
						map_policy.put("no_of_axle", no_of_axle);
						map_policy.put("axle_distance", axle_distance);
						map_policy.put("flow_token", flow_token);*/
						
						map_policy.put("title", title);
						map_policy.put("customer_name", customer_name);
						map_policy.put("country_code", country_code);
						map_policy.put("mobile_number", mobile_number);
						map_policy.put("email_id", email_id);
						map_policy.put("address", address);
						map_policy.put("region", region);
						
						map_policy.put("insurance_class", insurance_class_1.get().isEmpty()?list:insurance_class_1.get());
						map_policy.put("body_type_policy", body_type_policy_1.get().isEmpty()?list:body_type_policy_1.get());
						map_policy.put("vehicle_usage_policy", vehicle_usage_policy_1.get().isEmpty()?list:vehicle_usage_policy_1.get());
						map_policy.put("insurance_type",  insurance_type_1.get().isEmpty()?list:insurance_type_1.get());
						map_policy.put("isMandatoryBrokerLoginId", false);
						map_policy.put("isVisibleBrokerLoginId", false);
						map_policy.put("isVehicle_si", false);
						map_policy.put("isAccessories_si", false);
						map_policy.put("isWindshield_si", false);
						map_policy.put("extended_TPDD_si", false);
						map_policy.put("isGas", false);
						map_policy.put("isCarAlarm", false);
						map_policy.put("required_vehicle_si", false);
						map_policy.put("required_windshield_si", false);
						map_policy.put("required_accessories_si", false);
						map_policy.put("required_TPDD_si", false);
						map_policy.put("required_gps", false);
						map_policy.put("required_car_alarm", false);
						
						
						
						
						Map<String,String> mapPolicy = new HashMap<>();
						map_policy.put("error_messages", mapPolicy);
						
						return_response.put("data", map_policy);
						return_response.put("screen", "POLICY_DETAILS");
						response =printReq.toJson(return_response);

						log.info("response"+ response);
					}
				    /*
					 * return_response.put("screen", "SUCCESS"); return_response.put("data",
					 * extension_message_response); response =printReq.toJson(return_response);
					 */				
				}
				return response;
			}
			if("VEHILCE_REG_VALIDATION".equalsIgnoreCase(component_action)) {
				
				String reg_no= data.get("reg_no")==null?"":data.get("reg_no").toString().trim();
				
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name = data.get("customer_name")==null ? "" : data.get("customer_name").toString().trim();
				String country_code = data.get("country_code")==null ? "" : data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number")==null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id")==null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address")==null ? "" : data.get("address").toString().trim();
				String region = data.get("region")==null ? "" : data.get("region").toString().trim();
				
				/*String insurance_class =data.get("insurance_class")==null?"":data.get("insurance_class").toString().trim();
				String body_type_policy = data.get("body_type_policy")==null ? "" : data.get("body_type_policy").toString().trim();
				String vehicle_usage_policy = data.get("vehicle_usage_policy")==null ? "" : data.get("vehicle_usage_policy").toString().trim();
				String gps = data.get("gps")==null ? "" : data.get("gps").toString().trim();
				String insurance_type = data.get("insurance_type")==null ? "" : data.get("insurance_type").toString().trim();
				String car_alarm = data.get("car_alarm")==null ? "" : data.get("car_alarm").toString().trim();
				String insurance_claim = data.get("insurance_claim")==null ? "" : data.get("insurance_claim").toString().trim();
				String quatation_creator = data.get("quatation_creator")==null ? "" : data.get("quatation_creator").toString().trim();
				String isMandatoryBrokerLoginId = data.get("isMandatoryBrokerLoginId")==null ? "" : data.get("isMandatoryBrokerLoginId").toString().trim();
				String isVisibleBrokerLoginId = data.get("isVisibleBrokerLoginId")==null ? "" : data.get("isVisibleBrokerLoginId").toString().trim();
				String isVehicle_si = data.get("isVehicle_si")==null ? "" : data.get("isVehicle_si").toString().trim();
				String isAccessories_si = data.get("isAccessories_si")==null ? "" : data.get("isAccessories_si").toString().trim();
				String isWindshield_si = data.get("isWindshield_si")==null ? "" : data.get("isWindshield_si").toString().trim();
				String extended_TPDD_si = data.get("extended_TPDD_si")==null ? "" : data.get("extended_TPDD_si").toString().trim();
				String isGas = data.get("isGas")==null ? "" : data.get("isGas").toString().trim();
				String isCarAlarm = data.get("isCarAlarm")==null ? "" : data.get("isCarAlarm").toString().trim();
				String required_vehicle_si = data.get("isAccessories_si")==null ? "" : data.get("isAccessories_si").toString().trim();
				String required_windshield_si = data.get("isWindshield_si")==null ? "" : data.get("isWindshield_si").toString().trim();
				String required_accessories_si = data.get("extended_TPDD_si")==null ? "" : data.get("extended_TPDD_si").toString().trim();
				String required_TPDD_si = data.get("isGas")==null ? "" : data.get("isGas").toString().trim();
				String required_gps = data.get("isCarAlarm")==null ? "" : data.get("isCarAlarm").toString().trim();
				String required_car_alarm = data.get("isAccessories_si")==null ? "" : data.get("isAccessories_si").toString().trim();
				*/
				
				if(!reg_no.matches("[a-zA-Z0-9]+")) {
					input_validation.put("reg_no", "Special characters not allowed.");
				}
				
				
				Map<String,String> reg_validatation = new HashMap<String,String>();
				reg_validatation.put("InsuranceId", "100019");
				reg_validatation.put("BranchCode", "55");
				reg_validatation.put("BrokerBranchCode", "1");
				reg_validatation.put("ProductId", "5");
				reg_validatation.put("CreatedBy", "ugandabroker3");
				reg_validatation.put("SavedFrom", "API");
				reg_validatation.put("ReqRegNumber", reg_no);
				reg_validatation.put("ReqChassisNumber", "");
				
				String reg_noValidationApi =wh_get_reg_no_api;					
				api_response =thread.callEwayApi(reg_noValidationApi, mapper.writeValueAsString(reg_validatation),token);
				Map<String,Object> map =mapper.readValue(api_response, Map.class);
				Boolean status =(Boolean)map.get("IsError");
				
				if(status) {
					input_validation.put("reg_no", "reg no not found");
					
					Map<String,Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
									
					return_response.put("action", "data_exchange");
					return_response.put("data", error_messages);
					
					response =printReq.toJson(return_response);
					return response;
				}else {
					
					
					if(input_validation.size()>0) {
						Map<String,String> request_map= new HashMap<String,String>();
						request_map.put("BranchCode", "55");
						request_map.put("InsuranceId", "100019");
						
						String request_1 =printReq.toJson(request_map);
						
						Map<String,Object> error_messages = new HashMap<String, Object>();
						error_messages.put("title", title);
						error_messages.put("customer_name", customer_name);
						error_messages.put("country_code", country_code);
						error_messages.put("mobile_number", mobile_number);
						error_messages.put("email_id", email_id);
						error_messages.put("address", address);
						error_messages.put("region", region);
						error_messages.put("reg_no", reg_no);
						
						response =printReq.toJson(return_response);
					}
					else {
						String bodyType=map.get("VehicleType").toString();
						String vehicleUsage=map.get("Motorusage").toString();
						Map<String,String> request_map= new HashMap<String,String>();
						request_map.put("BranchCode", "55");
						request_map.put("InsuranceId", "100019");
						request_map.put("BodyId", bodyType);
																					
						String request_1 =printReq.toJson(request_map);
						
						CompletableFuture<List<Map<String,String>>> insurance_type_1 =thread.getInsuranceType(bodyType,vehicleUsage,token);
						CompletableFuture<List<Map<String,String>>> insurance_class_1 =thread.getInsuranceClass(token);
						CompletableFuture<List<Map<String,String>>> body_type_policy_1 =thread.getSTPBodyType(request_1,token);
						CompletableFuture<List<Map<String,String>>> vehicle_usage_policy_1 =thread.getSTPVehicleUsage(request_1,token);
				
						CompletableFuture.allOf(insurance_type_1,insurance_class_1,body_type_policy_1,vehicle_usage_policy_1).join();
						Map<String,Object> map_vehicle = new HashMap<String, Object>();
						
						map_vehicle.put("title", title);
						map_vehicle.put("customer_name", customer_name);
						map_vehicle.put("mobile_number", mobile_number);
						map_vehicle.put("email_id", email_id);
						map_vehicle.put("address", address);
						map_vehicle.put("region", region);
						map_vehicle.put("country_code", country_code);
						map_vehicle.put("reg_no", reg_no);
						map_vehicle.put("insurance_class", insurance_class_1.get().isEmpty()?list:insurance_class_1.get());
						map_vehicle.put("body_type_policy", body_type_policy_1.get().isEmpty()?list:body_type_policy_1.get());
						map_vehicle.put("vehicle_usage_policy", vehicle_usage_policy_1.get().isEmpty()?list:vehicle_usage_policy_1.get());
						//map_vehicle.put("gps", SAMPLE_DATA);
						map_vehicle.put("insurance_type",  insurance_type_1.get().isEmpty()?list:insurance_type_1.get());
						//map_vehicle.put("car_alarm", SAMPLE_DATA);
						
						//map_vehicle.put("insurance_claim", SAMPLE_DATA);
						//map_vehicle.put("quatation_creator", SAMPLE_DATA);
						map_vehicle.put("isMandatoryBrokerLoginId", false);
						map_vehicle.put("isVisibleBrokerLoginId", false);
						map_vehicle.put("isVehicle_si", false);
						map_vehicle.put("isAccessories_si", false);
						map_vehicle.put("isWindshield_si", false);
						map_vehicle.put("extended_TPDD_si", false);
						map_vehicle.put("isGas", false);
						map_vehicle.put("isCarAlarm", false);
						map_vehicle.put("required_vehicle_si", false);
						map_vehicle.put("required_windshield_si", false);
						map_vehicle.put("required_accessories_si", false);
						map_vehicle.put("required_TPDD_si", false);
						map_vehicle.put("required_gps", false);
						map_vehicle.put("required_car_alarm", false);
						
						
						Map<String,String> errorMap = new HashMap<>();
						map_vehicle.put("error_messages", errorMap);
						
						return_response.put("data", map_vehicle);
						return_response.put("screen", "POLICY_DETAILS");
						response =printReq.toJson(return_response);

						log.info("response"+ response);

					}			
				}
				return response;
			
			}
			if("quotation_creator".equalsIgnoreCase(component_action)) {
				String is_broker = data.get("quotation_creator")==null ? "" : data.get("quotation_creator").toString().trim();
				Map<String,Boolean> enableLogin =new HashMap<String, Boolean>();
				if("1".equalsIgnoreCase(is_broker)) {
					enableLogin.put("isVisibleBrokerLoginId", true);
					enableLogin.put("isMandatoryBrokerLoginId", true);
				}else if("2".equalsIgnoreCase(is_broker)){
					enableLogin.put("isVisibleBrokerLoginId", false);
					enableLogin.put("isMandatoryBrokerLoginId", false);
				}
				
				return_response.put("data", enableLogin);
				response =printReq.toJson(return_response);
				return response;
			}
			else if ("MAKE".equalsIgnoreCase(component_action)) {
				
				String body_type =data.get("body_type")==null?"":data.get("body_type").toString().trim();
				List<Map<String,String>> data_list = new ArrayList<Map<String,String>>();
				
				if(StringUtils.isNotBlank(body_type)) {
					String api =this.stpMake;
					
					Map<String,Object> region_req =new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100019");
					region_req.put("BranchCode", "55");
					
					api_request =printReq.toJson(region_req);
					
					api_response =thread.callEwayApi(api, api_request,token);
					
					Map<String,Object> region_obj =mapper.readValue(api_response, Map.class);
					List<Map<String,Object>> result =(List<Map<String,Object>>)region_obj.get("Result");
					
					data_list = result.stream().map(p->{
						Map<String,String> map = new HashMap<>();
						map.put("id", p.get("Code")==null?"":p.get("Code").toString());
						map.put("title", p.get("CodeDesc")==null?"":p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				}
				else {
					data_list =SAMPLE_DATA;
				}
				Map<String,Object> make_list = new HashMap<String, Object>();
				make_list.put("vehicle_make", data_list);
				return_response.put("data", make_list);
				response =printReq.toJson(return_response);
				return response;
			}
			else if("MODEL".equalsIgnoreCase(component_action)) {
				String body_type =data.get("body_type")==null?"":data.get("body_type").toString().trim();
				String make =data.get("body_make")==null?"":data.get("body_make").toString().trim();
				List<Map<String,String>> data_list = new ArrayList<Map<String,String>>();
				
				if(!"00000".equals(make) && StringUtils.isNotBlank(make) ) {
					String api =this.stpMakeModel;
					
					Map<String,Object> region_req =new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100019");
					region_req.put("BranchCode", "55");
					region_req.put("MakeId", make);
					
					api_request =printReq.toJson(region_req);
					
					api_response =thread.callEwayApi(api, api_request,token);
					
					Map<String,Object> region_obj =mapper.readValue(api_response, Map.class);
					List<Map<String,Object>> result =(List<Map<String,Object>>)region_obj.get("Result");
					
					data_list = result.stream().map(p->{
						Map<String,String> map = new HashMap<>();
						map.put("id", p.get("Code")==null?"":p.get("Code").toString());
						map.put("title", p.get("CodeDesc")==null?"":p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				}else {
					data_list =SAMPLE_DATA;
				}
				Map<String,Object> make_list = new HashMap<String, Object>();
				make_list.put("make", data_list);
				return_response.put("data", make_list);
				response =printReq.toJson(return_response);
				return response;
			}
			/*else if ("INSURANCE".equalsIgnoreCase(component_action)) {
				String body_type_policy =data.get("body_type_policy")==null?"":data.get("body_type_policy").toString().trim();
				String vehicle_usage_policy =data.get("vehicle_usage_policy")==null?"":data.get("vehicle_usage_policy").toString().trim();
				
				List<Map<String,String>> data_list = new ArrayList<Map<String,String>>();
				
				if(!"00000".equals(vehicle_usage_policy) && StringUtils.isNotBlank(vehicle_usage_policy) ) {
					String api =this.wh_cq_policytype;
					
					Map<String,Object> policy_req = new HashMap<String,Object>();
					policy_req.put("BodyId", body_type_policy);
					policy_req.put("InsuranceId", "100019");
					policy_req.put("BranchCode", "BranchCode");
					policy_req.put("Motorusage", vehicle_usage_policy);
					
					api_request =printReq.toJson(policy_req);
					
					api_response =thread.callEwayApi(api, api_request,token);
					
					Map<String,Object> region_obj =mapper.readValue(api_response, Map.class);
					List<Map<String,Object>> result =(List<Map<String,Object>>)region_obj.get("Result");
					
					data_list = result.stream().map(p->{
						Map<String,String> map = new HashMap<>();
						map.put("id", p.get("Code")==null?"":p.get("Code").toString());
						map.put("title", p.get("CodeDesc")==null?"":p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				}else {
					data_list =SAMPLE_DATA;
				}
				Map<String,Object> make_list = new HashMap<String, Object>();
				make_list.put("make", data_list);
				return_response.put("data", make_list);
				response =printReq.toJson(return_response);
				return response;
				}*/
			
			else if("POLICY_VALIDATION".equalsIgnoreCase(component_action)) {
				String quatation_creator = data.get("quatation_creator")==null ? "" : data.get("quatation_creator").toString().trim();
				String broker_loginid = data.get("broker_loginid")==null ? "" : data.get("broker_loginid").toString().trim();
				String insurance_type = data.get("insurance_type")==null ? "" : data.get("insurance_type").toString().trim();
				String insurance_class = data.get("insurance_class")==null ? "" : data.get("insurance_class").toString().trim();
				String body_type_policy = data.get("body_type_policy")==null ? "" : data.get("body_type_policy").toString().trim();
				String vehicle_usage_policy = data.get("vehicle_usage_policy")==null ? "" : data.get("vehicle_usage_policy").toString().trim();
				String gps = data.get("gps")==null ? "" : data.get("gps").toString().trim();
				String car_alarm = data.get("car_alarm")==null ? "" : data.get("car_alarm").toString().trim();
				String insurance_claim = data.get("insurance_claim")==null ? "" : data.get("insurance_claim").toString().trim();
				String vehicle_si = data.get("vehicle_si")==null ? "" : data.get("vehicle_si").toString().trim();
				String accessories_si = data.get("accessories_si")==null ? "" : data.get("accessories_si").toString().trim();
				String windshield_si = data.get("windshield_si")==null ? "" : data.get("windshield_si").toString().trim();
				String extended_TPDD_si = data.get("extended_TPDD_si")== null?"" : data.get("extended_TPDD_si").toString().trim();
				
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name = data.get("customer_name")==null ? "" : data.get("customer_name").toString().trim();
				String country_code = data.get("country_code")==null ? "" : data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number")==null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id")==null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address")==null ? "" : data.get("address").toString().trim();
				String region = data.get("region")==null ? "" : data.get("region").toString().trim();
				
				String chassis_number = data.get("chassis_number")==null ? "" : data.get("chassis_number").toString().trim();
				String body_type = data.get("body_type")==null ? "" : data.get("body_type").toString().trim();
				String registration_number = data.get("registration_number")==null ? "" : data.get("registration_number").toString().trim();
				String engine_number = data.get("engine_number")==null ? "" : data.get("engine_number").toString().trim();
				String body_make = data.get("body_make")==null ? "" : data.get("body_make").toString().trim();
				String vehicle_model = data.get("vehicle_model")==null ? "" : data.get("vehicle_model").toString().trim();
				String engine_capacity = data.get("engine_capacity")==null ? "" : data.get("engine_capacity").toString().trim();
				String manufacture_year = data.get("manufacture_year")==null ? "" : data.get("manufacture_year").toString().trim();
				String fuel_used = data.get("fuel_used")==null ? "" : data.get("fuel_used").toString().trim();
				String motor_category = data.get("motor_category")==null ? "" : data.get("motor_category").toString().trim();
				String vehicle_color = data.get("vehicle_color")==null ? "" : data.get("vehicle_color").toString().trim();
				String vehicle_usage = data.get("vehicle_usage")==null ? "" : data.get("vehicle_usage").toString().trim();
				String seating_capacity = data.get("seating_capacity")==null ? "" : data.get("seating_capacity").toString().trim();
				String tare_weight = data.get("tare_weight")==null ? "" : data.get("tare_weight").toString().trim();
				String gross_weight = data.get("gross_weight")==null ? "" : data.get("gross_weight").toString().trim();
				String no_of_axle = data.get("no_of_axle")==null ? "" : data.get("no_of_axle").toString().trim();
				String axle_distance = data.get("axle_distance")==null ? "" : data.get("axle_distance").toString().trim();

				
				
				
				if("1".equalsIgnoreCase(quatation_creator)) {
					Map<String,String> request_map = new HashMap<String,String>();
					request_map.put("Type", "LOGIN_ID_CHECK");
					request_map.put("LoginId", broker_loginid);
					String ewayValidation=wh_get_ewaydata_api;
					api_response=thread.callEwayApi(ewayValidation, mapper.writeValueAsString(request_map), token);
					Map<String,Object> map = mapper.readValue(api_response, Map.class);
					Boolean status = (Boolean) map.get("IsError");
					
					if(status) {
						input_validation.put("broker_loginid", "Broker Login Id not valid");
					}
				}
				if(insurance_class != null) {
				Map<String,Object> hide_suminsured = new HashMap<String, Object>();
				if("1".equals(insurance_class) || "2".equals(insurance_class)) {
					hide_suminsured.put("vehicle_si", true);
					hide_suminsured.put("required_vehicle_si", true);
					hide_suminsured.put("accessories_si", true);
					hide_suminsured.put("required_accessories_si", true);
					hide_suminsured.put("windshield_si", true);
					hide_suminsured.put("required_windshield_si", true);
					hide_suminsured.put("extended_TPDD_si", true);
					hide_suminsured.put("required_TPDD_si", true);
					hide_suminsured.put("isGas", true);
					hide_suminsured.put("required_gps", true);
					hide_suminsured.put("isCarAlarm", true);
					hide_suminsured.put("required_car_alarm", true);
				}else {
					hide_suminsured.put("vehicle_si", false);
					hide_suminsured.put("required_vehicle_si", false);
					hide_suminsured.put("accessories_si", false);
					hide_suminsured.put("required_accessories_si", false);
					hide_suminsured.put("windshield_si", false);
					hide_suminsured.put("required_windshield_si", false);
					hide_suminsured.put("extended_TPDD_si", false);
					hide_suminsured.put("required_TPDD_si", false);
					hide_suminsured.put("isGas", false);
					hide_suminsured.put("required_gps", false);
					hide_suminsured.put("isCarAlarm", false);
					hide_suminsured.put("required_car_alarm", false);
				}
				
				return_response.put("data", hide_suminsured);
				
				response =printReq.toJson(return_response);
				return response;
			}
				//validation check
				if(!input_validation.isEmpty() && input_validation.size()>0) {
					
					Map<String,String> request_map= new HashMap<String,String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					request_map.put("BodyId", body_type_policy);
																				
					String request_1 =printReq.toJson(request_map);
					
					
					
					CompletableFuture<List<Map<String,String>>> insurance_type_1 =thread.getInsuranceType(body_type_policy,vehicle_usage_policy,token);
					CompletableFuture<List<Map<String,String>>> insurance_class_1 =thread.getInsuranceClass(token);
					CompletableFuture<List<Map<String,String>>> body_type_policy_1 =thread.getSTPBodyType(request_1,token);
					CompletableFuture<List<Map<String,String>>> vehicle_usage_policy_1 =thread.getSTPVehicleUsage(request_1,token);
					
					CompletableFuture.allOf(body_type_policy_1,vehicle_usage_policy_1).join();
					
					Map<String,Object> error_messages = new HashMap<String,Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("insurance_type", insurance_type_1.get().isEmpty()? SAMPLE_DATA :insurance_type_1.get());
					error_messages.put("insurance_class", insurance_class_1.get().isEmpty()?SAMPLE_DATA:insurance_class_1.get());
					error_messages.put("body_type_policy", body_type_policy_1.get().isEmpty()?SAMPLE_DATA:body_type_policy_1.get());
					error_messages.put("vehicle_usage_policy", vehicle_usage_policy_1.get().isEmpty()?SAMPLE_DATA:vehicle_usage_policy_1.get());
					error_messages.put("gps", gps);
					error_messages.put("car_alarm", car_alarm);
					error_messages.put("insurance_claim", insurance_claim);
					error_messages.put("vehicle_si", vehicle_si);
					error_messages.put("accessories_si", accessories_si);
					error_messages.put("windshield_si", windshield_si);
					error_messages.put("extended_TPDD_si", extended_TPDD_si);
					
					return_response.put("action", "data_exchange");
					return_response.put("data", error_messages);
					
					response =printReq.toJson(return_response);														
				}
				else {
					Map<String,Object> extension_message_response =new HashMap<String, Object>();
					Map<String,Object> params =new HashMap<String, Object>();
					Map<String,Object> param_map =new HashMap<String, Object>();
						
					
					params.put("title", title);
					params.put("customer_name", customer_name);
					params.put("mobile_number", mobile_number);
					params.put("email_id", email_id);
					params.put("address", address);
					params.put("region", region);
					params.put("country_code", country_code);
					params.put("new_registration", "create new vehicle");
					params.put("search_heading", "Search for your vehicle by Registration Number here.");
					
					params.put("chassis_number", chassis_number);
					params.put("body_type", body_type);
					params.put("registration_number", registration_number);
					params.put("engine_number", engine_number);
					params.put("body_make", body_make);
					params.put("engine_capacity", engine_capacity);
					params.put("manufacture_year", manufacture_year);
					params.put("fuel_used", fuel_used);
					params.put("vehicle_model", vehicle_model);
					params.put("motor_category", motor_category);
					params.put("vehicle_color", vehicle_color);
					params.put("vehicle_usage", vehicle_usage);
					params.put("seating_capacity", seating_capacity);
					params.put("tare_weight", tare_weight);
					params.put("gross_weight", gross_weight);
					params.put("no_of_axle", no_of_axle);
					params.put("axle_distance", axle_distance);
					params.put("flow_token", flow_token);
					
					params.put("quatation_creator", quatation_creator);
					params.put("broker_loginid", broker_loginid);
					params.put("insurance_type", insurance_type);
					params.put("insurance_class", insurance_class);
					params.put("body_type_policy", body_type_policy);
					params.put("vehicle_usage_policy", vehicle_usage_policy);
					params.put("gps", gps);
					params.put("car_alarm", car_alarm);
					params.put("insurance_claim", insurance_claim);
					params.put("vehicle_si", vehicle_si);
					params.put("accessories_si", accessories_si);
					params.put("windshield_si", windshield_si);
					params.put("extended_TPDD_si", extended_TPDD_si);
					
					param_map.put("params", params);
					extension_message_response.put("extension_message_response", param_map);
					
					
					return_response.put("screen", "SUCCESS");
					return_response.put("data", extension_message_response);
					
					response =printReq.toJson(return_response);
					
					
				}
			}
			else if("CUSTOMER_SCREEN".equalsIgnoreCase(component_action)) {
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name = data.get("customer_name")==null ? "" : data.get("customer_name").toString().trim();
				String country_code = data.get("country_code")==null ? "" : data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number")==null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id")==null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address")==null ? "" : data.get("address").toString().trim();
				String region = data.get("region")==null ? "" : data.get("region").toString().trim();
				
				if(!customer_name.matches("[a-zA-Z]+")) {
					input_validation.put("customer_name", "Please enter valid name");
				}
				if(!mobile_number.matches("[0-9]+")) {
					input_validation.put("mobile_number", "Please enter valid mobile number");
				}else if(!mobile_number.matches("0?[0-9]{9}")) {
					input_validation.put("mobile_number", "MobileNo should be 9 digits");
				}
				if(!email_id.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
					input_validation.put("email_id", "Please enter valid email format");
				}
				
				if(input_validation.size()>0) {
					Map<String,String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					
					String request_1 =printReq.toJson(request_map);
					
					CompletableFuture<List<Map<String,String>>> title_1 =thread.getCustomerTitle(request_1,token);
					CompletableFuture<List<Map<String,String>>> country_code_1 =thread.getCustomerCountryCode(request_1,token);
					CompletableFuture<List<Map<String,String>>> region_1 =thread.getCustomerRegion(token);
					
					CompletableFuture.allOf(title_1,country_code_1,region_1).join();
					
					Map<String,Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("title", title_1.get().isEmpty()?SAMPLE_DATA:title_1.get());
					error_messages.put("country_code", country_code_1.get().isEmpty()?SAMPLE_DATA:country_code_1.get());
					error_messages.put("region", region_1.get().isEmpty()?SAMPLE_DATA:region_1.get());
					return_response.put("action", "data_exchange");
					return_response.put("data", error_messages);
					
					response =printReq.toJson(return_response);
				}
				else {
					/*Map<String,String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "01");
					request_map.put("InsuranceId", "100002");
					
					String request_1 =printReq.toJson(request_map);
					
					CompletableFuture<List<Map<String,String>>> fuel_type =thread.getFuelType(request_1,token);
					CompletableFuture<List<Map<String,String>>> color =thread.getColor(request_1,token);
					CompletableFuture<List<Map<String,String>>> manufacture_year =thread.getManuFactureYear();
					CompletableFuture<List<Map<String,String>>> body_type =thread.getSTPBodyType(request_1,token);
					CompletableFuture<List<Map<String,String>>> vehicle_usage =thread.getSTPVehicleUsage(request_1,token);
					CompletableFuture<List<Map<String,String>>> motor_category =thread.getMotorCategory(request_1,token);
				

					CompletableFuture.allOf(fuel_type,color,manufacture_year,
							body_type,vehicle_usage).join();*/
					
					Map<String,Object> map_vehicle = new HashMap<String, Object>();
					
					
					map_vehicle.put("new_registration", "Create new vehicle");
					map_vehicle.put("search_heading", "Search for your vehicle by Registration Number here.");
					
					map_vehicle.put("title", title);
					map_vehicle.put("customer_name", customer_name);
					map_vehicle.put("mobile_number", mobile_number);
					map_vehicle.put("email_id", email_id);
					map_vehicle.put("address", address);
					map_vehicle.put("region", region);
					map_vehicle.put("country_code", country_code);
					//map_vehicle.put("fuel_used", fuel_type.get().isEmpty()?list:fuel_type.get());
					//map_vehicle.put("body_type", body_type.get().isEmpty()?list:body_type.get());
					//map_vehicle.put("body_make", list);
					//map_vehicle.put("vehicle_model", list);
					//map_vehicle.put("manufacture_year", manufacture_year.get().isEmpty()?list:manufacture_year.get());
					//map_vehicle.put("vehicle_color", color.get().isEmpty()?list:color.get());
					////map_vehicle.put("vehicle_usage", vehicle_usage.get().isEmpty()?list:vehicle_usage.get());
				    //map_vehicle.put("motor_category", motor_category.get().isEmpty()?list:motor_category.get());
					//map_vehicle.put("isVisibleBrokerLoginId", false);
					//map_vehicle.put("isMandatoryBrokerLoginId", false);
					Map<String,String> map = new HashMap<>();
					map_vehicle.put("error_messages", map);
					
					
					return_response.put("data", map_vehicle);
					return_response.put("screen", "VEHICLE_DETAILS");
					response =printReq.toJson(return_response);

					log.info("response"+ response);
				}
				return response;
			}else if("CREATE_VEHICLE".equalsIgnoreCase(component_action)) {
				
				
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name = data.get("customer_name")==null ? "" : data.get("customer_name").toString().trim();
				String country_code = data.get("country_code")==null ? "" : data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number")==null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id")==null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address")==null ? "" : data.get("address").toString().trim();
				String region = data.get("region")==null ? "" : data.get("region").toString().trim();
				String reg_no = data.get("reg_no")==null ? "" : data.get("reg_no").toString().trim();
				
				Map<String,Object> map_newVehicle = new HashMap<String, Object>();
				
				Map<String,String> request_map = new HashMap<String, String>();
				request_map.put("BranchCode", "55");
				request_map.put("InsuranceId", "100019");
				
				String request_1 =printReq.toJson(request_map);
				
				CompletableFuture<List<Map<String,String>>> fuel_type =thread.getFuelType(request_1,token);
				CompletableFuture<List<Map<String,String>>> color =thread.getColor(request_1,token);
				CompletableFuture<List<Map<String,String>>> manufacture_year =thread.getManuFactureYear();
				CompletableFuture<List<Map<String,String>>> body_type =thread.getSTPBodyType(request_1,token);
				CompletableFuture<List<Map<String,String>>> vehicle_usage =thread.getSTPVehicleUsage(request_1,token);
				CompletableFuture<List<Map<String,String>>> motor_category =thread.getMotorCategory(request_1,token);
				//CompletableFuture<List<Map<String,String>>> make_e =thread.getStpMake(token, body_type);
			

				CompletableFuture.allOf(fuel_type,color,manufacture_year,
						body_type,vehicle_usage).join();
				
				map_newVehicle.put("fuel_used",  fuel_type.get().isEmpty()?list:fuel_type.get());
				map_newVehicle.put("body_type",  body_type.get().isEmpty()?list:body_type.get());
				map_newVehicle.put("vehicle_make", list);
				map_newVehicle.put("manufacture_year",  manufacture_year.get().isEmpty()?list:manufacture_year.get());
				map_newVehicle.put("vehicle_color", color.get().isEmpty()?list:color.get());
				map_newVehicle.put("vehicle_usage",  vehicle_usage.get().isEmpty()?list:vehicle_usage.get());
				map_newVehicle.put("motor_category",  motor_category.get().isEmpty()?list:motor_category.get());
				
				map_newVehicle.put("title", title);
				map_newVehicle.put("customer_name", customer_name);
				map_newVehicle.put("mobile_number", mobile_number);
				map_newVehicle.put("email_id", email_id);
				map_newVehicle.put("address", address);
				map_newVehicle.put("region", region);
				map_newVehicle.put("country_code", country_code);
				map_newVehicle.put("reg_no", reg_no);
				map_newVehicle.put("error_messages", mapper.readValue(error_messages_1, Map.class));
				
				/*map_newVehicle.put("body_type", SAMPLE_DATA);
				map_newVehicle.put("vehicle_make", SAMPLE_DATA);
				map_newVehicle.put("manufacture_year", SAMPLE_DATA);
				map_newVehicle.put("fuel_used", SAMPLE_DATA);
				map_newVehicle.put("motor_category", SAMPLE_DATA);
				map_newVehicle.put("vehicle_color", SAMPLE_DATA);
				map_newVehicle.put("vehicle_usage", SAMPLE_DATA);*/
				
				Map<String,String> map = new HashMap<>();
				map_newVehicle.put("error_messages", map);
				
				
				
				return_response.put("data", map_newVehicle);
				return_response.put("screen", "VEHICLE_INFORMATION");
				response =printReq.toJson(return_response);

				log.info("response"+ response);								
			}
			else if("SEARCH_VEHICLE".equalsIgnoreCase(component_action)) {
				String title =data.get("title")==null?"":data.get("title").toString().trim();
				String customer_name = data.get("customer_name")==null ? "" : data.get("customer_name").toString().trim();
				String country_code = data.get("country_code")==null ? "" : data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number")==null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id")==null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address")==null ? "" : data.get("address").toString().trim();
				String region = data.get("region")==null ? "" : data.get("region").toString().trim();
				String reg_no = data.get("reg_no")==null ? "" : data.get("reg_no").toString().trim();
				
				Map<String,Object> map_backPage = new HashMap<String, Object>();
				
				map_backPage.put("new_registration", "Create new vehicle");
				map_backPage.put("search_heading", "Search for your vehicle by Registration Number here.");
				
				map_backPage.put("title", title);
				map_backPage.put("customer_name", customer_name);
				map_backPage.put("mobile_number", mobile_number);
				map_backPage.put("email_id", email_id);
				map_backPage.put("address", address);
				map_backPage.put("region", region);
				map_backPage.put("country_code", country_code);
				
				return_response.put("data", map_backPage);
				return_response.put("screen", "VEHICLE_DETAILS");
				response =printReq.toJson(return_response);

				log.info("response"+ response);	

			}
			
		}
		catch(Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		}
		return response;
	}
			
}
