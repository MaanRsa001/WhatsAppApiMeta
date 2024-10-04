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
public class WhatsapppFlowServiceImpl implements WhatsapppFlowService {

	Logger log = LogManager.getLogger(WhatsapppFlowServiceImpl.class);

	ObjectMapper mapper = new ObjectMapper();

	public static Gson printReq = new Gson();

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

	private static OkHttpClient okhttp = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();

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
	
	@Value("${wh.get.motor.details}")
	private String wh_get_motor_details;

	private static List<Map<String, String>> IMAGE_SKIP_OPTION = new ArrayList<>();

	private static List<Map<String, String>> PRE_DROPDOWN_DATA = new ArrayList<>();

	private static List<Map<String, String>> SAMPLE_DATA = new ArrayList<>();

	static {
		Map<String, String> object_1 = new HashMap<String, String>();
		object_1.put("id", "Y");
		object_1.put("title", "Skip");
		IMAGE_SKIP_OPTION.add(object_1);

		Map<String, String> object_2 = new HashMap<String, String>();
		object_2.put("id", "1");
		object_2.put("title", "Registration Number");
		PRE_DROPDOWN_DATA.add(object_2);

		Map<String, String> object_3 = new HashMap<String, String>();
		object_3.put("id", "2");
		object_3.put("title", "Chassis Number");
		PRE_DROPDOWN_DATA.add(object_3);

		Map<String, String> object_4 = new HashMap<String, String>();
		object_4.put("id", "00000");
		object_4.put("title", "--No Record Found--");
		SAMPLE_DATA.add(object_4);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String claimIntimation(Map<String, Object> request) {
		String response = "";
		try {

			Map<String, Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version") == null ? "" : request.get("version").toString();
			String screen_name = request.get("screen") == null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String flow_token = request.get("flow_token") == null ? "" : request.get("flow_token").toString();

			Map<String, Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);

			if ("VALIDATE_REGISTRATION_NO".equalsIgnoreCase(component_action)) {

				Map<String, Object> map = new HashMap<String, Object>();

				String claim_inputType = data.get("claim_input_type") == null ? ""
						: data.get("claim_input_type").toString();
				String inputdata = data.get("inputdata") == null ? "" : data.get("inputdata").toString();
				String api = "";
				if ("1".equals(claim_inputType)) {
					map.put("ChassisNo", inputdata);
					map.put("InsuranceId", "100003");
					api = cs.getwebserviceurlProperty().getProperty("get.policy.details.bychassisNo");
				} else if ("2".equals(claim_inputType)) {
					api = cs.getwebserviceurlProperty().getProperty("get.policy.details.bypolicyno");
					map.put("QuotationPolicyNo", inputdata);
					map.put("InsuranceId", "100003");
				}

				String reg_request = printReq.toJson(map);

				response = apicall.callApi(api, reg_request);
				log.info("Claim Intimation response " + response);

				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				List<Map<String, Object>> claimList = mapper.readValue(response, List.class);

				List<Map<String, Object>> errorList = claimList.isEmpty() ? null
						: (List<Map<String, Object>>) claimList.get(0).get("Errors") == null ? Collections.EMPTY_LIST
								: (List<Map<String, Object>>) claimList.get(0).get("Errors");

				if (errorList == null || !errorList.isEmpty()) {
					Map<String, String> error = new HashMap<String, String>();

					if ("1".equals(claim_inputType)) {
						error.put("inputdata", "RegistrationNo not found");
					} else if ("2".equals(claim_inputType)) {
						error.put("inputdata", "PolicyNumber not found");
					}

					Map<String, Object> response_data = new HashMap<String, Object>();
					response_data.put("error_messages", error);
					return_res.put("data", response_data);

					response = this.mapper.writeValueAsString(return_res);
					return response;
				} else {

					Map<String, Object> policy_data = (Map<String, Object>) claimList.get(0).get("PolicyInfo");
					String policyNo = policy_data.get("PolicyNo") == null ? "" : policy_data.get("PolicyNo").toString();
					String customerName = policy_data.get("Contactpername") == null ? ""
							: policy_data.get("Contactpername").toString();
					String InsuranceId = policy_data.get("InsuranceId") == null ? ""
							: policy_data.get("InsuranceId").toString();
					String branchCode = policy_data.get("BranchCode") == null ? ""
							: policy_data.get("BranchCode").toString();
					String regionCode = policy_data.get("RegionCode") == null ? ""
							: policy_data.get("RegionCode").toString();
					String product = policy_data.get("Product") == null ? "" : policy_data.get("Product").toString();
					String divn_code = policy_data.get("ProductCode") == null ? ""
							: policy_data.get("ProductCode").toString();

					List<Map<String, String>> list_of_vehicle = claimList.stream().map(p -> {
						Map<String, String> vehicle_det = new HashMap<>();

						Map<String, Object> vd = (Map<String, Object>) p.get("VehicleInfo");

						String vehicle_model_desc = vd.get("Vehiclemodeldesc") == null ? ""
								: vd.get("Vehiclemodeldesc").toString();

						String chassis_no = vd.get("ChassisNo") == null ? "" : vd.get("ChassisNo").toString();

						vehicle_det.put("id", chassis_no);
						vehicle_det.put("title", chassis_no + " || " + vehicle_model_desc + "");

						return vehicle_det;
					}).collect(Collectors.toList());

					Map<String, Object> navigate_screen = new HashMap<String, Object>();
					Map<String, Object> error_message = new HashMap<String, Object>();
					error_message.put("", "");
					navigate_screen.put("registrationNo", inputdata);
					navigate_screen.put("policyNumber", policyNo);
					navigate_screen.put("customer_name", "Customer Name : " + customerName + "");
					navigate_screen.put("PolicyNumberText", "Policy Number : " + policyNo + "");
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
					response = this.mapper.writeValueAsString(return_res);

					return response;

				}

			} else if ("CLAIM_INPUT_TYPE".equalsIgnoreCase(component_action)) {

				String claim_inputType = data.get("claim_input_type") == null ? ""
						: data.get("claim_input_type").toString();
				Map<String, Object> inputTypeRes = new HashMap<String, Object>();
				inputTypeRes.put("isInputDataReq", true);
				inputTypeRes.put("isVisibleInputData", true);
				if ("1".equals(claim_inputType))
					inputTypeRes.put("inputdata_lable_name", "Registration Number");
				else if ("2".equals(claim_inputType))
					inputTypeRes.put("inputdata_lable_name", "Policy Number");

				return_res.put("data", inputTypeRes);

				response = this.mapper.writeValueAsString(return_res);

				return response;
			} else if ("ACCIDENT_VALIDATION".equalsIgnoreCase(component_action)) {

				String vehicle = data.get("vehicle") == null ? "" : data.get("vehicle").toString();
				String accident_date = data.get("accident_date") == null ? "" : data.get("accident_date").toString();
				String accident_time = data.get("accident_time") == null ? "" : data.get("accident_time").toString();
				String contact_no = data.get("contact_no") == null ? "" : data.get("contact_no").toString();
				String registrationNo = data.get("registrationNo") == null ? "" : data.get("registrationNo").toString();
				String policyNumber = data.get("policyNumber") == null ? "" : data.get("policyNumber").toString();
				String customer_name = data.get("customer_name") == null ? "" : data.get("customer_name").toString();
				String insuranceId = data.get("InsuranceId") == null ? "" : data.get("InsuranceId").toString();
				String branchCode = data.get("branchCode") == null ? "" : data.get("branchCode").toString();
				String regionCode = data.get("regionCode") == null ? "" : data.get("regionCode").toString();
				String product = data.get("product") == null ? "" : data.get("product").toString();
				String divn_code = data.get("divn_code") == null ? "" : data.get("divn_code").toString();

				Map<String, String> error_message = new HashMap<String, String>();

				if (!contact_no.matches("[0-9]+")) {
					error_message.put("contact_no", "Please enter valid mobile");
				} else if (!contact_no.matches("0?[0-9]{9}")) {
					error_message.put("contact_no", "MobileNo should be 9 digits");
				}
				if (!accident_time.matches("[0-9]{2}:[0-9]{2}")) {
					error_message.put("accident_time", "Enter valid time format");
				}

				if (error_message.size() > 0) {
					Map<String, Object> error_message_res = new HashMap<>();
					error_message_res.put("error_messages", error_message);
					return_res.put("data", error_message_res);

					response = this.mapper.writeValueAsString(return_res);

					return response;
				} else {

					String tokenApi = cs.getwebserviceurlProperty().getProperty("token.api");

					String token = apicall.getClaimToken(tokenApi);

					CompletableFuture<List<Map<String, String>>> loss_type = this.thread.getLossTypes(token,
							insuranceId, product);
					CompletableFuture<List<Map<String, String>>> casue_of_loss = this.thread.getCauseOfLoss(token,
							insuranceId, divn_code);
					CompletableFuture.allOf(loss_type, casue_of_loss).join();

					List<String> list = Arrays.asList("Driver", "Owner");
					List<Map<String, String>> drivenBy = list.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p);
						map.put("title", p);
						return map;
					}).collect(Collectors.toList());

					Map<String, Object> object = new HashMap<>();

					Map<String, Object> error_message_3 = new HashMap<String, Object>();
					error_message_3.put("", "");
					object.put("vehicle", vehicle);
					object.put("accident_date", accident_date);
					object.put("accident_time", accident_time);
					object.put("contact_no", contact_no);
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
					response = this.mapper.writeValueAsString(return_res);

					return response;
				}
			} else if ("LOSSTYPE_VALIDATION".equalsIgnoreCase(component_action)) {

				String loss_type = data.get("loss_type") == null ? "" : data.get("loss_type").toString();
				String cause_of_loss = data.get("cause_of_loss") == null ? "" : data.get("cause_of_loss").toString();
				String accident_person = data.get("accident_person") == null ? ""
						: data.get("accident_person").toString();
				String accident_desc = data.get("accident_desc") == null ? "" : data.get("accident_desc").toString();
				String vehicle = data.get("vehicle") == null ? "" : data.get("vehicle").toString();
				String accident_date = data.get("accident_date") == null ? "" : data.get("accident_date").toString();
				String accident_time = data.get("accident_time") == null ? "" : data.get("accident_time").toString();
				String contact_no = data.get("contact_no") == null ? "" : data.get("contact_no").toString();
				String registrationNo = data.get("registrationNo") == null ? "" : data.get("registrationNo").toString();
				String policyNumber = data.get("policyNumber") == null ? "" : data.get("policyNumber").toString();
				String customer_name = data.get("customer_name") == null ? "" : data.get("customer_name").toString();
				String InsuranceId = data.get("InsuranceId") == null ? "" : data.get("InsuranceId").toString();
				String branchCode = data.get("branchCode") == null ? "" : data.get("branchCode").toString();
				String regionCode = data.get("regionCode") == null ? "" : data.get("regionCode").toString();
				String product = data.get("product") == null ? "" : data.get("product").toString();
				String divn_code = data.get("divn_code") == null ? "" : data.get("divn_code").toString();

				Map<String, Object> extension_message_response = new HashMap<String, Object>();
				Map<String, Object> params = new HashMap<String, Object>();
				Map<String, Object> param_map = new HashMap<String, Object>();

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

				response = printReq.toJson(return_res);

				return response;

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public String createShortTermPolicy(Map<String, Object> request) {
		String response = "";
		String api_request = "";
		String api_response = "";
		try {
			Map<String, Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version") == null ? "" : request.get("version").toString();
			String screen_name = request.get("screen") == null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String flow_token = request.get("flow_token") == null ? "" : request.get("flow_token").toString();

			Map<String, Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);

			String sample_data = "[ {\"id\": \"0\", \"title\": \"--SELECT--\"}]";
			String error_messages_1 = " {\"id\": \"\", \"\": \"\"}";
			List<Map<String, Object>> list = mapper.readValue(sample_data, List.class);

			String token = this.thread.getEwayToken();

			Map<String, String> input_validation = new HashMap<>();
			if ("VEHILCE_VALIDATION".equalsIgnoreCase(component_action)) {
				String chassis_no = data.get("chassis_no") == null ? "" : data.get("chassis_no").toString().trim();
				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String make = data.get("make") == null ? "" : data.get("make").toString().trim();
				String model = data.get("model") == null ? "" : data.get("model").toString().trim();
				String engine_capacity = data.get("engine_capacity") == null ? ""
						: data.get("engine_capacity").toString().trim();
				String manufacture_year = data.get("manufacture_year") == null ? ""
						: data.get("manufacture_year").toString().trim();
				String fuel_used = data.get("fuel_used") == null ? "" : data.get("fuel_used").toString().trim();
				String vehicle_color = data.get("vehicle_color") == null ? ""
						: data.get("vehicle_color").toString().trim();
				String vehicle_usage = data.get("vehicle_usage") == null ? ""
						: data.get("vehicle_usage").toString().trim();
				String seating_capacity = data.get("seating_capacity") == null ? ""
						: data.get("seating_capacity").toString().trim();
				String isbroker = data.get("isbroker") == null ? "" : data.get("isbroker").toString().trim();
				String broker_loginid = data.get("broker_loginid") == null ? ""
						: data.get("broker_loginid").toString().trim();

				String title = data.get("title") == null ? "" : data.get("title").toString();
				String customer_name = data.get("customer_name") == null ? "" : data.get("customer_name").toString();
				String mobile_no = data.get("mobile_no") == null ? "" : data.get("mobile_no").toString();
				String email = data.get("email") == null ? "" : data.get("email").toString();
				String address = data.get("address") == null ? "" : data.get("address").toString();
				String region = data.get("region") == null ? "" : data.get("region").toString();
				String country_code = data.get("country_code") == null ? "" : data.get("country_code").toString();

				// validation message text should be not graterthan 30 characters.

				if (chassis_no.length() < 5) {
					input_validation.put("chassis_no", "Minimum 5 characters required");
				} else if (!chassis_no.matches("[a-zA-Z0-9]+")) {
					input_validation.put("chassis_no", "Special characters not allowed");
				}

				if (!engine_capacity.matches("[0-9]+")) {
					input_validation.put("engine_capacity", "digits only are allowed");
				}
				if ("1".equalsIgnoreCase(isbroker)) {

					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("Type", "LOGIN_ID_CHECK");
					request_map.put("LoginId", broker_loginid);
					String ewayValidationApi = wh_get_ewaydata_api;
					api_response = thread.callEwayApi(ewayValidationApi, mapper.writeValueAsString(request_map), token);
					Map<String, Object> map = mapper.readValue(api_response, Map.class);
					Boolean status = (Boolean) map.get("IsError");

					if (status)
						input_validation.put("broker_loginid", "broker loginid not found");
				}

				// seating capacity input validation
				if (!seating_capacity.matches("[0-9]+")) {
					input_validation.put("seating_capacity", "Only digits are allowed");
				} else {
					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("Type", "SEAT_CAPACITY");
					request_map.put("SeatingCapacity", seating_capacity);
					request_map.put("InsuranceId", "100002");
					request_map.put("BranchCode", "01");
					request_map.put("BodyType", body_type);
					String ewayValidationApi = wh_get_ewaydata_api;
					api_response = thread.callEwayApi(ewayValidationApi, mapper.writeValueAsString(request_map), token);

					Map<String, Object> validation_map = mapper.readValue(api_response, Map.class);
					Boolean status = (Boolean) validation_map.get("IsError");
					if (status) {
						Map<String, Object> seat_map = (Map<String, Object>) validation_map.get("Result");
						String seats = seat_map.get("Seating Capacity").toString();
						input_validation.put("seating_capacity", "should be under " + seats + " or equal ");
					}
				}

				// checking validation data
				if (!input_validation.isEmpty() && input_validation.size() > 0) {

					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "01");
					request_map.put("InsuranceId", "100002");
					request_map.put("BodyId", body_type);
					request_map.put("MakeId", make);

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> fuel_type_e = thread.getFuelType(request_1, token);
					CompletableFuture<List<Map<String, String>>> color_e = thread.getColor(request_1, token);
					CompletableFuture<List<Map<String, String>>> manufacture_year_e = thread.getManuFactureYear();
					CompletableFuture<List<Map<String, String>>> body_type_e = thread.getSTPBodyType(request_1, token);
					CompletableFuture<List<Map<String, String>>> vehicle_usage_e = thread.getSTPVehicleUsage(request_1,
							token);
					CompletableFuture<List<Map<String, String>>> make_e = thread.getStpMake(token, body_type);
					CompletableFuture<List<Map<String, String>>> model_e = thread.getSTPModel(body_type, make, token);

					CompletableFuture.allOf(fuel_type_e, color_e, manufacture_year_e, body_type_e, vehicle_usage_e,
							make_e, model_e).join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("body_type", body_type_e.get().isEmpty() ? SAMPLE_DATA : body_type_e.get());
					error_messages.put("make", make_e.get().isEmpty() ? SAMPLE_DATA : make_e.get());
					error_messages.put("model", model_e.get().isEmpty() ? SAMPLE_DATA : model_e.get());
					error_messages.put("manufacture_year",
							manufacture_year_e.get().isEmpty() ? list : manufacture_year_e.get());
					error_messages.put("fuel_used", fuel_type_e.get().isEmpty() ? SAMPLE_DATA : fuel_type_e.get());
					error_messages.put("vehicle_color", color_e.get().isEmpty() ? SAMPLE_DATA : color_e.get());
					error_messages.put("vehicle_usage",
							vehicle_usage_e.get().isEmpty() ? SAMPLE_DATA : vehicle_usage_e.get());

					error_messages.put("title", title);
					error_messages.put("customer_name", customer_name);
					error_messages.put("mobile_no", mobile_no);
					error_messages.put("email", email);
					error_messages.put("address", address);
					error_messages.put("region", region);
					error_messages.put("country_code", country_code);

					return_res.put("data", error_messages);

					response = printReq.toJson(return_res);
					return response;

				} else {

					Map<String, Object> extension_message_response = new HashMap<String, Object>();
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String, Object> param_map = new HashMap<String, Object>();

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

					response = printReq.toJson(return_res);

				}

			}
			if ("ISBROKER".equalsIgnoreCase(component_action)) {

				String is_broker = data.get("isBroker") == null ? "" : data.get("isBroker").toString().trim();
				Map<String, Boolean> enableLogin = new HashMap<String, Boolean>();
				if ("1".equalsIgnoreCase(is_broker)) {
					enableLogin.put("isVisibleBrokerLoginId", true);
					enableLogin.put("isMandatoryBrokerLoginId", true);
				} else if ("2".equalsIgnoreCase(is_broker)) {
					enableLogin.put("isVisibleBrokerLoginId", false);
					enableLogin.put("isMandatoryBrokerLoginId", false);
				}

				return_res.put("data", enableLogin);
				response = printReq.toJson(return_res);
				return response;

			} else if ("MAKE".equalsIgnoreCase(component_action)) {

				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();

				if (StringUtils.isNotBlank(body_type)) {
					String api = this.stpMake;

					Map<String, Object> region_req = new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100002");
					region_req.put("BranchCode", "01");

					api_request = printReq.toJson(region_req);

					api_response = thread.callEwayApi(api, api_request, token);

					Map<String, Object> region_obj = mapper.readValue(api_response, Map.class);
					List<Map<String, Object>> result = (List<Map<String, Object>>) region_obj.get("Result");

					data_list = result.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p.get("Code") == null ? "" : p.get("Code").toString());
						map.put("title", p.get("CodeDesc") == null ? "" : p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());

				} else {
					data_list = SAMPLE_DATA;
				}
				Map<String, Object> make_list = new HashMap<String, Object>();
				make_list.put("make", data_list);

				return_res.put("data", make_list);
				response = printReq.toJson(return_res);
				return response;

			} else if ("MODEL".equalsIgnoreCase(component_action)) {

				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String make = data.get("make") == null ? "" : data.get("make").toString().trim();
				List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();

				if (!"00000".equals(make) && StringUtils.isNotBlank(make)) {

					String api = this.stpMakeModel;

					Map<String, Object> region_req = new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100002");
					region_req.put("BranchCode", "01");
					region_req.put("MakeId", make);

					api_request = printReq.toJson(region_req);

					api_response = thread.callEwayApi(api, api_request, token);

					Map<String, Object> region_obj = mapper.readValue(api_response, Map.class);
					List<Map<String, Object>> result = (List<Map<String, Object>>) region_obj.get("Result");

					data_list = result.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p.get("Code") == null ? "" : p.get("Code").toString());
						map.put("title", p.get("CodeDesc") == null ? "" : p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				} else {
					data_list = SAMPLE_DATA;
				}

				Map<String, Object> make_list = new HashMap<String, Object>();
				make_list.put("model", data_list);

				return_res.put("data", make_list);
				response = printReq.toJson(return_res);
				return response;

			} else if ("CUSTOMER_VALIDATION".equalsIgnoreCase(component_action)) {

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String mobile_no = data.get("mobile_no") == null ? "" : data.get("mobile_no").toString().trim();
				String email = data.get("email") == null ? "" : data.get("email").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();

				if (!customer_name.matches("[a-zA-Z ]+")) {
					input_validation.put("customer_name", "Please enter valid name");
				}
				if (!mobile_no.matches("[0-9]+")) {
					input_validation.put("mobile_no", "Please enter valid mobile");
				} else if (!mobile_no.matches("0?[0-9]{9}")) {
					input_validation.put("mobile_no", "MobileNo should be 9 digits");
				}

				if (input_validation.size() > 0) {

					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "01");
					request_map.put("InsuranceId", "100002");

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> title_1 = thread.getCustomerTitle(request_1, token);
					CompletableFuture<List<Map<String, String>>> country_code_1 = thread
							.getCustomerCountryCode(request_1, token);
					CompletableFuture<List<Map<String, String>>> region_1 = thread.getCustomerRegion(token);

					CompletableFuture.allOf(title_1, country_code_1, region_1).join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("title", title_1.get().isEmpty() ? SAMPLE_DATA : title_1.get());
					error_messages.put("countryCode",
							country_code_1.get().isEmpty() ? SAMPLE_DATA : country_code_1.get());
					error_messages.put("region", region_1.get().isEmpty() ? SAMPLE_DATA : region_1.get());
					return_res.put("action", "data_exchange");
					return_res.put("data", error_messages);

					response = printReq.toJson(return_res);

				} else {

					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "01");
					request_map.put("InsuranceId", "100002");

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> fuel_type = thread.getFuelType(request_1, token);
					CompletableFuture<List<Map<String, String>>> color = thread.getColor(request_1, token);
					CompletableFuture<List<Map<String, String>>> manufacture_year = thread.getManuFactureYear();
					CompletableFuture<List<Map<String, String>>> body_type = thread.getSTPBodyType(request_1, token);
					CompletableFuture<List<Map<String, String>>> vehicle_usage = thread.getSTPVehicleUsage(request_1,
							token);
					CompletableFuture<List<Map<String, String>>> motor_category = thread.getMotorCategory(request_1,
							token);

					CompletableFuture.allOf(fuel_type, color, manufacture_year, body_type, vehicle_usage).join();

					Map<String, Object> map_vehicle = new HashMap<String, Object>();
					map_vehicle.put("title", title);
					map_vehicle.put("customer_name", customer_name);
					map_vehicle.put("mobile_no", mobile_no);
					map_vehicle.put("email", email);
					map_vehicle.put("address", address);
					map_vehicle.put("region", region);
					map_vehicle.put("country_code", country_code);
					map_vehicle.put("fuelUsed", fuel_type.get().isEmpty() ? list : fuel_type.get());
					map_vehicle.put("bodyType", body_type.get().isEmpty() ? list : body_type.get());
					map_vehicle.put("make", list);
					map_vehicle.put("model", list);
					map_vehicle.put("manufactureYear",
							manufacture_year.get().isEmpty() ? list : manufacture_year.get());
					map_vehicle.put("vehicleColor", color.get().isEmpty() ? list : color.get());
					map_vehicle.put("vehicleUsage", vehicle_usage.get().isEmpty() ? list : vehicle_usage.get());
					map_vehicle.put("isVisibleBrokerLoginId", false);
					map_vehicle.put("isMandatoryBrokerLoginId", false);
					map_vehicle.put("error_messages", mapper.readValue(error_messages_1, Map.class));
					return_res.put("data", map_vehicle);
					return_res.put("screen", "VEHICLE_DETAILS");
					response = printReq.toJson(return_res);

				}

				return response;

			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return response;
	}

	// @PostConstruct
	public void getCustomerDropdown() {
		try {

			String token = this.thread.getEwayToken();
			Map<String, String> request_map = new HashMap<String, String>();
			request_map.put("BranchCode", "01");
			request_map.put("InsuranceId", "100002");

			String request = printReq.toJson(request_map);

			log.info("Customer api start time is : " + new Date());

			CompletableFuture<List<Map<String, String>>> title = thread.getCustomerTitle(request, token);
			CompletableFuture<List<Map<String, String>>> occupation = thread.getCustomerOccupation(request, token);
			CompletableFuture<List<Map<String, String>>> country_code = thread.getCustomerCountryCode(request, token);
			CompletableFuture<List<Map<String, String>>> gender = thread.getCustomerGender(request, token);
			CompletableFuture<List<Map<String, String>>> country = thread.getCustomerCountry(request, token);

			CompletableFuture.allOf(title, occupation, country_code, gender, country).join();

			Map<String, String> response = new HashMap<String, String>();
			response.put("title", printReq.toJson(title.get()));
			response.put("occupation", printReq.toJson(occupation.get()));
			response.put("countryCode", printReq.toJson(country_code.get()));
			response.put("gender", printReq.toJson(gender.get()));
			response.put("country", printReq.toJson(country.get()));

			log.info("Customer api End time is : " + new Date());
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	public void vehicleDropdown() {
		try {

			String token = this.thread.getEwayToken();
			Map<String, String> request_map = new HashMap<String, String>();
			request_map.put("BranchCode", "01");
			request_map.put("InsuranceId", "100002");

			String request = printReq.toJson(request_map);

			CompletableFuture<List<Map<String, String>>> fuel_type = thread.getFuelType(request, token);
			CompletableFuture<List<Map<String, String>>> color = thread.getColor(request, token);
			CompletableFuture<List<Map<String, String>>> manufacture_year = thread.getManuFactureYear();
			CompletableFuture<List<Map<String, String>>> body_type = thread.getSTPBodyType(request, token);
			CompletableFuture<List<Map<String, String>>> vehicle_usage = thread.getSTPVehicleUsage(request, token);
			CompletableFuture<List<Map<String, String>>> motor_category = thread.getMotorCategory(request, token);

			CompletableFuture.allOf(fuel_type, color, manufacture_year, body_type, vehicle_usage, motor_category)
					.join();

			Map<String, String> response = new HashMap<String, String>();
			response.put("fuelUsed", printReq.toJson(fuel_type.get()));
			response.put("vehicleColor", printReq.toJson(color.get()));
			response.put("manufactureYear", printReq.toJson(manufacture_year.get()));
			response.put("bodyType", printReq.toJson(body_type.get()));
			response.put("vehicleUsage", printReq.toJson(vehicle_usage.get()));
			response.put("motorCategory", printReq.toJson(motor_category.get()));

			log.info("Vehicle api End time is : " + new Date());

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> quotation_flow_screen_data() {
		try {

			Map<String, Object> data = insurance.getWhatsappFlowMaster();

			Map<String, Object> flow_action_payload = new HashMap<String, Object>();
			// flow_action_payload.put("version", "3.1");
			flow_action_payload.put("screen", "MOTOR_QUOTATION");
			flow_action_payload.put("data", data);

			return flow_action_payload;

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String createVehicleQuotation(Map<String, Object> request) {
		String response = "";
		try {
			Map<String, Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version") == null ? "" : request.get("version").toString();
			String screen_name = request.get("screen") == null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String flow_token = request.get("flow_token") == null ? "" : request.get("flow_token").toString();

			Map<String, Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);

			if ("HIDE_SUMINSURED".equalsIgnoreCase(component_action)) {
				String policy_type = data.get("policyType") == null ? "" : data.get("policyType").toString().trim();
				;
				Map<String, Object> hide_suminsured = new HashMap<String, Object>();
				if ("1".equals(policy_type) || "2".equals(policy_type)) {
					hide_suminsured.put("disble_suminsured_field", true);
					hide_suminsured.put("isSuminsuredRequired", true);
				} else {
					hide_suminsured.put("disble_suminsured_field", false);
					hide_suminsured.put("isSuminsuredRequired", false);
				}

				return_res.put("data", hide_suminsured);

				response = printReq.toJson(return_res);
				return response;

			} else if ("VEHICLE_USAGE".equalsIgnoreCase(component_action)) {

				String sectionId = data.get("sectionName") == null ? "" : data.get("sectionName").toString().trim();
				if (StringUtils.isNotBlank(sectionId)) {
					String token = thread.getEwayToken();
					List<Map<String, String>> vehiUsage = thread.getVehicleUsage(token, sectionId);

					Map<String, Object> vehicle_usage = new HashMap<String, Object>();
					vehicle_usage.put("vehicleUsage", vehiUsage);

					return_res.put("data", vehicle_usage);

					response = printReq.toJson(return_res);
				} else {
					response = printReq.toJson(SAMPLE_DATA);
				}
				return response;

			} else if ("INPUT_VALIDATION".equals(component_action)) {

				String idType = data.get("idType") == null ? "" : data.get("idType").toString().trim();
				String customerName = data.get("customerName") == null ? ""
						: data.get("customerName").toString().trim();
				String idNumber = data.get("idNumber") == null ? "" : data.get("idNumber").toString().trim();
				String sumInsured = data.get("sumInsured") == null ? "0" : data.get("sumInsured").toString().trim();
				String registrationNo = data.get("registrationNo") == null ? ""
						: data.get("registrationNo").toString().trim();
				String policyType = data.get("policyType") == null ? "" : data.get("policyType").toString().trim();
				String sectionName = data.get("sectionName") == null ? "" : data.get("sectionName").toString().trim();
				String bodyType = data.get("bodyType") == null ? "" : data.get("bodyType").toString().trim();
				String vehicleUsage = data.get("vehicleUsage") == null ? ""
						: data.get("vehicleUsage").toString().trim();
				String claimyn = data.get("claimyn") == null ? "" : data.get("claimyn").toString().trim();

				Map<String, Object> input_validation = new HashMap<String, Object>();

				if (!customerName.matches("[a-zA-Z ]+")) {
					input_validation.put("customerName", "Special letters not allowed");
				}

				Map<String, Object> tiraMap = insurance.checkRegistrationWithTira(registrationNo);
				if (tiraMap == null) {
					input_validation.put("registrationNo", "Tira data not found");

				} else {
					String errorMessage = tiraMap.get("ErrorMessage") == null ? ""
							: tiraMap.get("ErrorMessage").toString();
					if (StringUtils.isNotBlank(errorMessage)) {

						input_validation.put("registrationNo", "Policy is not expiry");
					}
				}

				if (input_validation.size() > 0) {
					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);

					return_res.put("action", "data_exchange");
					return_res.put("data", error_messages);

					response = printReq.toJson(return_res);

				} else {
					Map<String, Object> extension_message_response = new HashMap<String, Object>();
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String, Object> param_map = new HashMap<String, Object>();

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

					response = printReq.toJson(return_res);

				}

				return response;
			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return "";

	}

	@Override
	public Map<String, Object> stp_flow_screen_data() {
		try {

			String token = this.thread.getEwayToken();
			Map<String, String> request_map = new HashMap<String, String>();
			request_map.put("BranchCode", "01");
			request_map.put("InsuranceId", "100002");

			String request = printReq.toJson(request_map);

			log.info("Vehicle api Start time is : " + new Date());

			/*
			 * CompletableFuture<List<Map<String,String>>> fuel_type
			 * =thread.getFuelType(request,token);
			 * CompletableFuture<List<Map<String,String>>> color
			 * =thread.getColor(request,token); CompletableFuture<List<Map<String,String>>>
			 * manufacture_year =thread.getManuFactureYear();
			 * CompletableFuture<List<Map<String,String>>> body_type
			 * =thread.getSTPBodyType(request,token);
			 * CompletableFuture<List<Map<String,String>>> vehicle_usage
			 * =thread.getSTPVehicleUsage(request,token);
			 * CompletableFuture<List<Map<String,String>>> motor_category
			 * =thread.getMotorCategory(request,token);
			 */

			CompletableFuture<List<Map<String, String>>> title = thread.getCustomerTitle(request, token);
			CompletableFuture<List<Map<String, String>>> country_code = thread.getCustomerCountryCode(request, token);
			CompletableFuture<List<Map<String, String>>> region = thread.getCustomerRegion(token);

			// CompletableFuture.allOf(,color,manufacture_year,
			// body_type,vehicle_usage,motor_category,title,country_code,region).join();

			CompletableFuture.allOf(title, country_code, region).join();

			Map<String, String> error_message = new HashMap<String, String>();
			error_message.put("", "");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("title", title.get());
			data.put("countryCode", country_code.get());
			data.put("region", region.get());
			data.put("error_messages", error_message);
			log.info("Vehicle api End time is : " + mapper.writeValueAsString(data));

			Map<String, Object> flow_action_payload = new HashMap<String, Object>();
			flow_action_payload.put("screen", "CUSTOMER_DETAILS");
			flow_action_payload.put("data", data);

			return flow_action_payload;
		} catch (Exception e) {
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

			List<Map<String, String>> claimInputOpt = new ArrayList<>();
			claimInputOpt.add(policyNo);
			claimInputOpt.add(regNo);

			Map<String, Object> data = new HashMap<String, Object>();

			InputStream is = this.getClass().getResourceAsStream("/images/claimintimation.bin");

			String image_url = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));

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

			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put("screen", "WELCOME_SCREEN");
			payload.put("data", data);

			return payload;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String inalipaClaimIntimation(Map<String, Object> request) {
		String response = "";
		try {
			Map<String, Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version") == null ? "" : request.get("version").toString();
			String screen_name = request.get("screen") == null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String flow_token = request.get("flow_token") == null ? "" : request.get("flow_token").toString();

			Map<String, Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);

			if ("INALIPA_CLAIM_VALIDATION".equalsIgnoreCase(component_action)) {

				String claim_type = data.get("claim_type") == null ? "" : data.get("claim_type").toString();
				String mobile_no = data.get("mobile_no") == null ? "" : data.get("mobile_no").toString();
				String timestamp = data.get("accident_date") == null ? "" : data.get("accident_date").toString();

				Date accident_date = new Date(Long.valueOf(timestamp));
				Date system_date = new Date();
				String format_date = this.cs.formatdatewithouttime(accident_date);
				LocalDate local_date = accident_date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

				// validate existing claim are there or not

				Map<String, Object> validation = new HashMap<String, Object>();

				if (validation.size() == 0) {
					if (accident_date.after(system_date)) {
						validation.put("accident_date", "Future date does not allow");
					}
					if (!mobile_no.matches("0?[0-9]{9}")) {
						validation.put("mobile_no", "9 digits only allowed");
					}
				}

				Map<String, Object> resultMap = new HashMap<String, Object>();
				String policyNo = "";
				Long mobileNo = 0L;
				if (validation.isEmpty()) {

					String url = cs.getwebserviceurlProperty().getProperty("eway.claimDetails.api");
					LinkedHashMap<String, Object> map = new LinkedHashMap<>();
					map.put("MobileNo", mobile_no);
					map.put("AccidentDate", format_date);
					map.put("ClaimType", claim_type);
					log.info("Api Call URL ==> " + url);
					log.info("Api Call Request ==> " + printReq.toJson(map));
					String claim_request = printReq.toJson(map);
					String claim_response = apicall.callEwayApi(url, claim_request);

					Map<String, Object> result = null;
					try {
						result = mapper.readValue(claim_response, Map.class);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if ("SUCCESS".equalsIgnoreCase(result.get("Message").toString())) {
						List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("Response");
						resultMap = resultList.get(0);
						policyNo = resultMap.get("PolicyNo") == null ? "" : resultMap.get("PolicyNo").toString();
						mobileNo = resultMap.get("MobileNo") == null ? null
								: Long.parseLong(resultMap.get("MobileNo").toString());
						List<InalipaIntimatedTable> exitorNot = inalipaIntiRepo.getExistsClaimDetails(policyNo,
								mobileNo, local_date);

						if (!exitorNot.isEmpty()) {
							validation.put("mobile_no", "claim exits already");
							validation.put("accident_date", "claim exits already");
						}
					} else {
						validation.put("mobile_no", "No record found");
						validation.put("accident_date", "No record found");
					}

					log.info("Inalipa claim response :: " + mapper.writeValueAsString(resultMap));
				}

				if (validation.size() > 0) {
					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", validation);
					error_messages.put("claim_type", getInalipaClaimTypes());
					return_res.put("data", error_messages);
					response = this.mapper.writeValueAsString(return_res);
					return response;
				} else {

					Date policyStartDate = resultMap.get("InceptionDate") == null ? null
							: new SimpleDateFormat("dd/MM/yyyy").parse(resultMap.get("InceptionDate").toString());
					Date policyEndDate = resultMap.get("ExpiryDate") == null ? null
							: new SimpleDateFormat("dd/MM/yyyy").parse(resultMap.get("ExpiryDate").toString());
					String claim_type_desc = "1".equals(claim_type) ? "Death Claim"
							: "2".equals(claim_type) ? "Partial Injury" : "Permanent & Total Disability";
					String claimRefMax = repository.getInalipaClamRefMax();
					InalipaIntimatedTable ina = new InalipaIntimatedTable();
					InalipaIntimatedTable in = InalipaIntimatedTable.builder().policyNo(policyNo).mobileNo(mobileNo)
							.intimatedMobileNo(Integer.valueOf(mobileNo.toString())).policyStartDate(policyStartDate)
							.policyEndDate(policyEndDate).intimatedDate(new Date()).ClaimType(claim_type_desc)
							.accidentDate(accident_date).claimNo(claimRefMax).claimId(claim_type).build();
					ina = inalipaIntiRepo.save(in);

					Map<String, Object> param = new HashMap<String, Object>();
					param.put("claim_text", "Claim Number : " + ina.getClaimNo());
					param.put("claim_no", ina.getClaimNo());
					param.put("accident_date", "Accident Date : " + format_date);
					param.put("policy_start_date",
							"PolicyStartDate : " + cs.formatdatewithouttime(ina.getPolicyStartDate()));
					param.put("policy_end_date",
							"PolicyStartDate : " + cs.formatdatewithouttime(ina.getPolicyEndDate()));
					param.put("mobile_no", "Mobile Number : " + ina.getIntimatedMobileNo().toString());
					param.put("claim_type", claim_type_desc);
					param.put("policy_no", "Policy Number : " + ina.getPolicyNo());
					param.put("remarks", "Please note above claim information or take screenshot for future purpose");

					return_res.put("screen", "INALIPA_CLAIM_RESPONSE");
					return_res.put("data", param);

					return response = cs.reqPrint(return_res);
				}
			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Object getInalipaClaimTypes() {
		try {

			Map<String, String> map = new HashMap<String, String>();
			map.put("1", "Death Claim");
			map.put("2", "Partial Injury");
			map.put("3", "Permanent & Total Disability");

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();

			map.entrySet().forEach(p -> {
				Map<String, String> data = new HashMap<String, String>();
				data.put("id", p.getKey());
				data.put("title", p.getValue());
				list.add(data);
			});

			return list;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> InalipaIntimateScreenData() {
		try {

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("claim_type", getInalipaClaimTypes());

			Map<String, Object> flow_action_payload = new HashMap<String, Object>();
			flow_action_payload.put("screen", "INALIPA_CLAIM_INTIMATION");
			flow_action_payload.put("data", data);

			return flow_action_payload;

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String preinspectionUpload(Map<String, Object> request) {
		String response = "";
		try {

			String screen_name = request.get("screen").toString();
			String action = request.get("action").toString();
			String flow_token = request.get("flow_token").toString();
			String version = request.get("version") == null ? "" : request.get("version").toString();
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> data = request.get("data") == null ? null : (Map<String, Object>) request.get("data");
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String upload_type = data.get("upload_type") == null ? "image" : data.get("upload_type").toString();

			if ("WELCOME_SCREEN".equalsIgnoreCase(component_action)) {

				String mobile_no = data.get("mobile_no") == null ? "" : data.get("mobile_no").toString().trim();
				String inputdata = data.get("inputdata") == null ? "" : data.get("inputdata").toString().trim();
				String input_type = data.get("input_type") == null ? "" : data.get("input_type").toString().trim();

				Map<String, String> validation = new HashMap<>();
				if ("1".equals(input_type)) {
					if (inputdata.length() < 5) {
						validation.put("inputdata", "Minimum 5 characters required");
					} else if (!StringUtils.isAlphanumeric(inputdata)) {
						validation.put("inputdata", "Special characters not allowed");
					}
				} else if ("2".equals(input_type)) {
					if (inputdata.length() < 5) {
						validation.put("inputdata", "Minimum 5 characters required");
					} else if (inputdata.matches("^[a-zA-Z0-9]+")) {
						validation.put("inputdata", "Special characters not allowed");
					}
				}
				Map<String, Object> flow_data = new HashMap<String, Object>();

				if (validation.size() > 0) {
					flow_data.put("error_messages", validation);
					map.put("screen", screen_name);
					// map.put("version", version);
					map.put("data", flow_data);

				} else {
					String error_messages_1 = " {\"id\": \"\", \"\": \"\"}";

					if ("image".equalsIgnoreCase(upload_type)) {
						Map<String, String> error_message = new HashMap<>();
						PreinspectionDataDetail pdd = insertPreinspectionData(input_type, inputdata, mobile_no,
								"VEHICLE_IMAGES");
						flow_data.put("title", "Upload Registration Card Image");
						flow_data.put("label", "Upload Registration Card Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", "");
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);
						flow_data.put("error_messages", error_message);

						flow_data.put("image_enabled", true);
						flow_data.put("registration_no", "4546456");
						flow_data.put("mobile_no", "8596469877");
						map.put("screen", "REGISTRATION_CARD");
						// map.put("version", version);
						map.put("data", flow_data);
					} else if ("document".equalsIgnoreCase(upload_type)) {

						PreinspectionDataDetail pdd = insertPreinspectionData(input_type, inputdata, mobile_no,
								"KYC_DOCUMENTS");
						flow_data.put("title", "Upload KYC Documents");
						flow_data.put("label", "Upload KYC Documents");
						flow_data.put("description", "Please browse your document from folder and upload it");
						flow_data.put("upload_transaction_no", "");
						flow_data.put("footer_label", "Upload");
						flow_data.put("registration_no", "4546456");
						flow_data.put("mobile_no", "8596469877");

						map.put("screen", "KYC_DOCUMENT_UPLOAD");
						map.put("version", version);
						map.put("data", flow_data);
						map.put("action", action);
						map.put("flow_token", flow_token);
					}
				}

				return response = mapper.writeValueAsString(map);

			} else if ("REGISTRATION_CARD".equalsIgnoreCase(screen_name)) {
				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());
				String image_skip = data.get("skip_image") == null ? "N" : data.get("skip_image").toString().trim();
				if ("Y".equals(image_skip)) {

					insertPreinspectionData(upload_transaction_no, "Skip", "Registration Card", "Skip", 101);

					Map<String, Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Upload Speedo Meter Image");
					flow_data.put("label", "Upload Speedo Meter Image");
					flow_data.put("description", "Please take a photo or browse your folder to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					flow_data.put("skip_image", IMAGE_SKIP_OPTION);
					flow_data.put("registration_no", "registration_no");
					map.put("screen", "SPEEDO_METER");
					map.put("version", version);
					map.put("data", flow_data);

					return response = mapper.writeValueAsString(map);

				} else if ("N".equals(image_skip)) {

					List<Map<String, Object>> listImage = data.get("registration_card_image") == null
							? Collections.EMPTY_LIST
							: (List<Map<String, Object>>) data.get("registration_card_image");

					Map<String, Object> image = listImage.get(0);

					String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
					String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

					String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path = file_path + file_name;

					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);
					FileUtils.writeByteArrayToFile(file, imageArray);
					Map<String, Object> flow_data = new HashMap<String, Object>();

					Boolean validation_status = false;// validateImageFile("vehicle_exterior",file);
					if (validation_status) {
						flow_data.put("title", "Upload Registration Card Image");
						flow_data.put("label", "Upload Registration Card Image");
						flow_data.put("description", "Please take a photo or browse your folder to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						flow_data.put("skip_image", IMAGE_SKIP_OPTION);

						Map<String, String> validation = new HashMap<String, String>();
						validation.put("registration_card_image",
								"Not Acceptable : The uploaded image is not a registration card.");
						flow_data.put("error_messages", validation);

						map.put("screen", screen_name);
						map.put("version", version);
						map.put("data", flow_data);

						if (file.exists())
							file.delete();
					} else {

						insertPreinspectionData(upload_transaction_no, file_path, "Registration Card", file_name, 101);
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

					// log.info("Vehicle front :: "+file_path);
					return response = mapper.writeValueAsString(map);
				}

			}
			if ("SPEEDO_METER".equalsIgnoreCase(screen_name)) {
				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());
				String image_skip = data.get("skip_image") == null ? "N" : data.get("skip_image").toString().trim();
				if ("Y".equals(image_skip)) {

					insertPreinspectionData(upload_transaction_no, "Skip", "Speedo Meter Image", "Skip", 102);

					Map<String, Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Upload Chassis Number Image");
					flow_data.put("label", "Upload Chassis Number Image");
					flow_data.put("description", "Please take a photo or browse your folder to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");
					flow_data.put("skip_image", IMAGE_SKIP_OPTION);

					map.put("screen", "CHASSIS_NUMBER");
					map.put("version", version);
					map.put("data", flow_data);

					return response = mapper.writeValueAsString(map);

				} else if ("N".equals(image_skip)) {

					List<Map<String, Object>> listImage = data.get("speedo_meter_image") == null
							? Collections.EMPTY_LIST
							: (List<Map<String, Object>>) data.get("speedo_meter_image");

					Map<String, Object> image = listImage.get(0);

					String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
					String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

					String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path = file_path + file_name;

					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);
					FileUtils.writeByteArrayToFile(file, imageArray);
					Map<String, Object> flow_data = new HashMap<String, Object>();

					Boolean validation_status = false;// validateImageFile("speedometer",file);
					if (validation_status) {
						Map<String, String> validation = new HashMap<String, String>();
						validation.put("speedo_meter_image",
								"Not Acceptable : The uploaded image is not a speedometer.");

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

						if (file.exists())
							file.delete();

					} else {

						insertPreinspectionData(upload_transaction_no, file_path, "Speedo Meter Image", file_name, 102);
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

					// log.info("Vehicle front :: "+file_path);
					return response = mapper.writeValueAsString(map);
				}

			} else if ("CHASSIS_NUMBER".equalsIgnoreCase(screen_name)) {

				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());
				String image_skip = data.get("skip_image") == null ? "N" : data.get("skip_image").toString().trim();
				if ("Y".equals(image_skip)) {

					insertPreinspectionData(upload_transaction_no, "Skip", "Chassis Number Image", "Skip", 103);

					Map<String, Object> flow_data = new HashMap<String, Object>();
					flow_data.put("title", "Vehicle Front Side Image");
					flow_data.put("label", "Upload Vehicle Front Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");

					map.put("screen", "VEHICLE_FRONT");
					map.put("version", version);
					map.put("data", flow_data);

					return response = mapper.writeValueAsString(map);

				} else if ("N".equals(image_skip)) {

					List<Map<String, Object>> listImage = data.get("chassis_number_image") == null
							? Collections.EMPTY_LIST
							: (List<Map<String, Object>>) data.get("chassis_number_image");

					Map<String, Object> image = listImage.get(0);

					String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
					String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

					String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path = file_path + file_name;

					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);
					FileUtils.writeByteArrayToFile(file, imageArray);

					Map<String, Object> flow_data = new HashMap<String, Object>();

					Boolean validation_status = false;// validateImageFile("vehicle_exterior",file);

					if (validation_status) {
						Map<String, String> validation = new HashMap<String, String>();
						validation.put("chassis_number_image",
								"Not Acceptable : The uploaded image is not a chassisnumber.");

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

						if (file.exists())
							file.delete();
					} else {

						insertPreinspectionData(upload_transaction_no, file_path, "Chassis Number Image", file_name,
								103);
						flow_data.put("title", "Vehicle Front Side Image");
						flow_data.put("label", "Upload Vehicle Front Side Image");
						flow_data.put("description", "Please take a photo to upload an image");
						flow_data.put("upload_transaction_no", upload_transaction_no.toString());
						flow_data.put("footer_label", "Upload");
						map.put("screen", "VEHICLE_FRONT");
						map.put("version", version);
						map.put("data", flow_data);

					}

					// log.info("Vehicle front :: "+file_path);
					return response = mapper.writeValueAsString(map);
				}

			} else if ("VEHICLE_FRONT".equalsIgnoreCase(screen_name)) {

				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String, Object>> listImage = data.get("vehicle_front") == null ? Collections.EMPTY_LIST
						: (List<Map<String, Object>>) data.get("vehicle_front");

				Map<String, Object> image = listImage.get(0);

				String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
				String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

				String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path = file_path + file_name;

				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);
				FileUtils.writeByteArrayToFile(file, imageArray);
				Map<String, Object> flow_data = new HashMap<String, Object>();

				Boolean validation_status = false;// validateImageFile("vehicle_exterior",file);
				if (validation_status) {
					Map<String, String> validation = new HashMap<String, String>();
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

					if (file.exists())
						file.delete();
				} else {

					insertPreinspectionData(upload_transaction_no, file_path, "Front Vehicle Image", file_name, 104);
					flow_data.put("title", "Vehicle Back Side Image");
					flow_data.put("label", "Upload Vehicle Back Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");

					map.put("screen", "VEHICLE_BACK");
					map.put("version", version);
					map.put("data", flow_data);

				}

				// log.info("Vehicle front :: "+file_path);
				return response = mapper.writeValueAsString(map);

			} else if ("VEHICLE_BACK".equalsIgnoreCase(screen_name)) {

				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String, Object>> listImage = data.get("vehicle_back") == null ? Collections.EMPTY_LIST
						: (List<Map<String, Object>>) data.get("vehicle_back");

				Map<String, Object> image = listImage.get(0);

				String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
				String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

				String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path = file_path + file_name;

				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);
				FileUtils.writeByteArrayToFile(file, imageArray);

				Map<String, Object> flow_data = new HashMap<String, Object>();

				Boolean validation_status = false;// validateImageFile("vehicle_exterior",file);
				if (validation_status) {
					Map<String, String> validation = new HashMap<String, String>();
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

					if (file.exists())
						file.delete();
				} else {

					insertPreinspectionData(upload_transaction_no, file_path, "Back Vehicle Image", file_name, 105);
					flow_data.put("title", "Vehicle Right Side Image");
					flow_data.put("label", "Upload Vehicle Right Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");

					map.put("screen", "VEHICLE_RIGHT");
					map.put("version", version);
					map.put("data", flow_data);

				}

				// log.info("Vehicle Back :: "+file_path);

				return response = mapper.writeValueAsString(map);

			} else if ("VEHICLE_RIGHT".equalsIgnoreCase(screen_name)) {

				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String, Object>> listImage = data.get("vehicle_right") == null ? Collections.EMPTY_LIST
						: (List<Map<String, Object>>) data.get("vehicle_right");

				Map<String, Object> image = listImage.get(0);

				String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
				String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

				String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path = file_path + file_name;

				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);
				FileUtils.writeByteArrayToFile(file, imageArray);

				Map<String, Object> flow_data = new HashMap<String, Object>();

				Boolean validation_status = false;// validateImageFile("vehicle_exterior",file);
				if (validation_status) {
					Map<String, String> validation = new HashMap<String, String>();
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

					if (file.exists())
						file.delete();
				} else {

					insertPreinspectionData(upload_transaction_no, file_path, "Side Vehicle Image", file_name, 106);
					flow_data.put("title", "Vehicle Left Side Image");
					flow_data.put("label", "Upload Vehicle Left Side Image");
					flow_data.put("description", "Please take a photo to upload an image");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());
					flow_data.put("footer_label", "Upload");

					map.put("screen", "VEHICLE_LEFT");
					map.put("version", version);
					map.put("data", flow_data);

				}

				// log.info("Vehicle Right :: "+file_path);

				return response = mapper.writeValueAsString(map);

			} else if ("VEHICLE_LEFT".equalsIgnoreCase(screen_name)) {

				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String, Object>> listImage = data.get("vehicle_left") == null ? Collections.EMPTY_LIST
						: (List<Map<String, Object>>) data.get("vehicle_left");

				Map<String, Object> image = listImage.get(0);

				String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
				String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

				String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
				file_path = file_path + file_name;

				byte imageArray[] = imageDecrypt.decryptMedia(image);
				File file = new File(file_path);
				FileUtils.writeByteArrayToFile(file, imageArray);

				Map<String, Object> flow_data = new HashMap<String, Object>();

				// log.info("Vehicle Right :: "+file_path);
				Boolean validation_status = false;// validateImageFile("vehicle_exterior",file);
				if (validation_status) {
					Map<String, String> validation = new HashMap<String, String>();
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

					if (file.exists())
						file.delete();
				} else {

					insertPreinspectionData(upload_transaction_no, file_path, "Bottom Vehicle Image", file_name, 107);
					flow_data.put("header", "Alliance Insurance Corportation Limted");
					flow_data.put("response_text",
							"Your documents has been received. Thank your for using alliance bot");
					flow_data.put("title", "Thank you....!");
					flow_data.put("upload_transaction_no", upload_transaction_no.toString());

					map.put("screen", "END_SCREEN");
					map.put("version", version);
					map.put("data", flow_data);

				}

				return response = mapper.writeValueAsString(map);

			} else if ("INPUT_TYPE".equalsIgnoreCase(component_action)) {

				String inputType = data.get("input_type") == null ? "" : data.get("input_type").toString();
				Map<String, Object> inputTypeRes = new HashMap<String, Object>();
				inputTypeRes.put("isInputDataReq", true);
				inputTypeRes.put("isVisibleInputData", true);
				if ("1".equals(inputType))
					inputTypeRes.put("inputdata_lable_name", "Registration Number");
				else if ("2".equals(inputType))
					inputTypeRes.put("inputdata_lable_name", "Chassis Number");

				map.put("screen", screen_name);
				map.put("version", version);
				map.put("data", inputTypeRes);

				response = this.mapper.writeValueAsString(map);

				return response;
			} else if ("KYC_DOCUMENT_UPLOAD".equalsIgnoreCase(component_action)) {

				Long upload_transaction_no = data.get("upload_transaction_no") == null ? null
						: Long.valueOf(data.get("upload_transaction_no").toString());

				List<Map<String, Object>> listImage = data.get("kyc_documents") == null ? Collections.EMPTY_LIST
						: (List<Map<String, Object>>) data.get("kyc_documents");

				log.info("KYC_DOCUMENT_UPLOAD document count is " + listImage.size() + "");

				for (Map<String, Object> image : listImage) {

					String file_name = image.get("file_name") == null ? "" : image.get("file_name").toString().trim();
					String media_id = image.get("media_id") == null ? "" : image.get("media_id").toString();

					String file_path = cs.getwebserviceurlProperty().getProperty("meta.image.store.path");
					file_path = file_path + file_name;

					byte imageArray[] = imageDecrypt.decryptMedia(image);
					File file = new File(file_path);

					FileUtils.writeByteArrayToFile(file, imageArray);

					insertPreinspectionData(upload_transaction_no, file_path, file_name, file_name, 500);
				}

				Map<String, Object> flow_data = new HashMap<String, Object>();
				flow_data.put("header", "Alliance Insurance Corportation Limted");
				flow_data.put("response_text", "Your documents has been received. Thank your for using alliance bot");
				flow_data.put("title", "Thank you....!");
				flow_data.put("upload_transaction_no", upload_transaction_no.toString());

				map.put("screen", "END_SCREEN");
				map.put("version", version);
				map.put("data", flow_data);

				return response = mapper.writeValueAsString(map);

			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Map<String, Object> preinspectionScreenData(String mobile_no) {
		try {
			String image_url = "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";
			Map<String, Object> flow_data = new HashMap<String, Object>();
			flow_data.put("title", "Welcome");
			flow_data.put("label", "Upload Vehicle Front Image");
			flow_data.put("input_type", PRE_DROPDOWN_DATA);
			flow_data.put("mobile_no", mobile_no);
			flow_data.put("isVisibleInputData", false);
			flow_data.put("isInputDataReq", false);
			flow_data.put("inputdata_lable_name", "Testing");
			flow_data.put("inputdata_lable_name", "Testing");
			flow_data.put("image_url", image_url);
			Map<String, Object> flow_action_payload = new HashMap<String, Object>();
			flow_action_payload.put("screen", "WELCOME_SCREEN");
			flow_action_payload.put("data", flow_data);
			// flow_action_payload.put("version", "3.0");
			System.out.println(mapper.writeValueAsString(flow_action_payload));

			return flow_action_payload;

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	private PreinspectionDataDetail insertPreinspectionData(String input_type, String input_data, String mobile_no,
			String docType) {
		try {
			Long tranId = preInsDataRepo.getTranId();
			PreinspectionDataDetail pdd = PreinspectionDataDetail.builder()
					.registrationNo("1".equals(input_type) ? input_data : null)
					.chassisNo("2".equals(input_type) ? input_data : null).entry_date(new Date()).status("Y")
					.tranId(tranId).mobileNo(mobile_no).documnetType(docType).build();

			PreinspectionDataDetail pddsave = preInsDataRepo.save(pdd);
			return pddsave;
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return null;
	}

	private void insertPreinspectionData(Long tran_id, String file_path, String image_name, String file_name,
			Integer doc_id) {
		try {
			PreinspectionImageDetail pmd = PreinspectionImageDetail.builder().tranId(tran_id).imageFilePath(file_path)
					.imageName(image_name).entry_date(new Date()).status("Y").originalFileName(file_name)
					.exifImageStatus("VALID").docId(doc_id).build();

			pidiRepo.save(pmd);
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
	}

	public Boolean validateImageFile(String file_type, File file) {
		try {
			String responseString = "";
			Response response = null;

			// for geting bearer token
			RequestBody token_body = new FormBody.Builder().add("username", python_image_token_username)
					.add("password", python_image_token_password).build();

			Request token_request = new Request.Builder().url(python_image_token_api)
					.addHeader("Content-Type", "application/x-www-form-urlencoded").post(token_body).build();

			response = okhttp.newCall(token_request).execute();
			responseString = response.body().string();

			@SuppressWarnings("unchecked")
			Map<String, Object> token_result = mapper.readValue(responseString, Map.class);
			String authorization = "Bearer " + token_result.get("access_token").toString();

			// Create multipart body
			MultipartBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
					.addFormDataPart("file_type", file_type)
					.addFormDataPart("file", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg")))
					.build();

			// Create request
			Request image_request = new Request.Builder().url(python_image_validate_api).post(requestBody)
					.addHeader("Authorization", authorization).build();

			response = okhttp.newCall(image_request).execute();
			responseString = response.body().string();

			@SuppressWarnings("unchecked")
			Map<String, Object> image_result = mapper.readValue(responseString, Map.class);
			String response_code = image_result.get("Status").toString();
			System.out.println(mapper.writeValueAsString(image_result));

			if ("200".equals(response_code)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
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
			Map<String, Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version") == null ? "" : request.get("version").toString();
			String screen = request.get("screen") == null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String flow_token = request.get("flow_token") == null ? "" : request.get("flow_token").toString();

			Map<String, Object> return_response = new HashMap<String, Object>();
			return_response.put("version", version);
			return_response.put("screen", screen);

			String sample_data = "[ {\"id\": \"0\", \"title\": \"--SELECT--\"} ]";
			String error_messages_1 = " {\"id\": \"\", \"\": \"\"}";
			List<Map<String, Object>> list = mapper.readValue(sample_data, List.class);

			String token = this.thread.getEwayToken();

			Map<String, String> input_validation = new HashMap<>();
			if ("VEHILCE_VALIDATION".equalsIgnoreCase(component_action)) {
				String chassis_number = data.get("chassis_number") == null ? ""
						: data.get("chassis_number").toString().trim();
				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String registration_number = data.get("registration_number") == null ? ""
						: data.get("registration_number").toString().trim();
				String engine_number = data.get("engine_number") == null ? ""
						: data.get("engine_number").toString().trim();
				String vehicle_make = data.get("vehicle_make") == null ? ""
						: data.get("vehicle_make").toString().trim();
				String vehicle_model = data.get("vehicle_model") == null ? ""
						: data.get("vehicle_model").toString().trim();
				String engine_capacity = data.get("engine_capacity") == null ? ""
						: data.get("engine_capacity").toString().trim();
				String manufacture_year = data.get("manufacture_year") == null ? ""
						: data.get("manufacture_year").toString().trim();
				String fuel_used = data.get("fuel_used") == null ? "" : data.get("fuel_used").toString().trim();
				String motor_category = data.get("motor_category") == null ? ""
						: data.get("motor_category").toString().trim();
				String vehicle_color = data.get("vehicle_color") == null ? ""
						: data.get("vehicle_color").toString().trim();
				String vehicle_usage = data.get("vehicle_usage") == null ? ""
						: data.get("vehicle_usage").toString().trim();
				String seating_capacity = data.get("seating_capacity") == null ? ""
						: data.get("seating_capacity").toString().trim();
				String tare_weight = data.get("tare_weight") == null ? "" : data.get("tare_weight").toString().trim();
				String gross_weight = data.get("gross_weight") == null ? ""
						: data.get("gross_weight").toString().trim();
				String no_of_axle = data.get("no_of_axle") == null ? "" : data.get("no_of_axle").toString().trim();
				String axle_distance = data.get("axle_distance") == null ? ""
						: data.get("axle_distance").toString().trim();

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number") == null ? ""
						: data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				// validation message must not greater than 30 characters.

				if (chassis_number.length() < 5) {
					input_validation.put("chassis_number", "Minimum characters required.");
				} else if (!chassis_number.matches("[a-zA-Z0-9]+")) {
					input_validation.put("chassis_number", "Special characters not allowed.");
				}
				if (!registration_number.matches("[a-zA-Z0-9]+")) {
					input_validation.put("registration_number", "Special characters not allowed.");
				}
				if (!engine_capacity.matches("[0-9]+")) {
					input_validation.put("engine_capacity", "digits only allowed");
				}
				if (!engine_number.matches("[0-9]+")) {
					input_validation.put("engine_number", "digits only allowed");
				}
				if (!tare_weight.matches("[0-9]+")) {
					input_validation.put("tare_weight", "digits only allowed");
				} else if (!tare_weight.matches("[0-9]+")) {
					input_validation.put("tare_weight", "digits only allowed");
				}
				if (!gross_weight.matches("[0-9]+")) {
					input_validation.put("gross_weight", "digits only allowed");
				} else if (!gross_weight.matches("[0-9]+")) {
					input_validation.put("gross_weight", "digits only allowed");
				}
				if (!no_of_axle.matches("[0-9]+")) {
					input_validation.put("no_of_axle", "digits only allowed");
				}
				if (!axle_distance.matches("[0-9]+")) {
					input_validation.put("axle_distance", "digits only allowed");
				}
				if (!seating_capacity.matches("[0-9]+")) {
					input_validation.put("seating_capacity", "digits only allowed");
				} /*
					 * else { Map<String,String> request_map = new HashMap<String, String>();
					 * request_map.put("Type", "SeatingCapacity");
					 * request_map.put("SeatingCapacity", seating_capacity );
					 * request_map.put("InsuranceId", "100019"); request_map.put("BranchCode",
					 * "55"); request_map.put("BodyType",body_type ); String
					 * ewayValidation=wh_get_ewaydata_api;
					 * api_response=thread.callEwayApi(ewayValidation,
					 * mapper.writeValueAsString(request_map), token); Map<String,Object> map =
					 * mapper.readValue(api_response, Map.class); Boolean status = (Boolean)
					 * map.get("IsError"); if(status) { Map<String,Object> seat_map
					 * =(Map<String,Object>) map.get("Result"); String seats
					 * =seat_map.get("SeatingCapacity").toString();
					 * input_validation.put("seating_capacity",
					 * "should be under "+seats+" or equal "); }
					 */

				// checking validation data
				if (!input_validation.isEmpty() && input_validation.size() > 0) {
					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					request_map.put("BodyId", body_type);
					request_map.put("MakeId", vehicle_make);

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> fuel_type_e = thread.getFuelType(request_1, token);
					CompletableFuture<List<Map<String, String>>> color_e = thread.getColor(request_1, token);
					CompletableFuture<List<Map<String, String>>> manufacture_year_e = thread.getManuFactureYear();
					CompletableFuture<List<Map<String, String>>> body_type_e = thread.getSTPBodyType(request_1, token);
					CompletableFuture<List<Map<String, String>>> vehicle_usage_e = thread.getSTPVehicleUsage(request_1,
							token);
					CompletableFuture<List<Map<String, String>>> make_e = thread.getStpMake(token, body_type);
					// CompletableFuture<List<Map<String,String>>> model_e
					// =thread.getSTPModel(body_type,vehicle_make,token);

					CompletableFuture
							.allOf(fuel_type_e, color_e, manufacture_year_e, body_type_e, vehicle_usage_e, make_e)
							.join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("body_type", body_type_e.get().isEmpty() ? SAMPLE_DATA : body_type_e.get());
					error_messages.put("body_make", make_e.get().isEmpty() ? SAMPLE_DATA : make_e.get());
					error_messages.put("vehicle_model", vehicle_model);
					error_messages.put("manufacture_year",
							manufacture_year_e.get().isEmpty() ? SAMPLE_DATA : manufacture_year_e.get());
					error_messages.put("fuel_used", fuel_type_e.get().isEmpty() ? SAMPLE_DATA : fuel_type_e.get());
					error_messages.put("vehicle_usage",
							vehicle_usage_e.get().isEmpty() ? SAMPLE_DATA : vehicle_usage_e.get());
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
					response = printReq.toJson(return_response);
					return response;
				} else {
					Map<String, Object> map_policy = new HashMap<String, Object>();
					Map<String, String> save_details = new HashMap<String, String>();

					save_details.put("Insuranceid", "100019");
					save_details.put("BranchCode", "55");
					save_details.put("AxelDistance", axle_distance);
					save_details.put("Chassisnumber", chassis_number);
					save_details.put("Color", vehicle_color);
					save_details.put("CreatedBy", "ugandabroker3");
					save_details.put("EngineNumber", engine_number);
					save_details.put("FuelType", fuel_used);
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
					save_details.put("RegistrationDate", null);

					String saveVehicle = wh_save_vehicle_info_api;
					api_response = thread.callEwayApi(saveVehicle, mapper.writeValueAsString(save_details), token);
					Map<String, Object> map = mapper.readValue(api_response, Map.class);
					String status = map.get("Message").toString();
					if ("Success".equalsIgnoreCase(status)) {

						Map<String, String> request_map = new HashMap<String, String>();
						request_map.put("BranchCode", "55");
						request_map.put("InsuranceId", "100019");

						String request_1 = printReq.toJson(request_map);

						//CompletableFuture<List<Map<String, String>>> insurance_type_1 = thread
								//.getInsuranceType(body_type, vehicle_make, token);
						CompletableFuture<List<Map<String, String>>> insurance_class_1 = thread
								.getInsuranceClass(token);
						CompletableFuture<List<Map<String, String>>> body_type_policy_1 = thread
								.getSTPBodyType(request_1, token);
						CompletableFuture<List<Map<String, String>>> vehicle_usage_policy_1 = thread
								.getSTPVehicleUsage(request_1, token);

						//CompletableFuture
								//.allOf(insurance_type_1, insurance_class_1, body_type_policy_1, vehicle_usage_policy_1)
								//.join();

						/*
						 * map_policy.put("chassis_number", chassis_number); map_policy.put("body_type",
						 * body_type); map_policy.put("registration_number", registration_number);
						 * map_policy.put("engine_number", engine_number);
						 * map_policy.put("vehicle_make", vehicle_make);
						 * map_policy.put("engine_capacity", engine_capacity);
						 * map_policy.put("manufacture_year", manufacture_year);
						 * map_policy.put("fuel_used", fuel_used); map_policy.put("vehicle_model",
						 * vehicle_model); map_policy.put("motor_category", motor_category);
						 * map_policy.put("vehicle_color", vehicle_color);
						 * map_policy.put("vehicle_usage", vehicle_usage);
						 * map_policy.put("seating_capacity", seating_capacity);
						 * map_policy.put("tare_weight", tare_weight); map_policy.put("gross_weight",
						 * gross_weight); map_policy.put("no_of_axle", no_of_axle);
						 * map_policy.put("axle_distance", axle_distance); map_policy.put("flow_token",
						 * flow_token);
						 */

						map_policy.put("title", title);
						map_policy.put("customer_name", customer_name);
						map_policy.put("country_code", country_code);
						map_policy.put("mobile_number", mobile_number);
						map_policy.put("email_id", email_id);
						map_policy.put("address", address);
						map_policy.put("region", region);

						map_policy.put("insurance_class",
								insurance_class_1.get().isEmpty() ? list : insurance_class_1.get());
						map_policy.put("body_type_policy",
								body_type_policy_1.get().isEmpty() ? list : body_type_policy_1.get());
						map_policy.put("vehicle_usage_policy",
								vehicle_usage_policy_1.get().isEmpty() ? list : vehicle_usage_policy_1.get());
						//map_policy.put("insurance_type",
								//insurance_type_1.get().isEmpty() ? list : insurance_type_1.get());
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

						Map<String, String> mapPolicy = new HashMap<>();
						map_policy.put("error_messages", mapPolicy);

						return_response.put("data", map_policy);
						return_response.put("screen", "POLICY_DETAILS");
						response = printReq.toJson(return_response);

						log.info("response" + response);
					}
					/*
					 * return_response.put("screen", "SUCCESS"); return_response.put("data",
					 * extension_message_response); response =printReq.toJson(return_response);
					 */
				}
				return response;
			}
			if ("VEHILCE_REG_VALIDATION".equalsIgnoreCase(component_action)) {

				String reg_no = data.get("reg_no") == null ? "" : data.get("reg_no").toString().trim();

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number") == null ? ""
						: data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				/*
				 * String insurance_class
				 * =data.get("insurance_class")==null?"":data.get("insurance_class").toString().
				 * trim(); String body_type_policy = data.get("body_type_policy")==null ? "" :
				 * data.get("body_type_policy").toString().trim(); String vehicle_usage_policy =
				 * data.get("vehicle_usage_policy")==null ? "" :
				 * data.get("vehicle_usage_policy").toString().trim(); String gps =
				 * data.get("gps")==null ? "" : data.get("gps").toString().trim(); String
				 * insurance_type = data.get("insurance_type")==null ? "" :
				 * data.get("insurance_type").toString().trim(); String car_alarm =
				 * data.get("car_alarm")==null ? "" : data.get("car_alarm").toString().trim();
				 * String insurance_claim = data.get("insurance_claim")==null ? "" :
				 * data.get("insurance_claim").toString().trim(); String quatation_creator =
				 * data.get("quatation_creator")==null ? "" :
				 * data.get("quatation_creator").toString().trim(); String
				 * isMandatoryBrokerLoginId = data.get("isMandatoryBrokerLoginId")==null ? "" :
				 * data.get("isMandatoryBrokerLoginId").toString().trim(); String
				 * isVisibleBrokerLoginId = data.get("isVisibleBrokerLoginId")==null ? "" :
				 * data.get("isVisibleBrokerLoginId").toString().trim(); String isVehicle_si =
				 * data.get("isVehicle_si")==null ? "" :
				 * data.get("isVehicle_si").toString().trim(); String isAccessories_si =
				 * data.get("isAccessories_si")==null ? "" :
				 * data.get("isAccessories_si").toString().trim(); String isWindshield_si =
				 * data.get("isWindshield_si")==null ? "" :
				 * data.get("isWindshield_si").toString().trim(); String extended_TPDD_si =
				 * data.get("extended_TPDD_si")==null ? "" :
				 * data.get("extended_TPDD_si").toString().trim(); String isGas =
				 * data.get("isGas")==null ? "" : data.get("isGas").toString().trim(); String
				 * isCarAlarm = data.get("isCarAlarm")==null ? "" :
				 * data.get("isCarAlarm").toString().trim(); String required_vehicle_si =
				 * data.get("isAccessories_si")==null ? "" :
				 * data.get("isAccessories_si").toString().trim(); String required_windshield_si
				 * = data.get("isWindshield_si")==null ? "" :
				 * data.get("isWindshield_si").toString().trim(); String required_accessories_si
				 * = data.get("extended_TPDD_si")==null ? "" :
				 * data.get("extended_TPDD_si").toString().trim(); String required_TPDD_si =
				 * data.get("isGas")==null ? "" : data.get("isGas").toString().trim(); String
				 * required_gps = data.get("isCarAlarm")==null ? "" :
				 * data.get("isCarAlarm").toString().trim(); String required_car_alarm =
				 * data.get("isAccessories_si")==null ? "" :
				 * data.get("isAccessories_si").toString().trim();
				 */

				if (!reg_no.matches("[a-zA-Z0-9]+")) {
					input_validation.put("reg_no", "Special characters not allowed.");
				}

				Map<String, String> reg_validatation = new HashMap<String, String>();
				reg_validatation.put("InsuranceId", "100019");
				reg_validatation.put("BranchCode", "55");
				reg_validatation.put("BrokerBranchCode", "1");
				reg_validatation.put("ProductId", "5");
				reg_validatation.put("CreatedBy", "ugandabroker3");
				reg_validatation.put("SavedFrom", "API");
				reg_validatation.put("ReqRegNumber", reg_no);
				reg_validatation.put("ReqChassisNumber", "");

				String reg_noValidationApi = wh_get_reg_no_api;
				api_response = thread.callEwayApi(reg_noValidationApi, mapper.writeValueAsString(reg_validatation),
						token);
				Map<String, Object> map = mapper.readValue(api_response, Map.class);
				Boolean status = (Boolean) map.get("IsError");

				if (status) {
					input_validation.put("reg_no", "reg no not found");

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);

					return_response.put("action", "data_exchange");
					return_response.put("data", error_messages);

					response = printReq.toJson(return_response);
					return response;
				} else {

					if (input_validation.size() > 0) {
						Map<String, String> request_map = new HashMap<String, String>();
						request_map.put("BranchCode", "55");
						request_map.put("InsuranceId", "100019");

						String request_1 = printReq.toJson(request_map);

						Map<String, Object> error_messages = new HashMap<String, Object>();
						error_messages.put("title", title);
						error_messages.put("customer_name", customer_name);
						error_messages.put("country_code", country_code);
						error_messages.put("mobile_number", mobile_number);
						error_messages.put("email_id", email_id);
						error_messages.put("address", address);
						error_messages.put("region", region);
						error_messages.put("reg_no", reg_no);

						response = printReq.toJson(return_response);
					} else {
						String bodyType = map.get("VehicleType").toString();
						String vehicleUsage = map.get("Motorusage").toString();
						Map<String, String> request_map = new HashMap<String, String>();
						request_map.put("BranchCode", "55");
						request_map.put("InsuranceId", "100019");
						request_map.put("BodyId", bodyType);

						String request_1 = printReq.toJson(request_map);

						//CompletableFuture<List<Map<String, String>>> insurance_type_1 = thread
								//.getInsuranceType(bodyType, vehicleUsage, token);
						CompletableFuture<List<Map<String, String>>> insurance_class_1 = thread
								.getInsuranceClass(token);
						CompletableFuture<List<Map<String, String>>> body_type_policy_1 = thread
								.getSTPBodyType(request_1, token);
						CompletableFuture<List<Map<String, String>>> vehicle_usage_policy_1 = thread
								.getSTPVehicleUsage(request_1, token);

						//CompletableFuture
								//.allOf(insurance_type_1, insurance_class_1, body_type_policy_1, vehicle_usage_policy_1)
								//.join();
						Map<String, Object> map_vehicle = new HashMap<String, Object>();

						map_vehicle.put("title", title);
						map_vehicle.put("customer_name", customer_name);
						map_vehicle.put("mobile_number", mobile_number);
						map_vehicle.put("email_id", email_id);
						map_vehicle.put("address", address);
						map_vehicle.put("region", region);
						map_vehicle.put("country_code", country_code);
						map_vehicle.put("reg_no", reg_no);
						map_vehicle.put("insurance_class",
								insurance_class_1.get().isEmpty() ? list : insurance_class_1.get());
						map_vehicle.put("body_type_policy",
								body_type_policy_1.get().isEmpty() ? list : body_type_policy_1.get());
						map_vehicle.put("vehicle_usage_policy",
								vehicle_usage_policy_1.get().isEmpty() ? list : vehicle_usage_policy_1.get());
						// map_vehicle.put("gps", SAMPLE_DATA);
						//map_vehicle.put("insurance_type",
								//insurance_type_1.get().isEmpty() ? list : insurance_type_1.get());
						// map_vehicle.put("car_alarm", SAMPLE_DATA);

						// map_vehicle.put("insurance_claim", SAMPLE_DATA);
						// map_vehicle.put("quatation_creator", SAMPLE_DATA);
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

						Map<String, String> errorMap = new HashMap<>();
						map_vehicle.put("error_messages", errorMap);

						return_response.put("data", map_vehicle);
						return_response.put("screen", "POLICY_DETAILS");
						response = printReq.toJson(return_response);

						log.info("response" + response);

					}
				}
				return response;

			}
			if ("quotation_creator".equalsIgnoreCase(component_action)) {
				String is_broker = data.get("quotation_creator") == null ? ""
						: data.get("quotation_creator").toString().trim();
				Map<String, Boolean> enableLogin = new HashMap<String, Boolean>();
				if ("1".equalsIgnoreCase(is_broker)) {
					enableLogin.put("isVisibleBrokerLoginId", true);
					enableLogin.put("isMandatoryBrokerLoginId", true);
				} else if ("2".equalsIgnoreCase(is_broker)) {
					enableLogin.put("isVisibleBrokerLoginId", false);
					enableLogin.put("isMandatoryBrokerLoginId", false);
				}

				return_response.put("data", enableLogin);
				response = printReq.toJson(return_response);
				return response;
			} else if ("MAKE".equalsIgnoreCase(component_action)) {

				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();

				if (StringUtils.isNotBlank(body_type)) {
					String api = this.stpMake;

					Map<String, Object> region_req = new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100019");
					region_req.put("BranchCode", "55");

					api_request = printReq.toJson(region_req);

					api_response = thread.callEwayApi(api, api_request, token);

					Map<String, Object> region_obj = mapper.readValue(api_response, Map.class);
					List<Map<String, Object>> result = (List<Map<String, Object>>) region_obj.get("Result");

					data_list = result.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p.get("Code") == null ? "" : p.get("Code").toString());
						map.put("title", p.get("CodeDesc") == null ? "" : p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				} else {
					data_list = SAMPLE_DATA;
				}
				Map<String, Object> make_list = new HashMap<String, Object>();
				make_list.put("vehicle_make", data_list);
				return_response.put("data", make_list);
				response = printReq.toJson(return_response);
				return response;
			} else if ("MODEL".equalsIgnoreCase(component_action)) {
				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String make = data.get("body_make") == null ? "" : data.get("body_make").toString().trim();
				List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();

				if (!"00000".equals(make) && StringUtils.isNotBlank(make)) {
					String api = this.stpMakeModel;

					Map<String, Object> region_req = new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100019");
					region_req.put("BranchCode", "55");
					region_req.put("MakeId", make);

					api_request = printReq.toJson(region_req);

					api_response = thread.callEwayApi(api, api_request, token);

					Map<String, Object> region_obj = mapper.readValue(api_response, Map.class);
					List<Map<String, Object>> result = (List<Map<String, Object>>) region_obj.get("Result");

					data_list = result.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p.get("Code") == null ? "" : p.get("Code").toString());
						map.put("title", p.get("CodeDesc") == null ? "" : p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				} else {
					data_list = SAMPLE_DATA;
				}
				Map<String, Object> make_list = new HashMap<String, Object>();
				make_list.put("make", data_list);
				return_response.put("data", make_list);
				response = printReq.toJson(return_response);
				return response;
			}
			/*
			 * else if ("INSURANCE".equalsIgnoreCase(component_action)) { String
			 * body_type_policy
			 * =data.get("body_type_policy")==null?"":data.get("body_type_policy").toString(
			 * ).trim(); String vehicle_usage_policy
			 * =data.get("vehicle_usage_policy")==null?"":data.get("vehicle_usage_policy").
			 * toString().trim();
			 * 
			 * List<Map<String,String>> data_list = new ArrayList<Map<String,String>>();
			 * 
			 * if(!"00000".equals(vehicle_usage_policy) &&
			 * StringUtils.isNotBlank(vehicle_usage_policy) ) { String api
			 * =this.wh_cq_policytype;
			 * 
			 * Map<String,Object> policy_req = new HashMap<String,Object>();
			 * policy_req.put("BodyId", body_type_policy); policy_req.put("InsuranceId",
			 * "100019"); policy_req.put("BranchCode", "BranchCode");
			 * policy_req.put("Motorusage", vehicle_usage_policy);
			 * 
			 * api_request =printReq.toJson(policy_req);
			 * 
			 * api_response =thread.callEwayApi(api, api_request,token);
			 * 
			 * Map<String,Object> region_obj =mapper.readValue(api_response, Map.class);
			 * List<Map<String,Object>> result
			 * =(List<Map<String,Object>>)region_obj.get("Result");
			 * 
			 * data_list = result.stream().map(p->{ Map<String,String> map = new
			 * HashMap<>(); map.put("id", p.get("Code")==null?"":p.get("Code").toString());
			 * map.put("title", p.get("CodeDesc")==null?"":p.get("CodeDesc").toString());
			 * return map; }).collect(Collectors.toList()); }else { data_list =SAMPLE_DATA;
			 * } Map<String,Object> make_list = new HashMap<String, Object>();
			 * make_list.put("make", data_list); return_response.put("data", make_list);
			 * response =printReq.toJson(return_response); return response; }
			 */

			else if ("POLICY_VALIDATION".equalsIgnoreCase(component_action)) {
				String quatation_creator = data.get("quatation_creator") == null ? ""
						: data.get("quatation_creator").toString().trim();
				String broker_loginid = data.get("broker_loginid") == null ? ""
						: data.get("broker_loginid").toString().trim();
				String insurance_type = data.get("insurance_type") == null ? ""
						: data.get("insurance_type").toString().trim();
				String insurance_class = data.get("insurance_class") == null ? ""
						: data.get("insurance_class").toString().trim();
				String body_type_policy = data.get("body_type_policy") == null ? ""
						: data.get("body_type_policy").toString().trim();
				String vehicle_usage_policy = data.get("vehicle_usage_policy") == null ? ""
						: data.get("vehicle_usage_policy").toString().trim();
				String gps = data.get("gps") == null ? "" : data.get("gps").toString().trim();
				String car_alarm = data.get("car_alarm") == null ? "" : data.get("car_alarm").toString().trim();
				String insurance_claim = data.get("insurance_claim") == null ? ""
						: data.get("insurance_claim").toString().trim();
				String vehicle_si = data.get("vehicle_si") == null ? "" : data.get("vehicle_si").toString().trim();
				String accessories_si = data.get("accessories_si") == null ? ""
						: data.get("accessories_si").toString().trim();
				String windshield_si = data.get("windshield_si") == null ? ""
						: data.get("windshield_si").toString().trim();
				String extended_TPDD_si = data.get("extended_TPDD_si") == null ? ""
						: data.get("extended_TPDD_si").toString().trim();

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number") == null ? ""
						: data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				String chassis_number = data.get("chassis_number") == null ? ""
						: data.get("chassis_number").toString().trim();
				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String registration_number = data.get("registration_number") == null ? ""
						: data.get("registration_number").toString().trim();
				String engine_number = data.get("engine_number") == null ? ""
						: data.get("engine_number").toString().trim();
				String body_make = data.get("body_make") == null ? "" : data.get("body_make").toString().trim();
				String vehicle_model = data.get("vehicle_model") == null ? ""
						: data.get("vehicle_model").toString().trim();
				String engine_capacity = data.get("engine_capacity") == null ? ""
						: data.get("engine_capacity").toString().trim();
				String manufacture_year = data.get("manufacture_year") == null ? ""
						: data.get("manufacture_year").toString().trim();
				String fuel_used = data.get("fuel_used") == null ? "" : data.get("fuel_used").toString().trim();
				String motor_category = data.get("motor_category") == null ? ""
						: data.get("motor_category").toString().trim();
				String vehicle_color = data.get("vehicle_color") == null ? ""
						: data.get("vehicle_color").toString().trim();
				String vehicle_usage = data.get("vehicle_usage") == null ? ""
						: data.get("vehicle_usage").toString().trim();
				String seating_capacity = data.get("seating_capacity") == null ? ""
						: data.get("seating_capacity").toString().trim();
				String tare_weight = data.get("tare_weight") == null ? "" : data.get("tare_weight").toString().trim();
				String gross_weight = data.get("gross_weight") == null ? ""
						: data.get("gross_weight").toString().trim();
				String no_of_axle = data.get("no_of_axle") == null ? "" : data.get("no_of_axle").toString().trim();
				String axle_distance = data.get("axle_distance") == null ? ""
						: data.get("axle_distance").toString().trim();

				if ("1".equalsIgnoreCase(quatation_creator)) {
					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("Type", "LOGIN_ID_CHECK");
					request_map.put("LoginId", broker_loginid);
					String ewayValidation = wh_get_ewaydata_api;
					api_response = thread.callEwayApi(ewayValidation, mapper.writeValueAsString(request_map), token);
					Map<String, Object> map = mapper.readValue(api_response, Map.class);
					Boolean status = (Boolean) map.get("IsError");

					if (status) {
						input_validation.put("broker_loginid", "Broker Login Id not valid");
					}
				}
				if (insurance_class != null) {
					Map<String, Object> hide_suminsured = new HashMap<String, Object>();
					if ("1".equals(insurance_class) || "2".equals(insurance_class)) {
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
					} else {
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

					response = printReq.toJson(return_response);
					return response;
				}
				// validation check
				if (!input_validation.isEmpty() && input_validation.size() > 0) {

					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					request_map.put("BodyId", body_type_policy);

					String request_1 = printReq.toJson(request_map);

					//CompletableFuture<List<Map<String, String>>> insurance_type_1 = thread
							//.getInsuranceType(body_type_policy, vehicle_usage_policy, token);
					CompletableFuture<List<Map<String, String>>> insurance_class_1 = thread.getInsuranceClass(token);
					CompletableFuture<List<Map<String, String>>> body_type_policy_1 = thread.getSTPBodyType(request_1,
							token);
					CompletableFuture<List<Map<String, String>>> vehicle_usage_policy_1 = thread
							.getSTPVehicleUsage(request_1, token);

					CompletableFuture.allOf(body_type_policy_1, vehicle_usage_policy_1).join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					//error_messages.put("insurance_type",
							//insurance_type_1.get().isEmpty() ? SAMPLE_DATA : insurance_type_1.get());
					error_messages.put("insurance_class",
							insurance_class_1.get().isEmpty() ? SAMPLE_DATA : insurance_class_1.get());
					error_messages.put("body_type_policy",
							body_type_policy_1.get().isEmpty() ? SAMPLE_DATA : body_type_policy_1.get());
					error_messages.put("vehicle_usage_policy",
							vehicle_usage_policy_1.get().isEmpty() ? SAMPLE_DATA : vehicle_usage_policy_1.get());
					error_messages.put("gps", gps);
					error_messages.put("car_alarm", car_alarm);
					error_messages.put("insurance_claim", insurance_claim);
					error_messages.put("vehicle_si", vehicle_si);
					error_messages.put("accessories_si", accessories_si);
					error_messages.put("windshield_si", windshield_si);
					error_messages.put("extended_TPDD_si", extended_TPDD_si);

					return_response.put("action", "data_exchange");
					return_response.put("data", error_messages);

					response = printReq.toJson(return_response);
				} else {
					Map<String, Object> extension_message_response = new HashMap<String, Object>();
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String, Object> param_map = new HashMap<String, Object>();

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

					response = printReq.toJson(return_response);

				}
			} else if ("CUSTOMER_SCREEN".equalsIgnoreCase(component_action)) {
				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number") == null ? ""
						: data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				if (!customer_name.matches("[a-zA-Z]+")) {
					input_validation.put("customer_name", "Please enter valid name");
				}
				if (!mobile_number.matches("[0-9]+")) {
					input_validation.put("mobile_number", "Please enter valid mobile number");
				} else if (!mobile_number.matches("0?[0-9]{9}")) {
					input_validation.put("mobile_number", "MobileNo should be 9 digits");
				}
				if (!email_id.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
					input_validation.put("email_id", "Please enter valid email format");
				}

				if (input_validation.size() > 0) {
					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> title_1 = thread.getCustomerTitle(request_1, token);
					CompletableFuture<List<Map<String, String>>> country_code_1 = thread
							.getCustomerCountryCode(request_1, token);
					CompletableFuture<List<Map<String, String>>> region_1 = thread.getCustomerRegion(token);

					CompletableFuture.allOf(title_1, country_code_1, region_1).join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("title", title_1.get().isEmpty() ? SAMPLE_DATA : title_1.get());
					error_messages.put("country_code",
							country_code_1.get().isEmpty() ? SAMPLE_DATA : country_code_1.get());
					error_messages.put("region", region_1.get().isEmpty() ? SAMPLE_DATA : region_1.get());
					return_response.put("action", "data_exchange");
					return_response.put("data", error_messages);

					response = printReq.toJson(return_response);
				} else {
					/*
					 * Map<String,String> request_map = new HashMap<String, String>();
					 * request_map.put("BranchCode", "01"); request_map.put("InsuranceId",
					 * "100002");
					 * 
					 * String request_1 =printReq.toJson(request_map);
					 * 
					 * CompletableFuture<List<Map<String,String>>> fuel_type
					 * =thread.getFuelType(request_1,token);
					 * CompletableFuture<List<Map<String,String>>> color
					 * =thread.getColor(request_1,token);
					 * CompletableFuture<List<Map<String,String>>> manufacture_year
					 * =thread.getManuFactureYear(); CompletableFuture<List<Map<String,String>>>
					 * body_type =thread.getSTPBodyType(request_1,token);
					 * CompletableFuture<List<Map<String,String>>> vehicle_usage
					 * =thread.getSTPVehicleUsage(request_1,token);
					 * CompletableFuture<List<Map<String,String>>> motor_category
					 * =thread.getMotorCategory(request_1,token);
					 * 
					 * 
					 * CompletableFuture.allOf(fuel_type,color,manufacture_year,
					 * body_type,vehicle_usage).join();
					 */

					Map<String, Object> map_vehicle = new HashMap<String, Object>();

					map_vehicle.put("new_registration", "Create new vehicle");
					map_vehicle.put("search_heading", "Search for your vehicle by Registration Number here.");

					map_vehicle.put("title", title);
					map_vehicle.put("customer_name", customer_name);
					map_vehicle.put("mobile_number", mobile_number);
					map_vehicle.put("email_id", email_id);
					map_vehicle.put("address", address);
					map_vehicle.put("region", region);
					map_vehicle.put("country_code", country_code);
					// map_vehicle.put("fuel_used", fuel_type.get().isEmpty()?list:fuel_type.get());
					// map_vehicle.put("body_type", body_type.get().isEmpty()?list:body_type.get());
					// map_vehicle.put("body_make", list);
					// map_vehicle.put("vehicle_model", list);
					// map_vehicle.put("manufacture_year",
					// manufacture_year.get().isEmpty()?list:manufacture_year.get());
					// map_vehicle.put("vehicle_color", color.get().isEmpty()?list:color.get());
					//// map_vehicle.put("vehicle_usage",
					// vehicle_usage.get().isEmpty()?list:vehicle_usage.get());
					// map_vehicle.put("motor_category",
					// motor_category.get().isEmpty()?list:motor_category.get());
					// map_vehicle.put("isVisibleBrokerLoginId", false);
					// map_vehicle.put("isMandatoryBrokerLoginId", false);
					Map<String, String> map = new HashMap<>();
					map_vehicle.put("error_messages", map);

					return_response.put("data", map_vehicle);
					return_response.put("screen", "VEHICLE_DETAILS");
					response = printReq.toJson(return_response);

					log.info("response" + response);
				}
				return response;
			} else if ("CREATE_VEHICLE".equalsIgnoreCase(component_action)) {

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number") == null ? ""
						: data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();
				String reg_no = data.get("reg_no") == null ? "" : data.get("reg_no").toString().trim();

				Map<String, Object> map_newVehicle = new HashMap<String, Object>();

				Map<String, String> request_map = new HashMap<String, String>();
				request_map.put("BranchCode", "55");
				request_map.put("InsuranceId", "100019");

				String request_1 = printReq.toJson(request_map);

				CompletableFuture<List<Map<String, String>>> fuel_type = thread.getFuelType(request_1, token);
				CompletableFuture<List<Map<String, String>>> color = thread.getColor(request_1, token);
				CompletableFuture<List<Map<String, String>>> manufacture_year = thread.getManuFactureYear();
				CompletableFuture<List<Map<String, String>>> body_type = thread.getSTPBodyType(request_1, token);
				CompletableFuture<List<Map<String, String>>> vehicle_usage = thread.getSTPVehicleUsage(request_1,
						token);
				CompletableFuture<List<Map<String, String>>> motor_category = thread.getMotorCategory(request_1, token);
				// CompletableFuture<List<Map<String,String>>> make_e =thread.getStpMake(token,
				// body_type);

				CompletableFuture.allOf(fuel_type, color, manufacture_year, body_type, vehicle_usage).join();

				map_newVehicle.put("fuel_used", fuel_type.get().isEmpty() ? list : fuel_type.get());
				map_newVehicle.put("body_type", body_type.get().isEmpty() ? list : body_type.get());
				map_newVehicle.put("vehicle_make", list);
				map_newVehicle.put("manufacture_year",
						manufacture_year.get().isEmpty() ? list : manufacture_year.get());
				map_newVehicle.put("vehicle_color", color.get().isEmpty() ? list : color.get());
				map_newVehicle.put("vehicle_usage", vehicle_usage.get().isEmpty() ? list : vehicle_usage.get());
				map_newVehicle.put("motor_category", motor_category.get().isEmpty() ? list : motor_category.get());

				map_newVehicle.put("title", title);
				map_newVehicle.put("customer_name", customer_name);
				map_newVehicle.put("mobile_number", mobile_number);
				map_newVehicle.put("email_id", email_id);
				map_newVehicle.put("address", address);
				map_newVehicle.put("region", region);
				map_newVehicle.put("country_code", country_code);
				map_newVehicle.put("reg_no", reg_no);
				map_newVehicle.put("error_messages", mapper.readValue(error_messages_1, Map.class));

				/*
				 * map_newVehicle.put("body_type", SAMPLE_DATA);
				 * map_newVehicle.put("vehicle_make", SAMPLE_DATA);
				 * map_newVehicle.put("manufacture_year", SAMPLE_DATA);
				 * map_newVehicle.put("fuel_used", SAMPLE_DATA);
				 * map_newVehicle.put("motor_category", SAMPLE_DATA);
				 * map_newVehicle.put("vehicle_color", SAMPLE_DATA);
				 * map_newVehicle.put("vehicle_usage", SAMPLE_DATA);
				 */

				Map<String, String> map = new HashMap<>();
				map_newVehicle.put("error_messages", map);

				return_response.put("data", map_newVehicle);
				return_response.put("screen", "VEHICLE_INFORMATION");
				response = printReq.toJson(return_response);

				log.info("response" + response);
			} else if ("SEARCH_VEHICLE".equalsIgnoreCase(component_action)) {
				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_number = data.get("mobile_number") == null ? ""
						: data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();
				String reg_no = data.get("reg_no") == null ? "" : data.get("reg_no").toString().trim();

				Map<String, Object> map_backPage = new HashMap<String, Object>();

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
				response = printReq.toJson(return_response);

				log.info("response" + response);

			}

		} catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		}
		return response;
	}

	@Override
	public String createQuote(Map<String, Object> request) {
		String response = "";
		String api_response = "";
		String api_request = "";

		try {
			Map<String, Object> data = (Map<String, Object>) request.get("data");
			String version = request.get("version") == null ? "" : request.get("version").toString();
			String screen_name = request.get("screen") == null ? "" : request.get("screen").toString();
			String component_action = data.get("component_action") == null ? ""
					: data.get("component_action").toString();
			String flow_token = request.get("flow_token") == null ? "" : request.get("flow_token").toString();
			Map<String, Object> return_res = new HashMap<String, Object>();
			return_res.put("version", version);
			return_res.put("screen", screen_name);
			Map<String, String> input_validation = new HashMap<>();

			String sample_data = "[ {\"id\": \"0\", \"title\": \"--SELECT--\"} ]";
			String error_messages_1 = " {\"id\": \"\", \"\": \"\"}";
			List<Map<String, Object>> list = mapper.readValue(sample_data, List.class);

			String token = this.thread.getEwayToken();

			if ("CUSTOMER_CREATION".equalsIgnoreCase(component_action)) {
				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_no = data.get("mobile_number") == null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				if (!customer_name.matches("[a-zA-Z ]+")) {
					input_validation.put("customer_name", "Please enter valid name");
				}
				if (!mobile_no.matches("[0-9]+")) {
					input_validation.put("mobile_number", "Please enter valid mobile");
				} else if (!mobile_no.matches("0?[0-9]{9}")) {
					input_validation.put("mobile_number", "MobileNo should be 9 digits");
				}

				if (input_validation.size() > 0) {

					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> title_1 = thread.getCustomerTitle(request_1, token);
					CompletableFuture<List<Map<String, String>>> country_code_1 = thread
							.getCustomerCountryCode(request_1, token);
					CompletableFuture<List<Map<String, String>>> region_1 = thread.getCustomerRegion(token);

					CompletableFuture.allOf(title_1, country_code_1, region_1).join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("title", title_1.get().isEmpty() ? SAMPLE_DATA : title_1.get());
					error_messages.put("country_code",
							country_code_1.get().isEmpty() ? SAMPLE_DATA : country_code_1.get());
					error_messages.put("region", region_1.get().isEmpty() ? SAMPLE_DATA : region_1.get());
					return_res.put("action", "data_exchange");
					return_res.put("data", error_messages);

					response = printReq.toJson(return_res);

					return response;

				} else {

					Map<String, Object> map_vehicle = new HashMap<String, Object>();
					Map<String, Object> error_messages = new HashMap<String, Object>();

					map_vehicle.put("title", title);
					map_vehicle.put("customer_name", customer_name);
					map_vehicle.put("mobile_number", mobile_no);
					map_vehicle.put("email_id", email_id);
					map_vehicle.put("address", address);
					map_vehicle.put("region", region);
					map_vehicle.put("country_code", country_code);
					map_vehicle.put("embedded_link", "Add New Vehicle");
					map_vehicle.put("search_heading", "Search for your vehicle by Registration Number here.");
					map_vehicle.put("error_messages", error_messages);

					return_res.put("data", map_vehicle);
					return_res.put("screen", "VEHICLE_DETAILS");
					response = printReq.toJson(return_res);
					return response;
				}

			} else if ("SEARCH_VEHICLE".equalsIgnoreCase(component_action)) {

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_no = data.get("mobile_number") == null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();
				String registration_no = data.get("registration_no") == null ? ""
						: data.get("registration_no").toString().trim();

				if (!registration_no.matches("[a-zA-Z0-9]+")) {
					input_validation.put("registration_no", "Special characters not allowed.");
				}

				// api call need to write for checking reg no
				Map<String, String> reg_validatation = new HashMap<String, String>();
				reg_validatation.put("InsuranceId", "100019");
				reg_validatation.put("BranchCode", "55");
				reg_validatation.put("BrokerBranchCode", "1");
				reg_validatation.put("ProductId", "5");
				reg_validatation.put("CreatedBy", "ugandabroker3");
				reg_validatation.put("SavedFrom", "API");
				reg_validatation.put("ReqRegNumber", registration_no);
				reg_validatation.put("ReqChassisNumber", "");

				String reg_noValidationApi = wh_get_reg_no_api;
				api_response = thread.callEwayApi(reg_noValidationApi, mapper.writeValueAsString(reg_validatation),
						token);
				Map<String, Object> map = mapper.readValue(api_response, Map.class);
				Boolean status = (Boolean) map.get("IsError");

				if (status) {
					input_validation.put("registration_no", "reg no not found");

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);

					return_res.put("action", "data_exchange");
					return_res.put("data", error_messages);

					response = printReq.toJson(return_res);
					return response;
					
				} else {

					//String bodyType = map.get("VehicleType").toString();
					//String vehicleUsage = map.get("Motorusage").toString();
					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					//request_map.put("BodyId", bodyType);

					String request_1 = printReq.toJson(request_map);

					//CompletableFuture<List<Map<String, String>>> insurance_type_1 = thread.getInsuranceType(bodyType,
							//vehicleUsage, token);
					CompletableFuture<List<Map<String, String>>> insurance_class_1 = thread.getInsuranceClass(token);
					CompletableFuture<List<Map<String, String>>> body_type_policy_1 = thread.getSTPBodyType(request_1,
							token);
					CompletableFuture<List<Map<String, String>>> vehicle_usage_policy_1 = thread
							.getSTPVehicleUsage(request_1, token);

					CompletableFuture
							.allOf(insurance_class_1, body_type_policy_1, vehicle_usage_policy_1)
							.join();
					Map<String, Object> map_vehicle = new HashMap<String, Object>();

					map_vehicle.put("title", title);
					map_vehicle.put("customer_name", customer_name);
					map_vehicle.put("mobile_number", mobile_no);
					map_vehicle.put("email_id", email_id);
					map_vehicle.put("address", address);
					map_vehicle.put("region", region);
					map_vehicle.put("country_code", country_code);
					map_vehicle.put("reg_no", registration_no);

					map_vehicle.put("insurance_class",
							insurance_class_1.get().isEmpty() ? list : insurance_class_1.get());
					map_vehicle.put("body_type_policy",
							body_type_policy_1.get().isEmpty() ? list : body_type_policy_1.get());
					map_vehicle.put("vehicle_usage_policy",
							vehicle_usage_policy_1.get().isEmpty() ? list : vehicle_usage_policy_1.get());
					//map_vehicle.put("insurance_type", insurance_type_1.get().isEmpty() ? list : insurance_type_1.get());
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

					Map<String, String> errorMap = new HashMap<>();
					map_vehicle.put("error_messages", errorMap);

					return_res.put("data", map_vehicle);
					return_res.put("screen", "POLICY_DETAILS");
					response = printReq.toJson(return_res);

					log.info("response" + response);

					return response;

				}

			} else if ("POLICY_PAGE".equalsIgnoreCase(component_action)) {

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_no = data.get("mobile_number") == null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();
				String registration_no = data.get("registration_no") == null ? ""
						: data.get("registration_no").toString().trim();
				String insurance_type = data.get("insurance_type") == null ? ""
						: data.get("insurance_type").toString().trim();
				String insurance_class = data.get("insurance_class") == null ? ""
						: data.get("insurance_class").toString().trim();
				String gps = data.get("gps") == null ? "" : data.get("gps").toString().trim();
				String car_alarm = data.get("car_alarm") == null ? "" : data.get("car_alarm").toString().trim();
				String insurance_claim = data.get("insurance_claim") == null ? ""
						: data.get("insurance_claim").toString().trim();
				String body_type_policy = data.get("body_type_policy") == null ? ""
						: data.get("body_type_policy").toString().trim();
				String vehicle_usage_policy = data.get("vehicle_usage_policy") == null ? ""
						: data.get("vehicle_usage_policy").toString().trim();
				String quotation_creator = data.get("quotation_creator") == null ? ""
						: data.get("quotation_creator").toString().trim();
				String vehicle_si = data.get("vehicle_si") == null ? "" : data.get("vehicle_si").toString().trim();
				String accessories_si = data.get("accessories_si") == null ? ""
						: data.get("accessories_si").toString().trim();
				String windshield_si = data.get("windshield_si") == null ? ""
						: data.get("windshield_si").toString().trim();
				String extended_TPDD_si = data.get("extended_TPDD_si") == null ? ""
						: data.get("extended_TPDD_si").toString().trim();
				String broker_loginid = data.get("broker_loginid") == null ? ""
						: data.get("broker_loginid").toString().trim();
				String policy_start_date = data.get("policy_start_date") == null ? ""
						: data.get("policy_start_date").toString().trim();

				// String date = new Date().();
				// validation need to do here
				/*
				 * if(!policy_start_date.equals(date)) {
				 * input_validation.put("policy_start_date", "Please provide valid date.");
				 * 
				 * }
				 */

				if (!vehicle_si.matches("[0-9]+")) {
					input_validation.put("vehicle_si", "Please enter valid mobile");
				}
				if (!accessories_si.matches("[0-9]+")) {
					input_validation.put("windshield_si", "Please enter valid mobile");
				}
				if (!windshield_si.matches("[0-9]+")) {
					input_validation.put("windshield_si", "Please enter valid mobile");
				}
				if (!extended_TPDD_si.matches("[0-9]+")) {
					input_validation.put("extended_TPDD_si", "Please enter valid mobile");
				}
				//Boolean validation_status = true;

				if (input_validation.size() > 0) {

					
					CompletableFuture<List<Map<String, String>>> insurance_type_1 = thread
							.getInsuranceType(token);
					CompletableFuture<List<Map<String, String>>> insurance_class_1 = thread.getInsuranceClass(token);
					
					CompletableFuture.allOf(insurance_type_1, insurance_class_1).join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("insurance_type",
							insurance_type_1.get().isEmpty() ? SAMPLE_DATA : insurance_type_1.get());
					error_messages.put("insurance_class",
							insurance_class_1.get().isEmpty() ? SAMPLE_DATA : insurance_class_1.get());
					error_messages.put("gps", gps);
					error_messages.put("car_alarm", car_alarm);
					error_messages.put("insurance_claim", insurance_claim);
					error_messages.put("vehicle_si", vehicle_si);
					error_messages.put("accessories_si", accessories_si);
					error_messages.put("windshield_si", windshield_si);
					error_messages.put("extended_TPDD_si", extended_TPDD_si);

					return_res.put("action", "data_exchange");
					return_res.put("data", error_messages);

					response = printReq.toJson(return_res);

				} else {

					Map<String, Object> extension_message_response = new HashMap<String, Object>();
					Map<String, Object> params = new HashMap<String, Object>();
					Map<String, Object> param_map = new HashMap<String, Object>();

					params.put("title", title);
					params.put("customer_name", customer_name);
					params.put("country_code", country_code);
					params.put("mobile_no", mobile_no);
					params.put("email_id", email_id);
					params.put("address", address);
					params.put("region", region);
					params.put("registration_no", registration_no);
					params.put("insurance_type", insurance_type);
					params.put("insurance_class", insurance_class);
					params.put("gps", gps);
					params.put("car_alarm", car_alarm);
					params.put("insurance_claim", insurance_claim);
					params.put("body_type_policy", body_type_policy);
					params.put("vehicle_usage_policy", vehicle_usage_policy);
					params.put("quotation_creator", quotation_creator);
					params.put("vehicle_si", vehicle_si);
					params.put("accessories_si", accessories_si);
					params.put("windshield_si", windshield_si);
					params.put("extended_TPDD_si", extended_TPDD_si);
					params.put("broker_loginid", broker_loginid);

					param_map.put("params", params);
					extension_message_response.put("extension_message_response", param_map);

					return_res.put("screen", "SUCCESS");
					return_res.put("data", extension_message_response);

					response = printReq.toJson(return_res);

					return response;

				}

			} else if ("CREATE_VEHICLE".equalsIgnoreCase(component_action)) {

				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_no = data.get("mobile_number") == null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				Map<String, String> request_map = new HashMap<String, String>();
				request_map.put("BranchCode", "55");
				request_map.put("InsuranceId", "100019");

				String request_1 = printReq.toJson(request_map);

				CompletableFuture<List<Map<String, String>>> fuel_type = thread.getFuelType(request_1, token);
				CompletableFuture<List<Map<String, String>>> color = thread.getColor(request_1, token);
				CompletableFuture<List<Map<String, String>>> manufacture_year = thread.getManuFactureYear();
				CompletableFuture<List<Map<String, String>>> body_type_e = thread.getSTPBodyType(request_1, token);
				CompletableFuture<List<Map<String, String>>> vehicle_usage = thread.getSTPVehicleUsage(request_1,
						token);
				CompletableFuture<List<Map<String, String>>> motor_category = thread.getMotorCategory(request_1, token);

				CompletableFuture.allOf(fuel_type, color, manufacture_year, body_type_e, vehicle_usage).join();

				Map<String, String> error_messages = new HashMap<>();

				Map<String, Object> return_map = new HashMap<>();
				return_map.put("title", title);
				return_map.put("customer_name", customer_name);
				return_map.put("country_code", country_code);
				return_map.put("mobile_no", mobile_no);
				return_map.put("email_id", email_id);
				return_map.put("address", address);
				return_map.put("region", region);
				return_map.put("body_type", body_type_e.get().isEmpty() ? list : body_type_e.get());
				return_map.put("vehicle_make", list);
				return_map.put("vehicle_model", list);
				return_map.put("manufacture_year", manufacture_year.get().isEmpty() ? list : manufacture_year.get());
				return_map.put("fuel_used", fuel_type.get().isEmpty() ? list : fuel_type.get());
				return_map.put("vehicle_color", color.get().isEmpty() ? list : color.get());
				return_map.put("vehicle_usage", vehicle_usage.get().isEmpty() ? list : vehicle_usage.get());
				return_map.put("motor_category", motor_category.get().isEmpty() ? list : motor_category.get());
				return_map.put("error_messages", error_messages);

				return_res.put("data", return_map);
				return_res.put("screen", "VEHICLE_INFORMATION");

				response = printReq.toJson(return_res);
				return response;

			} else if ("SEARCH_BACK".equalsIgnoreCase(component_action)) {
				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_no = data.get("mobile_number") == null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				Map<String, Object> return_map = new HashMap<>();
				Map<String, Object> error_messages = new HashMap<>();

				return_map.put("title", title);
				return_map.put("customer_name", customer_name);
				return_map.put("country_code", country_code);
				return_map.put("mobile_no", mobile_no);
				return_map.put("email_id", email_id);
				return_map.put("address", address);
				return_map.put("region", region);
				return_map.put("embedded_link", "Add New Vehicle");
				return_map.put("search_heading", "Search for your vehicle by Registration Number here.");
				return_map.put("error_messages", error_messages);

				return_res.put("data", return_map);
				return_res.put("screen", "VEHICLE_DETAILS");

				response = printReq.toJson(return_res);
				return response;

			} else if ("MAKE".equalsIgnoreCase(component_action)) {
				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();

				List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();

				if (StringUtils.isNotBlank(body_type)) {
					String api = this.stpMake;

					Map<String, Object> region_req = new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100019");
					region_req.put("BranchCode", "55");

					api_request = printReq.toJson(region_req);

					api_response = thread.callEwayApi(api, api_request, token);

					Map<String, Object> region_obj = mapper.readValue(api_response, Map.class);
					List<Map<String, Object>> result = (List<Map<String, Object>>) region_obj.get("Result");

					data_list = result.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p.get("Code") == null ? "" : p.get("Code").toString());
						map.put("title", p.get("CodeDesc") == null ? "" : p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				} else {
					data_list = SAMPLE_DATA;
				}
				Map<String, Object> make_list = new HashMap<String, Object>();
				make_list.put("vehicle_make", data_list);
				return_res.put("data", make_list);
				response = printReq.toJson(return_res);
				return response;

				/*
				 * List<String> arraylist = new ArrayList<String>(); if (!arraylist.isEmpty()) {
				 * // api call } else { Map<String, String> make = new HashMap<>();
				 * make.put("id", "1"); make.put("title", "Honda");
				 * 
				 * List<Map<String, String>> makeList = new ArrayList<>(); makeList.add(make);
				 * 
				 * return_res.put("data", makeList); response = printReq.toJson(return_res);
				 * return response;
				 * 
				 * }
				 */
			}else if ("MODEL".equalsIgnoreCase(component_action)) {
				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String make = data.get("vehicle_make") == null ? "" : data.get("vehicle_make").toString().trim();
				List<Map<String, String>> data_list = new ArrayList<Map<String, String>>();

				if (!"00000".equals(make) && StringUtils.isNotBlank(make)) {
					String api = this.stpMakeModel;

					Map<String, Object> region_req = new HashMap<String, Object>();
					region_req.put("BodyId", body_type);
					region_req.put("InsuranceId", "100019");
					region_req.put("BranchCode", "55");
					region_req.put("MakeId", make);

					api_request = printReq.toJson(region_req);

					api_response = thread.callEwayApi(api, api_request, token);

					Map<String, Object> region_obj = mapper.readValue(api_response, Map.class);
					List<Map<String, Object>> result = (List<Map<String, Object>>) region_obj.get("Result");

					data_list = result.stream().map(p -> {
						Map<String, String> map = new HashMap<>();
						map.put("id", p.get("Code") == null ? "" : p.get("Code").toString());
						map.put("title", p.get("CodeDesc") == null ? "" : p.get("CodeDesc").toString());
						return map;
					}).collect(Collectors.toList());
				} else {
					data_list = SAMPLE_DATA;
				}
				Map<String, Object> make_list = new HashMap<String, Object>();
				make_list.put("vehicle_make", data_list);
				return_res.put("data", make_list);
				response = printReq.toJson(return_res);
				return response;
			}
			else if ("SAVE_VEHICLE".equalsIgnoreCase(component_action)) {
				String title = data.get("title") == null ? "" : data.get("title").toString().trim();
				String customer_name = data.get("customer_name") == null ? ""
						: data.get("customer_name").toString().trim();
				String country_code = data.get("country_code") == null ? ""
						: data.get("country_code").toString().trim();
				String mobile_no = data.get("mobile_number") == null ? "" : data.get("mobile_number").toString().trim();
				String email_id = data.get("email_id") == null ? "" : data.get("email_id").toString().trim();
				String address = data.get("address") == null ? "" : data.get("address").toString().trim();
				String region = data.get("region") == null ? "" : data.get("region").toString().trim();

				String body_type = data.get("body_type") == null ? "" : data.get("body_type").toString().trim();
				String vehicle_make = data.get("vehicle_make") == null ? ""
						: data.get("vehicle_make").toString().trim();
				String vehicle_model = data.get("vehicle_model") == null ? ""
						: data.get("vehicle_model").toString().trim();
				String manufacture_year = data.get("manufacture_year") == null ? ""
						: data.get("manufacture_year").toString().trim();
				String fuel_used = data.get("fuel_used") == null ? "" : data.get("fuel_used").toString().trim();
				String vehicle_color = data.get("vehicle_color") == null ? ""
						: data.get("vehicle_color").toString().trim();
				String vehicle_usage = data.get("vehicle_usage") == null ? ""
						: data.get("vehicle_usage").toString().trim();
				String registration_no = data.get("registration_no") == null ? ""
						: data.get("registration_no").toString().trim();
				String engine_number = data.get("engine_number") == null ? ""
						: data.get("engine_number").toString().trim();
				String tare_weight = data.get("tare_weight") == null ? "" : data.get("tare_weight").toString().trim();
				String gross_weight = data.get("gross_weight") == null ? ""
						: data.get("gross_weight").toString().trim();

				String no_of_axle = data.get("no_of_axle") == null ? "" : data.get("no_of_axle").toString().trim();
				String axle_distance = data.get("axle_distance") == null ? ""
						: data.get("axle_distance").toString().trim();
				String motor_category = data.get("motor_category") == null ? ""
						: data.get("motor_category").toString().trim();
				String chassis_number = data.get("chassis_number") == null ? ""
						: data.get("chassis_number").toString().trim();
				String engine_capacity = data.get("engine_capacity") == null ? ""
						: data.get("engine_capacity").toString().trim();
				String seating_capacity = data.get("seating_capacity") == null ? ""
						: data.get("seating_capacity").toString().trim();

				// validation need
				if (chassis_number.length() < 5) {
					input_validation.put("chassis_number", "Minimum characters required.");
				} else if (!chassis_number.matches("[a-zA-Z0-9]+")) {
					input_validation.put("chassis_number", "Special characters not allowed.");
				}
				if (!registration_no.matches("[a-zA-Z0-9]+")) {
					input_validation.put("registration_no", "Special characters not allowed.");
				}
				if (!engine_capacity.matches("[0-9]+")) {
					input_validation.put("engine_capacity", "digits only allowed");
				}
				if (!engine_number.matches("[0-9]+")) {
					input_validation.put("engine_number", "digits only allowed");
				}
				if (!tare_weight.matches("[0-9]+")) {
					input_validation.put("tare_weight", "digits only allowed");
				} else if (!tare_weight.matches("[0-9]+")) {
					input_validation.put("tare_weight", "digits only allowed");
				}
				if (!gross_weight.matches("[0-9]+")) {
					input_validation.put("gross_weight", "digits only allowed");
				}
				if (!no_of_axle.matches("[0-9]+")) {
					input_validation.put("no_of_axle", "digits only allowed");
				}
				if (!axle_distance.matches("[0-9]+")) {
					input_validation.put("axle_distance", "digits only allowed");
				}
				if (!seating_capacity.matches("[0-9]+")) {
					input_validation.put("seating_capacity", "digits only allowed");
				}

				if (!input_validation.isEmpty() && input_validation.size() > 0) {
					Map<String, String> request_map = new HashMap<String, String>();
					request_map.put("BranchCode", "55");
					request_map.put("InsuranceId", "100019");
					request_map.put("BodyId", body_type);
					request_map.put("MakeId", vehicle_make);

					String request_1 = printReq.toJson(request_map);

					CompletableFuture<List<Map<String, String>>> fuel_type_e = thread.getFuelType(request_1, token);
					CompletableFuture<List<Map<String, String>>> color_e = thread.getColor(request_1, token);
					CompletableFuture<List<Map<String, String>>> manufacture_year_e = thread.getManuFactureYear();
					CompletableFuture<List<Map<String, String>>> body_type_e = thread.getSTPBodyType(request_1, token);
					CompletableFuture<List<Map<String, String>>> vehicle_usage_e = thread.getSTPVehicleUsage(request_1,
							token);
					CompletableFuture<List<Map<String, String>>> make_e = thread.getStpMake(token, body_type);
					CompletableFuture<List<Map<String,String>>> model_e =thread.getSTPModel(body_type,vehicle_make,token);

					CompletableFuture
							.allOf(fuel_type_e, color_e, manufacture_year_e, body_type_e, vehicle_usage_e, make_e,model_e)
							.join();

					Map<String, Object> error_messages = new HashMap<String, Object>();
					error_messages.put("error_messages", input_validation);
					error_messages.put("body_type", body_type_e.get().isEmpty() ? SAMPLE_DATA : body_type_e.get());
					error_messages.put("vehicle_make", make_e.get().isEmpty() ? SAMPLE_DATA : make_e.get());
					error_messages.put("vehicle_model", model_e.get().isEmpty()?SAMPLE_DATA : model_e.get());
					error_messages.put("manufacture_year",
							manufacture_year_e.get().isEmpty() ? SAMPLE_DATA : manufacture_year_e.get());
					error_messages.put("fuel_used", fuel_type_e.get().isEmpty() ? SAMPLE_DATA : fuel_type_e.get());
					error_messages.put("vehicle_usage",
							vehicle_usage_e.get().isEmpty() ? SAMPLE_DATA : vehicle_usage_e.get());
					error_messages.put("vehicle_color", color_e.get().isEmpty() ? SAMPLE_DATA : color_e.get());

					error_messages.put("title", title);
					error_messages.put("customer_name", customer_name);
					error_messages.put("country_code", country_code);
					error_messages.put("mobile_number", mobile_no);
					error_messages.put("email_id", email_id);
					error_messages.put("address", address);
					error_messages.put("region", region);
					error_messages.put("axle_distance", axle_distance);
					error_messages.put("no_of_axle", no_of_axle);
					error_messages.put("gross_weight", gross_weight);
					error_messages.put("tare_weight", tare_weight);
					error_messages.put("registration_no", registration_no);
					error_messages.put("engine_number", engine_number);
					error_messages.put("chassis_number", chassis_number);
					error_messages.put("engine_capacity", engine_capacity);
					error_messages.put("seating_capacity", seating_capacity);

					return_res.put("data", error_messages);
					response = printReq.toJson(return_res);
					return response;
				} else {
					Map<String, Object> map_policy = new HashMap<String, Object>();
					Map<String, String> save_details = new HashMap<String, String>();

					save_details.put("Insuranceid", "100019");
					save_details.put("BranchCode", "55");
					save_details.put("AxelDistance", axle_distance);
					save_details.put("Chassisnumber", chassis_number);
					save_details.put("Color", vehicle_color);
					save_details.put("CreatedBy", "ugandabroker3");
					save_details.put("EngineNumber", engine_number);
					save_details.put("FuelType", fuel_used);
					save_details.put("Grossweight", gross_weight);
					save_details.put("ManufactureYear", manufacture_year);
					save_details.put("MotorCategory", motor_category);
					save_details.put("Motorusage", vehicle_usage);
					save_details.put("NumberOfAxels", no_of_axle);
					save_details.put("OwnerCategory", "1");
					save_details.put("Registrationnumber", registration_no);
					save_details.put("ResEngineCapacity", engine_capacity);
					save_details.put("ResOwnerName", "Testing");
					save_details.put("ResStatusCode", "Y");
					save_details.put("ResStatusDesc", "None");
					save_details.put("SeatingCapacity", seating_capacity);
					save_details.put("Tareweight", tare_weight);
					save_details.put("Vehcilemodel", vehicle_model);
					save_details.put("VehicleType", body_type);
					save_details.put("Vehiclemake", vehicle_make);
					save_details.put("RegistrationDate", null);

					String saveVehicle = wh_save_vehicle_info_api;
					api_response = thread.callEwayApi(saveVehicle, mapper.writeValueAsString(save_details), token);
					Map<String, Object> map = mapper.readValue(api_response, Map.class);
					String status = map.get("Message").toString();
					if ("Success".equalsIgnoreCase(status)) {
						
						//Map<String,Object> insuranceType = new HashMap<>();
						//insuranceType.put("Insurancetype", input_validation);
						
						/*
						 * Map<String,String> oldRate= new HashMap<>();
						 * oldRate.put("OldAcccessoriesSumInsured", null); oldRate.put("OldSumInsured",
						 * null); oldRate.put("OldTppdIncreaeLimit", null);
						 * oldRate.put("OldWindScreenSumInsured", null);
						 * 
						 * Map<String,Object> exchangeRateSync =new HashMap<>();
						 * exchangeRateSync.put("ExchangeRateScenario", oldRate);
						 */
						
						/*
						 * Map<String,Object> saveMotorDetails= new HashMap<>();
						 * saveMotorDetails.put("AcExecutiveId", null);
						 * saveMotorDetails.put("AcccessoriesSumInsured", null);
						 * saveMotorDetails.put("AccessoriesInformation", null);
						 * saveMotorDetails.put("AdditionalCircumstances", null);
						 * saveMotorDetails.put("AgencyCode", "12573");
						 * saveMotorDetails.put("ApplicationId", "1");
						 * saveMotorDetails.put("AxelDistance", axle_distance);
						 * saveMotorDetails.put("BdmCode", "70100010");
						 * saveMotorDetails.put("BorrowerType", null);
						 * saveMotorDetails.put("BranchCode", "55");
						 * saveMotorDetails.put("BrokerBranchCode", "1");
						 * saveMotorDetails.put("BrokerCode", "12573");
						 * saveMotorDetails.put("CarAlarmYn", "N");
						 * saveMotorDetails.put("Chassisnumber", chassis_number);
						 * saveMotorDetails.put("CityLimit", null); saveMotorDetails.put("ClaimRatio",
						 * null); saveMotorDetails.put("ClaimType", "0");
						 * saveMotorDetails.put("CollateralName", null);
						 * saveMotorDetails.put("CollateralYn", null); saveMotorDetails.put("Color",
						 * vehicle_color); saveMotorDetails.put("CommissionType", null);
						 * saveMotorDetails.put("CoverNoteNo", null);
						 * saveMotorDetails.put("CubicCapacity", "2000");
						 * saveMotorDetails.put("Currency", "UGX"); saveMotorDetails.put("CustomerCode",
						 * "70100010"); saveMotorDetails.put("CustomerReferenceNo", "AGI-CUST-16956");
						 * saveMotorDetails.put("DefenceValue", null);
						 * saveMotorDetails.put("DrivenByDesc", "D");
						 * saveMotorDetails.put("EndorsementDate", null);
						 * saveMotorDetails.put("EndorsementEffectiveDate", null);
						 * saveMotorDetails.put("EndorsementRemarks", null);
						 * saveMotorDetails.put("EndorsementType", null);
						 * saveMotorDetails.put("EndorsementTypeDesc", null);
						 * saveMotorDetails.put("EndorsementYn", "N");
						 * saveMotorDetails.put("EndtCategoryDesc", null);
						 * saveMotorDetails.put("EndtCount", null);
						 * saveMotorDetails.put("EndtPrevPolicyNo", null);
						 * saveMotorDetails.put("EndtPrevQuoteNo", null);
						 * saveMotorDetails.put("EndtStatus", null);
						 * saveMotorDetails.put("EngineNumber", engine_number);
						 * saveMotorDetails.put("ExcessLimit", "0");
						 * saveMotorDetails.put("ExchangeRate", "1.0");
						 * saveMotorDetails.put("FirstLossPayee", null);
						 * saveMotorDetails.put("FleetOwnerYn", "N"); saveMotorDetails.put("FuelType",
						 * fuel_used); saveMotorDetails.put("Gpstrackinginstalled", null);
						 * saveMotorDetails.put("Grossweight", gross_weight);
						 * saveMotorDetails.put("HavePromoCode", "N");
						 * saveMotorDetails.put("HoldInsurancePolicy", "N");
						 * saveMotorDetails.put("Idnumber", "64564654654");
						 * saveMotorDetails.put("Inflation", ""); saveMotorDetails.put("InsuranceClass",
						 * null); saveMotorDetails.put("InsuranceId", "100019");
						 * 
						 * saveMotorDetails.put("InsurerSettlement", "");
						 * saveMotorDetails.put("InterestedCompanyDetails", "");
						 * saveMotorDetails.put("IsFinanceEndt", null); saveMotorDetails.put("LoginId",
						 * "ugandabroker3"); saveMotorDetails.put("ManufactureYear",manufacture_year);
						 * saveMotorDetails.put("ModelNumber", null);
						 * saveMotorDetails.put("MotorCategory", motor_category);
						 * saveMotorDetails.put("Motorusage", vehicle_usage);
						 * saveMotorDetails.put("MotorusageId", ""); saveMotorDetails.put("Ncb", "0");
						 * saveMotorDetails.put("NcdYn", null); saveMotorDetails.put("NoOfClaims",
						 * null); saveMotorDetails.put("NoOfComprehensives", "0");
						 * saveMotorDetails.put("NoOfVehicles", "");
						 * saveMotorDetails.put("OrginalPolicyNo", null);
						 * saveMotorDetails.put("OwnerCategory", "" );
						 * saveMotorDetails.put("PolicyEndDate","" );
						 * saveMotorDetails.put("PolicyRenewalYn", "N");
						 * saveMotorDetails.put("PolicyStartDate","" );
						 * saveMotorDetails.put("PolicyType", null); saveMotorDetails.put("ProductId",
						 * "5"); saveMotorDetails.put("PromoCode", null);
						 * saveMotorDetails.put("PurchaseDate", null);
						 * saveMotorDetails.put("RadioOrCasseteplayer", null);
						 * saveMotorDetails.put("RegistrationDate", null);
						 * saveMotorDetails.put("RegistrationYear", "");
						 * saveMotorDetails.put("Registrationnumber",registration_no);
						 * saveMotorDetails.put("RequestReferenceNo","" );
						 * saveMotorDetails.put("RoofRack", null); saveMotorDetails.put("SaveOrSubmit",
						 * "Save"); saveMotorDetails.put("SavedFrom", "WEB");
						 * saveMotorDetails.put("Scenarios", exchangeRateSync);
						 * saveMotorDetails.put("SeatingCapacity", seating_capacity);
						 * saveMotorDetails.put("SectionId","" ); saveMotorDetails.put("SourceTypeId",
						 * ""); saveMotorDetails.put("SpotFogLamp", null);
						 * saveMotorDetails.put("Status", "Y"); saveMotorDetails.put("Stickerno", null);
						 * saveMotorDetails.put("SubUserType", "Broker");
						 * saveMotorDetails.put("SumInsured", null); saveMotorDetails.put("Tareweight",
						 * tare_weight); saveMotorDetails.put("TiraCoverNoteNo", null);
						 * saveMotorDetails.put("TppdFreeLimit", null);
						 * saveMotorDetails.put("TppdIncreaeLimit", null);
						 * saveMotorDetails.put("TrailerDetails", null);
						 * saveMotorDetails.put("UserType", "Broker");
						 * saveMotorDetails.put("Vehcilemodel", "vehicle_model");
						 * saveMotorDetails.put("VehcilemodelId", ""); saveMotorDetails.put("VehicleId",
						 * ""); saveMotorDetails.put("VehicleType","" );
						 * saveMotorDetails.put("VehicleTypeId","" );
						 * saveMotorDetails.put("VehicleValueType", "");
						 * saveMotorDetails.put("Vehiclemake",vehicle_make );
						 * saveMotorDetails.put("VehiclemakeId","" );
						 * saveMotorDetails.put("WindScreenSumInsured", null);
						 * saveMotorDetails.put("Windscreencoverrequired", "N");
						 * saveMotorDetails.put("accident", null);
						 * saveMotorDetails.put("periodOfInsurance", "365");
						 * 
						 * String saveVehicleDetails = wh_save_motor_details_api; api_response =
						 * thread.callEwayApi(saveVehicleDetails,
						 * mapper.writeValueAsString(saveMotorDetails), token); Map<String, Object>
						 * mapping = mapper.readValue(api_response, Map.class); String saveStatus =
						 * map.get("Message").toString();
						 if ("Success".equalsIgnoreCase(saveStatus)) {
						
						//Map<String,String> getMotorDetails = new HashMap<>();
						//getMotorDetails.put("Idnumber",saveMotorDetails.get("Idnumber"));
						//getMotorDetails.put("RequestReferenceNo",saveMotorDetails.get("RequestReferenceNo"));
						//getMotorDetails.put("Vehicleid",saveMotorDetails.get("Vehicleid"));*/
						
						/*String getMotorDetailsAPI=wh_get_motor_details;
						
						api_response = thread.callEwayApi(saveVehicleDetails, mapper.writeValueAsString(saveMotorDetails), token);
						Map<String, Object> mappingMotorDetails = mapper.readValue(api_response, Map.class);
						
						String bodyType=mappingMotorDetails.get("VehicleType").toString();
						String vehicleUsage=mappingMotorDetails.get("Motorusage").toString();
						*/

						CompletableFuture<List<Map<String, String>>> insurance_type_1 = thread
								.getInsuranceType(token);
						CompletableFuture<List<Map<String, String>>> insurance_class_1 = thread
								.getInsuranceClass(token);
						

						CompletableFuture.allOf(insurance_type_1, insurance_class_1).join();

						map_policy.put("title", title);
						map_policy.put("customer_name", customer_name);
						map_policy.put("country_code", country_code);
						map_policy.put("mobile_number", mobile_no);
						map_policy.put("email_id", email_id);
						map_policy.put("address", address);
						map_policy.put("region", region);

						map_policy.put("insurance_class",
								insurance_class_1.get().isEmpty() ? list : insurance_class_1.get());
						map_policy.put("insurance_type",
								insurance_type_1.get().isEmpty() ? list : insurance_type_1.get());
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

						Map<String, String> mapPolicy = new HashMap<>();
						map_policy.put("error_messages", mapPolicy);

						return_res.put("data", map_policy);
						return_res.put("screen", "POLICY_DETAILS");
						response = printReq.toJson(return_res);

						log.info("response" + response);

						return response;

					}
											}
				}
			
			//return response;
		} catch (Exception ex) {
			log.error(ex);
			ex.printStackTrace();

		}
		return response;

	}

}