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
	
	
	@Value("${wh.stp.make}")
	private String stpMake;
	
	@Value("${wh.stp.model}")
	private String stpMakeModel;
	
	@Value("${wh.stp.region}")
	private String stpRegion;
	
	@Value("${wh.get.ewaydata.api}")
	private String wh_get_ewaydata_api;
	
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
					request_map.put("BranchCode",broker_loginid);
					request_map.put("BodyType",body_type);
					String ewayValidationApi =wh_get_ewaydata_api;					
					api_response =thread.callEwayApi(ewayValidationApi,mapper.writeValueAsString(request_map),token);
					
					Map<String,Object> validation_map =mapper.readValue(api_response, Map.class);				
					Boolean status =(Boolean)validation_map.get("IsError");					
					if(status) {
						Map<String,Object> seat_map =(Map<String,Object>) validation_map.get("Result");
						String seats =seat_map.get("SeatingCapacity").toString();
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
				if(StringUtils.isBlank(sectionId)) {
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
					
					insertPreinspectionData(upload_transaction_no,file_path,"Registration Card",file_name,101);

					
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
					
					log.info("Vehicle front :: "+file_path);
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
					
					insertPreinspectionData(upload_transaction_no,file_path,"Speedo Meter Image",file_name,102);

					
					Map<String,Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Upload Chassis Number Image");
					flow_data.put("label", "Upload Chassis Number Image");
					flow_data.put("description", "Please take a photo or browse your folder to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					flow_data.put("skip_image", IMAGE_SKIP_OPTION);
					
					map.put("screen", "CHASSIS_NUMBER");
					map.put("version", version);
					map.put("data", flow_data);
					
					log.info("Vehicle front :: "+file_path);
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
					
					insertPreinspectionData(upload_transaction_no,file_path,"Chassis Number Image",file_name,103);

					
					Map<String,Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Vehicle Front Side Image");
					flow_data.put("label", "Upload Vehicle Front Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					
					map.put("screen", "VEHICLE_FRONT");
					map.put("version", version);
					map.put("data", flow_data);
					
					log.info("Vehicle front :: "+file_path);
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
				
				insertPreinspectionData(upload_transaction_no,file_path,"Front Vehicle Image",file_name,104);

				
				Map<String,Object> flow_data = new HashMap<String, Object>();
				flow_data.put("title", "Vehicle Back Side Image");
				flow_data.put("label", "Upload Vehicle Back Side Image");
				flow_data.put("description", "Please take a photo to upload an image");
				flow_data.put("upload_transaction_no", upload_transaction_no.toString());
				flow_data.put("footer_label", "Upload");
				
				map.put("screen", "VEHICLE_BACK");
				map.put("version", version);
				map.put("data", flow_data);
				
				log.info("Vehicle front :: "+file_path);
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
				
				insertPreinspectionData(upload_transaction_no,file_path,"Back Vehicle Image",file_name,105);

				
				Map<String,Object> flow_data = new HashMap<String, Object>();
				flow_data.put("title", "Vehicle Right Side Image");
				flow_data.put("label", "Upload Vehicle Right Side Image");
				flow_data.put("description", "Please take a photo to upload an image");
				flow_data.put("upload_transaction_no", upload_transaction_no.toString());
				flow_data.put("footer_label", "Upload");
				
				map.put("screen", "VEHICLE_RIGHT");
				map.put("version", version);
				map.put("data", flow_data);
				
				log.info("Vehicle Back :: "+file_path);
				
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
				
				insertPreinspectionData(upload_transaction_no,file_path,"Side Vehicle Image",file_name,106);

				
				Map<String,Object> flow_data = new HashMap<String, Object>();
				flow_data.put("title", "Vehicle Left Side Image");
				flow_data.put("label", "Upload Vehicle Left Side Image");
				flow_data.put("description", "Please take a photo to upload an image");
				flow_data.put("upload_transaction_no", upload_transaction_no.toString());
				flow_data.put("footer_label", "Upload");
				
				map.put("screen", "VEHICLE_LEFT");
				map.put("version", version);
				map.put("data", flow_data);
				
				log.info("Vehicle Right :: "+file_path);

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
				
				insertPreinspectionData(upload_transaction_no,file_path,"Bottom Vehicle Image",file_name,107);
				
				Map<String,Object> flow_data = new HashMap<String, Object>();
				flow_data.put("header", "Alliance Insurance Corportation Limted");
				flow_data.put("response_text", "Your documents has been received. Thank your for using alliance bot");
				flow_data.put("title", "Thank you....!");
				flow_data.put("upload_transaction_no", upload_transaction_no.toString());

				log.info("Vehicle Right :: "+file_path);

				
				map.put("screen", "END_SCREEN");
				map.put("version", version);
				map.put("data", flow_data);
				
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
	
	

}
