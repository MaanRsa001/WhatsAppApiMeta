package com.maan.whatsapp.claimintimation;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;
import com.maan.whatsapp.entity.master.PreinspectionDataDetail;
import com.maan.whatsapp.insurance.InsuranceServiceImpl;
import com.maan.whatsapp.repository.whatsapp.PreInspectionDataDetailRepo;
import com.maan.whatsapp.response.error.Error;
import com.maan.whatsapp.service.common.CommonService;
import com.maan.whatsapp.service.whatsapptemplate.WhatsapptemplateServiceImpl;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class ClaimIntimationServiceImpl {

	Logger log = LogManager.getLogger(ClaimIntimationServiceImpl.class);

	ObjectMapper mapper = new ObjectMapper();

	private Gson printReq = new Gson();

	@Autowired
	private CommonService cs;

	@Autowired
	private ClaimIntimationRepository repository;

	@Autowired
	private PreInspectionDataDetailRepo preInsDataRepo;

	@Autowired
	private WhatsapptemplateServiceImpl tempServiceImpl;
	
	@Autowired
	private InalipaIntimatedTableRepository inalipaIntiRepo;
	
	@Autowired
	private InsuranceServiceImpl insuranceServiceImpl;

	private OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS).build();

	private MediaType mediaType = MediaType.parse("application/json");

	public Object claimintimation(ClaimIntimationReq req) {
		Map<String, Object> map = new HashMap<String, Object>();
		ClaimIntimationRes intimationRes = new ClaimIntimationRes();
		String message = "";
		String errorDesc = "";
		String firstName = "";
		String getPolicyApi = "";
		try {

			if ("Policy".equalsIgnoreCase(req.getApiType())) {
				if (StringUtils.isNotBlank(req.getPolicyNo())) {
					map.put("QuotationPolicyNo", req.getPolicyNo());
					map.put("InsuranceId", req.getCompanyId());
					getPolicyApi = cs.getwebserviceurlProperty().getProperty("get.policy.details.bypolicyno");
				} else {
					map.put("ChassisNo", req.getChassisNo());
					map.put("InsuranceId", req.getCompanyId());
					getPolicyApi = cs.getwebserviceurlProperty().getProperty("get.policy.details.bychassisNo");
				}
				String request = printReq.toJson(map);
				log.info("Claim Intimation API " + getPolicyApi);
				log.info("Claim Intimation Policy Request " + request);
				String response = callApi(getPolicyApi, request);
				log.info("Claim Intimation Policy response " + response);

				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				List<Map<String, Object>> policyResponse = mapper.readValue(response, List.class);

				// List<ClaimErrors> error
				// =policyResponse.getFaileResponse().getErrors().isEmpty()?null:policyResponse.getFaileResponse().getErrors();
				if (!CollectionUtils.isEmpty(policyResponse)) {
					int count = 1;
					List<ClaimIntimationEntity> saveList = new ArrayList<ClaimIntimationEntity>(5);
					List<Map<String, Object>> errorList = (List<Map<String, Object>>) policyResponse.get(0)
							.get("Errors");
					Boolean errorStatus = CollectionUtils.isEmpty(errorList) ? true : false;
					if (errorStatus) {
						Long serialNo = repository.getSerialNo();
						for (Map<String, Object> data : policyResponse) {
							Map<String, Object> vd = (Map<String, Object>) data.get("VehicleInfo");
							Map<String, Object> pd = (Map<String, Object>) data.get("PolicyInfo");
							ClaimIntimationEntity ci = ClaimIntimationEntity.builder().apiType(req.getApiType())
									.chassisNo(vd.get("ChassisNo") == null ? "" : vd.get("ChassisNo").toString())
									.civilId(pd.get("Civilid") == null ? "" : pd.get("Civilid").toString())
									.botOptionNo(String.valueOf(count))
									.contactPersonName(
											pd.get("Contactpername") == null ? "" : pd.get("Contactpername").toString())
									.manufactureYear(vd.get("Manufactureyear") == null ? ""
											: vd.get("Manufactureyear").toString())
									.mobileNo(req.getMobileNo())
									.plateNo(vd.get("Platenocharacter") == null ? ""
											: vd.get("Platenocharacter").toString())
									.policyFrom(pd.get("PolicyFrom") == null ? "" : pd.get("PolicyFrom").toString())
									.policyNo(pd.get("PolicyNo") == null ? "" : pd.get("PolicyNo").toString())
									.policyTo(pd.get("PolicyTo") == null ? "" : pd.get("PolicyTo").toString())
									.product(pd.get("Product") == null ? "" : pd.get("Product").toString())
									.serialNo(serialNo)
									.sumInsured(vd.get("Suminsured") == null ? "" : vd.get("Suminsured").toString())
									.status("Y").entryDate(new Date())
									.vehiModel(vd.get("Vehiclemodeldesc") == null ? ""
											: vd.get("Vehiclemodeldesc").toString())
									.vehiType(vd.get("Vehicletypedesc") == null ? ""
											: vd.get("Vehicletypedesc").toString())
									.vehRegNo(vd.get("VechRegNo") == null ? "" : vd.get("VechRegNo").toString())
									.divnCode(pd.get("ProductCode") == null ? "" : pd.get("ProductCode").toString())
									.insuranceId(pd.get("InsuranceId") == null ? "" : pd.get("InsuranceId").toString())
									.branchCode(pd.get("BranchCode") == null ? "" : pd.get("BranchCode").toString())
									.regionCode(pd.get("RegionCode") == null ? "" : pd.get("RegionCode").toString())
									.build();

							saveList.add(ci);

							count++;
						}
						List<ClaimIntimationEntity> result = repository.saveAll(saveList);

						message = result.stream().map(p -> {
							String vehMod = p.getVehiModel() + "/" + p.getVehiType() + "/" + p.getVehRegNo();
							String optionNo = p.getBotOptionNo();
							return "*Choose " + optionNo + "* : " + vehMod + "";
						}).collect(Collectors.joining("\n\n"));
						firstName = StringUtils.isBlank(result.get(0).getContactPersonName()) ? ""
								: result.get(0).getContactPersonName();
					} else {
						errorDesc = "No Record Found..Please try again";
					}
				} else {
					errorDesc = "No Record Found..Please try again..";
				}

			} else if ("LossType".equalsIgnoreCase(req.getApiType())) {
				Map<String, Object> da = repository.getProductCode(req.getMobileNo());
				map.put("InsuranceId", da.get("INSURANCE_ID") == null ? "100002" : da.get("INSURANCE_ID").toString());
				map.put("PolicytypeId", da.get("PRODUCT") == null ? "" : da.get("PRODUCT").toString());
				map.put("Status", "Y");
				String lossType = cs.getwebserviceurlProperty().getProperty("get.loss.type");
				String request = printReq.toJson(map);
				log.info("Claim Intimation LossType API " + lossType);
				log.info("Claim Intimation LossType Request " + request);
				String response = callApi(lossType, request);
				log.info("Claim Intimation LossType response " + response);
				LosstypeRes lossRes = mapper.readValue(response, LosstypeRes.class);
				if (!CollectionUtils.isEmpty(lossRes.getPrimary())) {
					int count = 1;
					List<ClaimIntimationEntity> saveList = new ArrayList<ClaimIntimationEntity>();
					Long serialNo = repository.getSerialNo();
					for (int i = 0; i < lossRes.getPrimary().size(); i++) {
						PrimaryLoss p = lossRes.getPrimary().get(i);
						ClaimIntimationEntity ci = ClaimIntimationEntity.builder().apiType(req.getApiType())
								.entryDate(new Date()).status("Y").mobileNo(req.getMobileNo()).serialNo(serialNo)
								.botOptionNo(String.valueOf(count)).code(p.getCode()).codeDesc(p.getCodeDesc()).build();
						saveList.add(ci);

						count++;
					}

					List<ClaimIntimationEntity> result = repository.saveAll(saveList);

					message = result.stream().map(p -> {
						String vehMod = p.getCodeDesc();
						String optionNo = p.getBotOptionNo();
						return "*Choose " + optionNo + "* : " + vehMod + "";
					}).collect(Collectors.joining("\n"));

				} else {
					errorDesc = "No Record Found";
				}

			} else if ("CauseOfLoss".equalsIgnoreCase(req.getApiType())) {
				Map<String, Object> da = repository.getProductCode(req.getMobileNo());
				map.put("InscompanyId", da.get("INSURANCE_ID") == null ? "100002" : da.get("INSURANCE_ID").toString());
				map.put("CclProdCode", da.get("DIVN_CODE") == null ? "" : da.get("DIVN_CODE").toString());
				String request = printReq.toJson(map);
				String causeOfLossType = cs.getwebserviceurlProperty().getProperty("get.cause.of.loss");
				log.info("Claim Intimation Cause Of Loss API " + causeOfLossType);
				log.info("Claim Intimation Cause Of Loss Request " + request);
				String response = callApi(causeOfLossType, request);
				List<Map<String, Object>> causeOfLoss = mapper.readValue(response, List.class);
				log.info("Claim Intimation Cause Of Loss Response " + response);
				if (!CollectionUtils.isEmpty(causeOfLoss)) {
					int count = 1;
					List<ClaimIntimationEntity> saveList = new ArrayList<ClaimIntimationEntity>();
					Long serialNo = repository.getSerialNo();
					for (int i = 0; i < causeOfLoss.size(); i++) {
						Map<String, Object> p = causeOfLoss.get(i);
						ClaimIntimationEntity ci = ClaimIntimationEntity.builder().apiType(req.getApiType())
								.entryDate(new Date()).status("Y").mobileNo(req.getMobileNo()).serialNo(serialNo)
								.botOptionNo(String.valueOf(count))
								.code(p.get("CclCauseLossCode") == null ? "" : p.get("CclCauseLossCode").toString())
								.codeDesc(p.get("CclCauseLossDesc") == null ? "" : p.get("CclCauseLossDesc").toString())
								.build();
						saveList.add(ci);

						count++;

					}

					List<ClaimIntimationEntity> result = repository.saveAll(saveList);

					message = result.stream().map(p -> {
						String vehMod = p.getCodeDesc();
						String optionNo = p.getBotOptionNo();
						return "*Choose " + optionNo + "* : " + vehMod + "";
					}).collect(Collectors.joining("\n"));

				} else {
					errorDesc = "No Record Found";
				}

			} else if ("CLAIM_INTIMATION".equalsIgnoreCase(req.getApiType())) {
				Map<String, Object> policyList = repository.getClaimDeatils(req.getMobileNo(), "Policy".toUpperCase(),
						req.getVehicle());
				Map<String, Object> lossList = repository.getClaimDeatils(req.getMobileNo(), "LossType".toUpperCase(),
						req.getLossType());
				Map<String, Object> causeOfloss = repository.getClaimDeatils(req.getMobileNo(),
						"CauseOfLoss".toUpperCase(), req.getCauseOfLoss());
				String policyNo = policyList.get("POLICY_NO") == null ? "" : policyList.get("POLICY_NO").toString();
				String chassisNo = policyList.get("CHASSIS_NO") == null ? "" : policyList.get("CHASSIS_NO").toString();
				map.put("Chassissno", StringUtils.isBlank(req.getChassisNo()) ? chassisNo : req.getChassisNo());
				map.put("PolicyNo", StringUtils.isBlank(req.getPolicyNo()) ? policyNo : req.getPolicyNo());
				map.put("BranchCode", policyList.get("BRANCH_CODE") == null ? "" : policyList.get("BRANCH_CODE"));
				map.put("InsuranceId", policyList.get("INSURANCE_ID") == null ? "" : policyList.get("INSURANCE_ID"));
				map.put("RegionCode", policyList.get("REGION_CODE") == null ? "" : policyList.get("REGION_CODE"));
				map.put("CustMobCode", "255");
				map.put("Assuredname",
						policyList.get("CONTACT_PERSON_NAME") == null ? "" : policyList.get("CONTACT_PERSON_NAME"));
				map.put("Contactno", req.getMobileNo());
				map.put("CreatedBy", "");
				map.put("Status", "Y");
				map.put("Accidentdate", req.getAccidentDate());
				map.put("Accidenttime", req.getAccidentTime());
				map.put("AccidentPlace", req.getLocation());
				map.put("Losstypeid", lossList.get("CODE") == null ? "" : lossList.get("CODE"));
				map.put("CauseOfLossCode", causeOfloss.get("CODE") == null ? "" : causeOfloss.get("CODE"));
				map.put("Usesofvehicle", "None");
				map.put("AccidentDesc", "99".equals(req.getAccidentDesc()) ? "" : req.getAccidentDesc());
				map.put("DrivenBy", "1".equals(req.getDrivenBy()) ? "Owner"
						: "2".equals(req.getDrivenBy()) ? "Driver" : req.getDrivenBy());

				String claimSubmitApi = cs.getwebserviceurlProperty().getProperty("claim.intimation");
				log.info("Claim Intimation Submit API " + claimSubmitApi);
				log.info("Claim Intimation Request" + printReq.toJson(map));
				String request = printReq.toJson(map);
				message = callApi(claimSubmitApi, request);
				log.info("Claim Intimation Request" + message);

				return message;
			}
			intimationRes.setErrorDesc(errorDesc);
			intimationRes.setResponse(message);
			intimationRes.setFirstName(firstName);
		} catch (Exception e) {
			errorDesc = e.getMessage();
			intimationRes.setErrorDesc(errorDesc);
			e.printStackTrace();
		}
		return intimationRes;
	}

	public  String callApi(String url, String request) {
		String apiReponse = "";
		try {
			Response response = null;
			Map<String, Object> tokReq = new HashMap<String, Object>();
			tokReq.put("InsuranceId", cs.getwebserviceurlProperty().getProperty("InsuranceId"));
			tokReq.put("LoginType", cs.getwebserviceurlProperty().getProperty("LoginType"));
			tokReq.put("Password", cs.getwebserviceurlProperty().getProperty("Password"));
			tokReq.put("UserId", cs.getwebserviceurlProperty().getProperty("UserId"));

			String tokenJsonReq = new Gson().toJson(tokReq);
			String tokenApi = cs.getwebserviceurlProperty().getProperty("token.api");
			RequestBody tokenReqBody = RequestBody.create(tokenJsonReq, mediaType);
			Request tokenReq = new Request.Builder().url(tokenApi).post(tokenReqBody).build();
			response = httpClient.newCall(tokenReq).execute();
			String obj = response.body().string();
			TokenResponse tokenRes = mapper.readValue(obj, TokenResponse.class);

			String token = tokenRes.getTokenResponse().getToken();

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

	@SuppressWarnings("unchecked")
	public String callEwayApi(String url, String request) {
		String apiReponse = "";
		try {
			Response response = null;
			Map<String, Object> tokReq = new HashMap<String, Object>();
			tokReq.put("LoginId", "guest");
			tokReq.put("Password", "Admin@01");
			tokReq.put("ReLoginKey", "Y");
			
		//	log.info("Token Request ==> "+tokReq.toString());
			String tokenJsonReq = new Gson().toJson(tokReq);
			String tokenApi = cs.getwebserviceurlProperty().getProperty("eway.token.api");
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

	public void validateInputField(ClaimIntimationValidationReq req) throws WhatsAppValidationException, NumberFormatException, ParseException {
		List<Error> errorList = new ArrayList<Error>();
		if ("Vehicle".equalsIgnoreCase(req.getType())) {
			List<Map<String, Object>> list = repository.getDeatilsByMobileNo(req.getMobileNo(), "Policy".toUpperCase());
			Boolean status = list.stream().anyMatch(p -> p.get("BOT_OPTION_NO").equals(req.getVehicle()));
			if (!status) {
				errorList.add(new Error("Please choose valid vehicle", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("LossType".equalsIgnoreCase(req.getType())) {
			List<Map<String, Object>> list = repository.getDeatilsByMobileNo(req.getMobileNo(),
					"LossType".toUpperCase());
			Boolean status = list.stream().anyMatch(p -> p.get("BOT_OPTION_NO").equals(req.getLossType()));
			if (!status) {
				errorList.add(new Error("Please choose valid losstype", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("CauseOfLoss".equalsIgnoreCase(req.getType())) {
			List<Map<String, Object>> list = repository.getDeatilsByMobileNo(req.getMobileNo(), "CAUSEOFLOSS");
			Boolean status = list.stream().anyMatch(p -> p.get("BOT_OPTION_NO").equals(req.getCauseOfLoss()));
			if (!status) {
				errorList.add(new Error("Please choose valid causeofloss", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("AccidentDate".equalsIgnoreCase(req.getType())) {
			if (!req.getAccidentDate().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
				errorList.add(new Error("Please enter valid accidentdate format ex :DD/MM/YYYY", "ErrorMsg", "101"));
			} else {
				String dateFormat = "dd/MM/yyyy";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

				LocalDate accidentDate = LocalDate.parse(req.getAccidentDate(), formatter);
				LocalDate currentDate = LocalDate.now();
				if (accidentDate.isAfter(currentDate)) {
					errorList.add(new Error("Accidentdate does not allow future date", "ErrorMsg", "101"));
				}
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("AccidentTime".equalsIgnoreCase(req.getType())) {
			if (!req.getAccidentTime().matches("[0-9]{2}:[0-9]{2}")) {
				errorList.add(new Error("Please enter valid time format ex :HH:MM", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("DrivenBY".equalsIgnoreCase(req.getType())) {
			List<String> drivenBy = Arrays.asList("1", "2", "3");
			Boolean status = drivenBy.stream().anyMatch(p -> p.equals(req.getDrivenBy()));
			if (!status) {
				errorList.add(new Error("Please choose valid option for who has made the accident", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("Claimrefno".equalsIgnoreCase(req.getType())) {
			if (!req.getClaimrefno().matches("[A-Z]{2}-[0-9]{5}")) {
				errorList.add(new Error("ClaimRefNo is should be like this format:AB-12345", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);
		} else if ("BODY_TYPE".equalsIgnoreCase(req.getType())) {
			List<Map<String, Object>> list = repository.getDeatilsByMobileNo(req.getMobileNo(), "BODY_TYPE");
			Boolean status = list.stream().anyMatch(p -> p.get("BOT_OPTION_NO").equals(req.getBodyId()));
			if (!status) {
				errorList.add(new Error("Please choose valid bodytype", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);
		} else if ("MAKE".equalsIgnoreCase(req.getType())) {

			List<Map<String, Object>> list = repository.getDeatilsByMobileNo(req.getMobileNo(), "MAKE");

			boolean status = list.stream().anyMatch(a -> a.get("BOT_OPTION_NO").equals(req.getMakeId()));

			if (!status) {
				errorList.add(new Error("*Please choose valid Make*", "ErrorMsg", "101"));
			}

			throw new WhatsAppValidationException(errorList);

		} else if ("CHASSIS_NO".equalsIgnoreCase(req.getType())) {

			String chassisNo = req.getChassisNo();
			if (!StringUtils.isBlank(chassisNo)) {
				if (chassisNo.length() > 4 && chassisNo.length() < 21) {
					if (!StringUtils.isAlphanumeric(chassisNo)) {
						errorList.add(new Error("*Please Enter valid ChassisNo*", "ErrorMsg", "101"));
					}
				} else
					errorList.add(new Error("ChassisNo Length should be more than 4 char and lesser than 20 char",
							"ErrorMsg", "101"));
			} else {
				errorList.add(new Error("*ChassisNo is blank*", "ErrorMsg", "101"));
			}
			throw new WhatsAppValidationException(errorList);

		} else if ("ENGINE_NO".equalsIgnoreCase(req.getType())) {

			String engineNo = req.getEngineNo();
			if (!StringUtils.isBlank(engineNo)) {
				if (engineNo.length() > 4 && engineNo.length() < 21) {
					if (!StringUtils.isAlphanumeric(engineNo)) {
						errorList.add(new Error("*Please Enter valid ChassisNo*", "ErrorMsg", "101"));
					}
				} else
					errorList.add(new Error("*ChassisNo Length should be more than 4 char and lesser than 20 char*",
							"ErrorMsg", "101"));
			} else {
				errorList.add(new Error("*ChassisNo is blank*", "ErrorMsg", "101"));
			}
			throw new WhatsAppValidationException(errorList);

		} else if ("ENGINE_CAPACITY".equalsIgnoreCase(req.getType())) {

			String engineCapacity = req.getEngineCapacity();

			if (NumberUtils.isCreatable(engineCapacity)) {
				if (!(engineCapacity.length() < 5)) {
					errorList.add(new Error("*Please enter Valid Engine Number*", "ErrorMsg", "101"));
				}
			}else
				errorList.add(new Error("*Please enter Valid Engine Number*", "ErrorMsg", "101"));
			
				throw new WhatsAppValidationException(errorList);
		}

		else if ("Model".equalsIgnoreCase(req.getType())) {
			List<Map<String, Object>> list = repository.getDeatilsByMobileNo(req.getMobileNo(),
					req.getType().toUpperCase());
			Boolean status = list.stream().anyMatch(p -> p.get("BOT_OPTION_NO").equals(req.getModelId()));
			if (!status) {
				errorList.add(new Error("Please choose valid Model", "ErrorMsg", "101"));
			}
			throw new WhatsAppValidationException(errorList);
		}
		else if("Inalipa_MobileNo".equalsIgnoreCase(req.getType())) {
			if(StringUtils.isBlank(req.getMobileNo())) {
				errorList.add(new Error("*Please Enter Mobile Number*", "ErrorMsg", "500"));
			}else if(!req.getMobileNo().matches("[0-9]*")) {
				errorList.add(new Error("*Please Enter valid Mobile Number*", "ErrorMsg", "500"));
			}
			throw new WhatsAppValidationException(errorList);
		}
		else if("Inalipa_AccidentDate".equalsIgnoreCase(req.getType())) {
			if (!req.getAccidentDate().matches("[0-9]{2}/[0-9]{2}/[0-9]{4}")) {
				errorList.add(new Error("Please enter valid accidentdate format ex :DD/MM/YYYY", "ErrorMsg", "101"));
			}else {
				String dateFormat = "dd/MM/yyyy";
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
				LocalDate accidentDate = LocalDate.parse(req.getAccidentDate(), formatter);
				LocalDate currentDate = LocalDate.now();
				if (accidentDate.isAfter(currentDate)) {
					errorList.add(new Error("Accidentdate does not allow future date", "ErrorMsg", "101"));
				}else {
					String url = cs.getwebserviceurlProperty().getProperty("eway.claimDetails.api");
					LinkedHashMap<String, Object> map = new LinkedHashMap<>();
					map.put("MobileNo", req.getMobileNo());
					map.put("AccidentDate", req.getAccidentDate());
					map.put("ClaimType", req.getClaimType());
					log.info("Api Call URL ==> "+url);
					log.info("Api Call Request ==> "+printReq.toJson(map));
					String request = printReq.toJson(map);
					String response = callEwayApi(url, request);
					Map<String,Object> result = null;
					try {
						result = mapper.readValue(response, Map.class);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if("SUCCESS".equalsIgnoreCase(result.get("Message").toString())) {
						List<Map<String,Object>> resultList = (List<Map<String, Object>>) result.get("Response");
						Map<String,Object> resultMap = resultList.get(0);
						String policyNo = resultMap.get("PolicyNo")==null?"":resultMap.get("PolicyNo").toString();
						Long mobileNo = resultMap.get("MobileNo")==null?null:Long.parseLong(resultMap.get("MobileNo").toString());
						List<InalipaIntimatedTable> exitorNot = inalipaIntiRepo.getExistsClaimDetails(policyNo,mobileNo,accidentDate);
						if(CollectionUtils.isEmpty(exitorNot)) {
							String claimRefMax = repository.getInalipaClamRefMax();
							InalipaIntimatedTable in = InalipaIntimatedTable.builder()
								.policyNo(policyNo)
								.mobileNo(mobileNo)
								.policyStartDate(resultMap.get("InceptionDate")==null?null:new SimpleDateFormat("dd/MM/yyyy").parse(resultMap.get("InceptionDate").toString()))
								.policyEndDate(resultMap.get("ExpiryDate")==null?null:new SimpleDateFormat("dd/MM/yyyy").parse(resultMap.get("ExpiryDate").toString()))
								.intimatedDate(new Date())
								.ClaimType(resultMap.get("ClaimType")==null?"":resultMap.get("ClaimType").toString())
								.accidentDate(new SimpleDateFormat("dd/MM/yyyy").parse(req.getAccidentDate()))
								.claimNo(claimRefMax)
								.claimId(req.getClaimType())
								.build();
							inalipaIntiRepo.save(in);
						}else {
							errorList.add(new Error("Your Claim Already Intimated Your ClaimRefNo is *"+exitorNot.get(0).getClaimNo()+"*", "ErrorMsg", "101"));
						}
						
					}else {
						errorList.add(new Error("*"+result.get("ErrorMessage").toString()+"*", "ErrorMsg", "101"));
					}
				}
			}
			throw new WhatsAppValidationException(errorList);
		}
		else if("Inalipa_ClaimNo".equalsIgnoreCase(req.getType())) {
			InalipaIntimatedTable list=null;
			if(!req.getClaimNo().matches("[a-zA-Z]{2}-[0-9]+")) {
				errorList.add(new Error("*Kindly provide a legitimate claim registration Number.*", "ErrorMsg", "403"));
			}else {
				try {
					list = inalipaIntiRepo.findById(req.getClaimNo()).get();
				}catch(Exception e) {
					log.info("Error in Validate Inalipa_ClaimNo ==> "+e.getMessage());
				}
				if(list==null) {
					errorList.add(new Error("*No Data Found for your claim registration Number*", "ErrorMsg", "403"));
				}
			}
			throw new WhatsAppValidationException(errorList);
		}
	}

	public Object uploadPreinspection(PreInspectionReq req) {
		try {
			PreinspectionDataDetail pidd = PreinspectionDataDetail.builder()
					.chassisNo(StringUtils.isBlank(req.getChassisNo()) ? "" : req.getChassisNo()).entry_date(new Date())
					.mobileNo(req.getMobileNo())
					.registrationNo(StringUtils.isBlank(req.getRegistraionNo()) ? "" : req.getRegistraionNo())
					.status("Y").tranId(preInsDataRepo.getTranId()).build();
			PreinspectionDataDetail pdd = preInsDataRepo.save(pidd);

			Map<String, String> image = new HashMap<String, String>();
			image.put(req.getImageName1(), req.getImage1());
			image.put(req.getImageName2(), req.getImage2());
			image.put(req.getImageName3(), req.getImage3());
			image.put(req.getImageName4(), req.getImage4());
			image.put(req.getImageName5(), req.getImage5());
			image.put(req.getImageName6(), req.getImage6());
			image.put(req.getImageName7(), req.getImage7());
			image.entrySet().stream().map(m -> {
				String key = m.getKey();
				String value = m.getValue();
				String fileName = "";
				if (!"99".equalsIgnoreCase(value) && StringUtils.isNotEmpty(value)) {
					String file = value.replace("//", "\\");
					fileName = new File(file).getName();
					;
					preInsDataRepo.insertImageDetails(pdd.getTranId().toString(), key, file, new Date(), "VALID",
							fileName, "N");
				}
				if ("99".equalsIgnoreCase(value)) {
					fileName = "Image was skipped";
					preInsDataRepo.insertImageDetails(pdd.getTranId().toString(), key, value, new Date(), "VALID",
							fileName, "N");
				}
				return "success";
			}).collect(Collectors.toList());

			Map<String, String> response = new HashMap<String, String>();
			String link = cs.getwebserviceurlProperty().getProperty("wa.preins.screen.link").replace("{TranId}",
					pdd.getTranId().toString());
			String tinyUrl = insuranceServiceImpl.getTinyUrl(link);
			response.put("TinyUrl", tinyUrl);

			return response;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Object checkClaimStatus(ClaimStatus req) throws WhatsAppValidationException {
		String response = "";
		String api = "";
		Map<String, Object> request = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(req.getPolicyNo())) {
			request.put("PolicyNo", req.getPolicyNo());
			request.put("InsuranceId", req.getCompanyId());
			api = cs.getwebserviceurlProperty().getProperty("claim.status.bypolicy");
		} else if (StringUtils.isNotBlank(req.getChassisNo())) {
			request.put("ChassisNo", req.getChassisNo());
			request.put("InsuranceId", req.getCompanyId());
			api = cs.getwebserviceurlProperty().getProperty("claim.status.bychassis");

		}
		log.info("checkClaimStatus API " + api);
		String reqString = cs.reqPrint(request);
		log.info("checkClaimStatus Request " + reqString);
		response = callApi(api, reqString);
		log.info("checkClaimStatus Response " + response);
		Map<String, Object> map = null;
		try {
			map = mapper.readValue(response, Map.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("Intimated Details");
		if ("CLAIM_STATUS".equalsIgnoreCase(req.getType())) {
			if (!CollectionUtils.isEmpty(list)) {
				String message = list.stream().map(m -> {
					String claimRefNo = m.get("Claimrefno") == null ? "N/A" : m.get("Claimrefno").toString();
					String claimIntimateDate = m.get("Entrydate") == null ? "N/A" : m.get("Entrydate").toString();
					String accidentDate = m.get("Accidentdate") == null ? "N/A" : m.get("Accidentdate").toString();
					String chassisNo = m.get("ChassisNo") == null ? "N/A" : m.get("ChassisNo").toString();
					String claimStatus = m.get("ClaimStatus") == null ? "N/A" : m.get("ClaimStatus").toString();
					return "ChassisNo : " + chassisNo + "\nClaimRefNo : " + claimRefNo + "\nClaim Status : "
							+ claimStatus + "\nClaim IntimateDate : " + claimIntimateDate + "\n" + "AccidentDate : "
							+ accidentDate + "";
				}).collect(Collectors.joining("\n\n"));
				String policyNo = list.get(0).get("PolicyNo") == null ? "" : list.get(0).get("PolicyNo").toString();
				String mobileNo = list.get(0).get("Contactno") == null ? "N/A"
						: list.get(0).get("Contactno").toString();
				String chassisNo = list.get(0).get("ChassisNo") == null ? "" : list.get(0).get("ChassisNo").toString();

				Map<String, Object> retRes = new HashMap<String, Object>();
				retRes.put("Response", message);
				retRes.put("PolicyNo", policyNo);
				retRes.put("ChassisNo", chassisNo);
				retRes.put("MobileNo", mobileNo);

				return retRes;
			} else {
				List<Error> errorList = new ArrayList<Error>();
				errorList.add(new Error("No Record found", "ErrorMsg", "100"));
				throw new WhatsAppValidationException(errorList);
			}
		} else if ("CLAIM_UPLOAD".equalsIgnoreCase(req.getType())) {
			String message = "";
			if (!CollectionUtils.isEmpty(list)) {
				String policyNo = list.get(0).get("PolicyNo") == null ? "" : list.get(0).get("PolicyNo").toString();
				String mobileNo = list.get(0).get("Contactno") == null ? "N/A"
						: list.get(0).get("Contactno").toString();
				String chassisNo = list.get(0).get("ChassisNo") == null ? "" : list.get(0).get("ChassisNo").toString();
				List<ClaimIntimationEntity> entities = new ArrayList<ClaimIntimationEntity>();
				Long serialNo = repository.getSerialNo();
				int count = 1;
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> obj = list.get(i);
					ClaimIntimationEntity entity = ClaimIntimationEntity.builder()
							.accidentDate(obj.get("Accidentdate") == null ? "N/A" : obj.get("Accidentdate").toString())
							.claimRefNo(obj.get("Claimrefno") == null ? "N/A" : obj.get("Claimrefno").toString())
							.apiType("CLAIM_UPLOAD")
							.claimStatus(obj.get("ClaimStatus") == null ? "N/A" : obj.get("ClaimStatus").toString())
							.policyNo(policyNo).chassisNo(chassisNo).botOptionNo(String.valueOf(count))
							.insuranceId(req.getCompanyId()).mobileNo(req.getMobileNo()).serialNo(serialNo).build();
					entities.add(entity);
					count++;
				}
				List<ClaimIntimationEntity> result = repository.saveAll(entities);
				message = result.stream().map(p -> {
					String claimRefNo = p.getClaimRefNo();
					String botNo = p.getBotOptionNo();
					String claimStatus = p.getClaimStatus();
					return "*Choose " + botNo + "* : " + claimRefNo + "/" + claimStatus + " ";
				}).collect(Collectors.joining("\n\n"));
				Map<String, Object> retRes = new HashMap<String, Object>();
				retRes.put("Response", message);
				retRes.put("PolicyNo", policyNo);
				retRes.put("ChassisNo", chassisNo);
				retRes.put("MobileNo", mobileNo);

				return retRes;
			} else {
				Map<String, Object> retRes = new HashMap<String, Object>();
				retRes.put("ErrorDesc", "No Record Found");
				return retRes;

			}

		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Object shortTermPolicy(ShortTermPolicyReq req) {
		String errorDesc = "";
		Map<String, String> retResponse = new HashMap<>();

		try {
			if ("BODY_TYPE".equalsIgnoreCase(req.getType())) {
				String url = cs.getwebserviceurlProperty().getProperty("eway.bodytype.api");
				LinkedHashMap<String, Object> map = new LinkedHashMap<>();
				map.put("InsuranceId", req.getInsuranceId());
				map.put("BranchCode", req.getBranchCode());
				String request = printReq.toJson(map);
				String response = callEwayApi(url, request);
				Map<String, Object> resMap = null;
				try {
					resMap = mapper.readValue(response, Map.class);
				} catch (Exception e) {
					e.printStackTrace();
				}

				List<Map<String, Object>> bodyType = resMap.get("Result") == null ? Collections.emptyList()
						: (List<Map<String, Object>>) resMap.get("Result");

				if (bodyType.size() > 0) {

					int count = 1;

					Long serialNo = repository.getSerialNo();

					List<ClaimIntimationEntity> list = new ArrayList<>();

					for (int i = 0; i < bodyType.size(); i++) {

						Map<String, Object> mapList = bodyType.get(i);

						ClaimIntimationEntity entity = ClaimIntimationEntity.builder().mobileNo(req.getMobileNumber())
								.botOptionNo(String.valueOf(count + i))
								.code(mapList.get("Code") == null ? "" : mapList.get("Code").toString())
								.codeDesc(mapList.get("CodeDesc") == null ? "" : mapList.get("CodeDesc").toString())
								.serialNo(serialNo).apiType(req.getType()).build();
						list.add(entity);

						// if(i==9)
						// break;
					}

					List<ClaimIntimationEntity> data = repository.saveAll(list);

					String message = data.stream().map(m -> {
						String codeDesc = StringUtils.isBlank(m.getCodeDesc()) ? "N/A" : m.getCodeDesc();
						String optionNo = StringUtils.isBlank(m.getBotOptionNo()) ? "N/A" : m.getBotOptionNo();
						return "*Choose " + optionNo + "* : " + codeDesc + "";
					}).collect(Collectors.joining("\n"));
					retResponse.put("Response", message);
					retResponse.put("ErrorDesc", "");
				} else {
					errorDesc = "No Records found";
					retResponse.put("Response", errorDesc);
					retResponse.put("ErrorDesc", "");
				}
			} else if ("MAKE".equalsIgnoreCase(req.getType())) {

				String url = cs.getwebserviceurlProperty().getProperty("eway.make.api");

				LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();

				reqMap.put("BodyId", repository.getType(req.getMobileNumber(), "BODY_TYPE", req.getBodyId()));
				reqMap.put("InsuranceId", req.getInsuranceId());
				reqMap.put("BranchCode", req.getBranchCode());

				String request = printReq.toJson(reqMap);

				String response = callEwayApi(url, request);

				Map<String, Object> resMap = null;

				try {

					resMap = mapper.readValue(response, Map.class);
				} catch (Exception e) {

					e.printStackTrace();
				}

				List<Map<String, Object>> make = resMap.get("Result") == null ? Collections.emptyList()
						: (List<Map<String, Object>>) resMap.get("Result");

				if (make.size() > 0) {

					List<ClaimIntimationEntity> list = new ArrayList<ClaimIntimationEntity>();

					Long serialNo = repository.getSerialNo();

					int count = 1;

					for (int i = 0; i < make.size(); i++) {

						Map<String, Object> makeList = make.get(i);

						ClaimIntimationEntity record = ClaimIntimationEntity.builder().mobileNo(req.getMobileNumber())
								.apiType(req.getType()).botOptionNo(String.valueOf(count + i))
								.code(makeList.get("Code") == null ? "" : makeList.get("Code").toString())
								.serialNo(serialNo)
								.codeDesc(makeList.get("CodeDesc") == null ? "" : makeList.get("CodeDesc").toString())
								.build();

						list.add(record);
					}

					List<ClaimIntimationEntity> data = repository.saveAll(list);

					String message = data.stream().map(item -> {
						String codeDesc = StringUtils.isEmpty(item.getCodeDesc()) ? "N/A" : item.getCodeDesc();
						String userOpted = StringUtils.isEmpty(item.getBotOptionNo()) ? "N/A" : item.getBotOptionNo();

						return "*Choose " + userOpted + "* : " + codeDesc + "";
					}).collect(Collectors.joining("\n"));

					retResponse.put("Response", message);
					retResponse.put("ErrorDesc", "");
				} else {
					errorDesc = "No Records found";
					retResponse.put("Response", errorDesc);
					retResponse.put("ErrorDesc", "");
				}

			} else if ("MODEL".equalsIgnoreCase(req.getType())) {

				String url = cs.getwebserviceurlProperty().getProperty("eway.model.api");

				String makeId = repository.getType(req.getMobileNumber(), "MAKE", req.getMakeId());

				String bodyId = repository.getType(req.getMobileNumber(), "BODY_TYPE", req.getBodyId());

				Map<String, Object> reqMap = new HashMap<>();

				reqMap.put("InsuranceId", req.getInsuranceId());
				reqMap.put("BranchCode", req.getBranchCode());
				reqMap.put("MakeId", makeId);
				reqMap.put("BodyId", bodyId);

				String request = printReq.toJson(reqMap);

				String response = callEwayApi(url, request);
				Map<String, Object> map = mapper.readValue(response, Map.class);
				List<Map<String, Object>> res = new ArrayList<>();

				try {
					res = (List<Map<String, Object>>) map.get("Result");
				} catch (Exception e) {

				}
				Long serialNo = repository.getSerialNo();
				if (res.size() > 0) {
					List<ClaimIntimationEntity> list = new ArrayList<>();
					for (int i = 0; i < res.size(); i++) {
						Map<String, Object> resMap = res.get(i);
						ClaimIntimationEntity entity = ClaimIntimationEntity.builder()
								.code(resMap.get("Code") == null ? "" : resMap.get("Code").toString())
								.codeDesc(resMap.get("CodeDesc") == null ? "" : resMap.get("CodeDesc").toString())
								.apiType(req.getType()).serialNo(serialNo).botOptionNo(String.valueOf(i + 1))
								.mobileNo(req.getMobileNumber()).build();

						list.add(entity);
					}
					List<ClaimIntimationEntity> cList = repository.saveAll(list);

					String message = cList.stream().map(item -> {
						String codeDesc = StringUtils.isEmpty(item.getCodeDesc()) ? "N/A" : item.getCodeDesc();
						String userOpted = StringUtils.isEmpty(item.getBotOptionNo()) ? "N/A" : item.getBotOptionNo();

						return "*Choose " + userOpted + "* : " + codeDesc + "";
					}).collect(Collectors.joining("\n"));

					retResponse.put("Response", message);
					retResponse.put("ErrorDesc", "");
				} else {
					errorDesc = "No Records found";
					retResponse.put("Response", errorDesc);
					retResponse.put("ErrorDesc", "");
				}

			}
//			else if ("COLOR".equalsIgnoreCase(req.getType())) {
//
//				String url = cs.getwebserviceurlProperty().getProperty("eway.color.api");
//
//				LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();
//
//				reqMap.put("InsuranceId", req.getInsuranceId());
//				reqMap.put("BranchCode", req.getBranchCode());
//
//				String request = printReq.toJson(reqMap);
//
//				String response = callEwayApi(url, request);
//
//				Map<String, Object> resMap = null;
//
//				try {
//					resMap = mapper.readValue(response, Map.class);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//				List<Map<String, Object>> color = resMap.get("Result") == null ? Collections.emptyList()
//						: (List<Map<String, Object>>) resMap.get("Result");
//
//				if (color.size() > 0) {
//
//					List<ClaimIntimationEntity> list = new ArrayList<ClaimIntimationEntity>();
//
//					int count = 1;
//
//					Long serialNo = repository.getSerialNo();
//
//					for (Map<String, Object> result : color) {
//
//						ClaimIntimationEntity record = ClaimIntimationEntity.builder().mobileNo(req.getMobileNumber())
//								.apiType(req.getType()).serialNo(serialNo)
//								.code(result.get("Code") == null ? "" : result.get("Code").toString())
//								.codeDesc(result.get("CodeDesc") == null ? "" : result.get("CodeDesc").toString())
//								.botOptionNo(String.valueOf(count++)).build();
//
//						list.add(record);
//					}
//
//					List<ClaimIntimationEntity> table = repository.saveAll(list);
//
//					String message = table.stream().map(record -> {
//						String codeDesc = StringUtils.isEmpty(record.getCodeDesc()) ? "N/A" : record.getCodeDesc();
//						String userOpted = StringUtils.isEmpty(record.getBotOptionNo()) ? "N/A"
//								: record.getBotOptionNo();
//
//						return "*Choose " + userOpted + " :* " + codeDesc;
//
//					}).collect(Collectors.joining("\n"));
//
//					retResponse.put("Response", message);
//					retResponse.put("ErrorDesc", "");
//				} else {
//					errorDesc = "No Records found";
//					retResponse.put("Response", errorDesc);
//					retResponse.put("ErrorDesc", "");
//				}
//			}
			else if ("FUEL".equalsIgnoreCase(req.getType())) {
				String url = cs.getwebserviceurlProperty().getProperty("eway.fuel.api");

				LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();

				reqMap.put("InsuranceId", req.getInsuranceId());
				reqMap.put("BranchCode", req.getBranchCode());

				String request = printReq.toJson(reqMap);

				String response = callEwayApi(url, request);

				Map<String, Object> resMap = null;

				try {

					resMap = mapper.readValue(response, Map.class);
				} catch (Exception e) {

					e.printStackTrace();
				}

				List<Map<String, Object>> make = resMap.get("Result") == null ? Collections.emptyList()
						: (List<Map<String, Object>>) resMap.get("Result");

				if (make.size() > 0) {

					List<ClaimIntimationEntity> list = new ArrayList<ClaimIntimationEntity>();

					Long serialNo = repository.getSerialNo();

					int count = 1;

					for (int i = 0; i < make.size(); i++) {

						Map<String, Object> makeList = make.get(i);

						ClaimIntimationEntity record = ClaimIntimationEntity.builder().mobileNo(req.getMobileNumber())
								.apiType(req.getType()).botOptionNo(String.valueOf(count + i))
								.code(makeList.get("Code") == null ? "" : makeList.get("Code").toString())
								.serialNo(serialNo)
								.codeDesc(makeList.get("CodeDesc") == null ? "" : makeList.get("CodeDesc").toString())
								.build();

						list.add(record);
					}

					List<ClaimIntimationEntity> data = repository.saveAll(list);

					String message = data.stream().map(item -> {
						String codeDesc = StringUtils.isEmpty(item.getCodeDesc()) ? "N/A" : item.getCodeDesc();
						String userOpted = StringUtils.isEmpty(item.getBotOptionNo()) ? "N/A" : item.getBotOptionNo();

						return "*Choose " + userOpted + "* : " + codeDesc + "";
					}).collect(Collectors.joining("\n"));

					retResponse.put("Response", message);
					retResponse.put("ErrorDesc", "");
				} else {
					errorDesc = "No Records found";
					retResponse.put("Response", errorDesc);
					retResponse.put("ErrorDesc", "");
				}

			} else if ("VehicleUsage".equalsIgnoreCase(req.getType())) {

				String url = cs.getwebserviceurlProperty().getProperty("eway.vehicle.api");

				LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();

				reqMap.put("InsuranceId", req.getInsuranceId());
				reqMap.put("BranchCode", req.getBranchCode());

				String request = printReq.toJson(reqMap);

				String response = callEwayApi(url, request);

				Map<String, Object> resMap = null;

				try {

					resMap = mapper.readValue(response, Map.class);

				} catch (Exception e) {
					e.printStackTrace();
				}

				List<Map<String, Object>> listMap = resMap.get("Result") == null ? Collections.emptyList()
						: (List<Map<String, Object>>) resMap.get("Result");

				if (listMap.size() > 0) {

					List<ClaimIntimationEntity> list = new ArrayList<ClaimIntimationEntity>();
					int count = 1;

					Long serial = repository.getSerialNo();

					for (Map<String, Object> result : listMap) {

						ClaimIntimationEntity record = ClaimIntimationEntity.builder().mobileNo(req.getMobileNumber())
								.botOptionNo(String.valueOf(count++))
								.code(result.get("Code") == null ? "" : result.get("Code").toString())
								.codeDesc(result.get("CodeDesc") == null ? "" : result.get("CodeDesc").toString())
								.serialNo(serial).build();

						list.add(record);
					}

					List<ClaimIntimationEntity> table = repository.saveAll(list);

					String message = table.stream().map(item -> {
						String codeDesc = StringUtils.isEmpty(item.getCodeDesc()) ? "N/A" : item.getCodeDesc();
						String userOpted = StringUtils.isEmpty(item.getBotOptionNo()) ? "N/A" : item.getBotOptionNo();

						return "*Choose " + userOpted + "* : " + codeDesc + "";
					}).collect(Collectors.joining("\n"));

					retResponse.put("Response", message);
					retResponse.put("ErrorDesc", "");
				} else {
					errorDesc = "No Records found";
					retResponse.put("Response", errorDesc);
					retResponse.put("ErrorDesc", "");
				}

			}

		} catch (

		Exception e) {
			log.error(e);
			retResponse.put("ErrorDesc", e.getMessage());
			return retResponse;
		}
		return retResponse;
	}

	public void saveCustomer() {

	}

	public Object shortTermPolicyResponse(ShortTermPolicyReq req) {

		String url = "http://102.69.166.162:8080/EwayCommonApi/api/getcustomerdetails";

		LinkedHashMap<String, Object> reqMap = new LinkedHashMap<String, Object>();

		reqMap.put("CustomerReferenceNo", "Cust-00917");

		String request = printReq.toJson(reqMap);

		String response = callEwayApi(url, request);

		Map<String, Object> resMap = null;

		try {

			resMap = mapper.readValue(response, Map.class);
		} catch (Exception e) {

			e.printStackTrace();
		}

		Map<String, Object> map = (Map<String, Object>) resMap.get("Result");

		String custno = (String) map.get("CustomerReferenceNo");
		String idNumber = (String) map.get("Idnumber");
		String customerName = (String) map.get("CustomerName");

		return null;
	}

	public Object getClaimRefNo(ClaimRefNoReq req) throws WhatsAppValidationException {
		log.info("Enter into getClaimRefNo");
		List<Error> error = new ArrayList<>();
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			LocalDate accDate = LocalDate.parse(req.getAccidentDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			List<InalipaIntimatedTable> list = inalipaIntiRepo.getClaimUploadDetails(req.getMobileNo(), accDate);
			if(!CollectionUtils.isEmpty(list)) {
				result.put("Response", list.get(0).getClaimNo());
				result.put("ErrorMsg", "");
			}else {
				error.add(new Error("*Failed to Receive Claim Intimation? Please Contact Admin*","ErrorMsg","403"));
			}
			log.info("Exit into getClaimRefNo");
		}catch(Exception e) {
			log.info("Error in getClaimRefNo ==> "+e.getMessage());
			result.put("ErrorMsg", e.getMessage());
			e.printStackTrace();
		}
		if(!error.isEmpty())throw new WhatsAppValidationException(error);
		return result;
	}
	
	@PostConstruct
	private void generateJosn() {
		try {
			String json ="{statusString=SENT,waId=919566362141,senderName=Jbaskar96}";
			
			System.out.println(json);
			   
			Map<String, String> actual = Arrays.stream(json.replace("{", "").replace("}", "").split(","))
		            .map(arrayData-> arrayData.split("="))
		            .collect(Collectors.toMap(d-> ((String)d[0]).trim(), d-> (String)d[1]));

		
			System.out.println(actual);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
