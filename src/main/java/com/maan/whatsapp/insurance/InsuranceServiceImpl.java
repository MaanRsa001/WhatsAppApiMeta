package com.maan.whatsapp.insurance;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.claimintimation.ClaimIntimationEntity;
import com.maan.whatsapp.claimintimation.ClaimIntimationRepository;
import com.maan.whatsapp.claimintimation.ClaimIntimationServiceImpl;
import com.maan.whatsapp.config.exception.WhatsAppValidationException;
import com.maan.whatsapp.meta.FlowCreateQuoteReq;
import com.maan.whatsapp.response.error.Error;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

@Service
@PropertySource("classpath:WebServiceUrl.properties")
public class InsuranceServiceImpl implements InsuranceService{
	
		
	@Autowired
	private ClaimIntimationServiceImpl serviceImpl;
	
	@Autowired
	private ClaimIntimationRepository claimIntimationRepository;
	
	@Autowired
	private AsyncProcessThread aysyncThread;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private Gson objectPrint;
	
	@Value("${eway.motor.tira.api}")
	private String tiraApi;
	@Value("${eway.motor.policy.holder.api}")
	private String policyHolderApi;
	@Value("${eway.motor.save.api}")
	private String saveMotorApi;
	@Value("${eway.motor.calc.api}")
	private String calcApi;
	@Value("${eway.motor.view.calc.api}")
	private String viewCalcApi;
	@Value("${eway.motor.vehicle.update.api}")
	private String vehUpdateApi;
	@Value("${eway.motor.section.api}")
	private String sectionApi;
	@Value("${eway.motor.usage.api}")
	private String usageApi;
	@Value("${eway.motor.customer.api}")
	private String customerApi;
	@Value("${eway.motor.buypolicy}")
	private String buyploicyApi;
	@Value("${eway.motor.makepayment}")
	private String makePaymentApi;
	@Value("${eway.motor.insertpayment}")
	private String insertPaymentApi;
	@Value("${eway.motor.createlogin}")
	private String ewayLoginCreateApi;
	@Value("${eway.motor.paymentlink}")						
	private String ewayMotorPaymentLink;
	@Value("${eway.motor.selcom.payementApi}")						
	private String ewayMotorSelcomPaymentApi;
	@Value("${eway.motor.selcom.paymentCheckApi}")						
	private String ewaySelcomPaymentCheckApi;
	@Value("${eway.motor.policy.document}")						
	private String motorPolicyDocumentApi;
	@Value("${whatsapp.api.button}")						
	private String whatsappApiButton;
	@Value("${whatsapp.auth}")						
	private String whatsappAuth;
	@Value("${eway.motor.redirect.url}")						
	private String redirectUrl;
	@Value("${whatsapp.api}")						
	private String whatsappApi;
	@Value("${whatsapp.api.sendSessionMessage}")						
	private String whatsappApiSendSessionMessage;
	@Value("${tira.post.api}")						
	private String tiraPostApi;
	
	@Value("${turl.api}")						
	private String turlApi;
	
	
	
	
	Logger log = LogManager.getLogger(getClass());
	
	
	private static final List<String> ID_TYPES =Arrays.asList("1","2","3","4","5","6");

	@Override
	public void validateInputField(InsuranceReq req) throws WhatsAppValidationException {
		
		List<Error> list = new ArrayList<>();

		if("RegistrationNo".equalsIgnoreCase(req.getType())) {
			if(!req.getRegisrationNo().matches("[a-zA-Z0-9]*")){
				list.add(new Error("Special character or Whitespace does not allow  for Registration no","ErrorMsg","500"));
			}else if(StringUtils.isNotBlank(req.getRegisrationNo())) {
				Map<String,Object> tiraMap=checkRegistrationWithTira(req.getRegisrationNo());
				if(tiraMap==null) {
					list.add(new Error("No Record found for you entered Registration No (*"+req.getRegisrationNo()+"*)"
							+ "Registration No . PLease try with different Registration No","ErrorMsg","500"));
				}else {
					String errorMessage =tiraMap.get("ErrorMessage")==null?"":tiraMap.get("ErrorMessage").toString();
					if(StringUtils.isNotBlank(errorMessage)) {
						list.add(new Error("*Sorry...! we can insure only vehicle which will be below 30 days of policy expiry date ("+req.getRegisrationNo()+")*","ErrorMsg","500"));
					}
				}
			}
		}else if("IDType".equalsIgnoreCase(req.getType())) {
			
			Boolean status =ID_TYPES.stream().anyMatch(p ->p.equals(req.getIdType()));
			if(!status) {
				list.add(new Error("Please choose valid IDType","ErrorMsg","500"));
			}
		}else if("IDNumber".equalsIgnoreCase(req.getType())) {
			if(!req.getIdNumber().matches("[a-zA-Z0-9]*")){
				list.add(new Error("Special character or Whitespace does not allow for IDNumber","ErrorMsg","500"));
			}
		}else if("CustomerName".equalsIgnoreCase(req.getType())) {
			if(!req.getCustomerName().matches("[a-zA-Z\\s]*")){
				list.add(new Error("Special character does not allow for CustomerName","ErrorMsg","500"));
			}
		}else if("SumInsured".equalsIgnoreCase(req.getType())) {
			if(!req.getSumInsured().matches("[0-9]*")){
				list.add(new Error("SumInsured allows only digits","ErrorMsg","500"));
			}else if("0".equals(req.getSumInsured())) {
				list.add(new Error("SumInsured should be greater than 0.","ErrorMsg","500"));
			}
		}else if("MOTOR_SECTION".equalsIgnoreCase(req.getType())) {
			List<Map<String,Object>> sectionList =claimIntimationRepository.getDeatilsByMobileNo(req.getMobileNo(),"MOTOR_SECTION");
			Boolean status =sectionList.stream().anyMatch(p ->p.get("BOT_OPTION_NO").toString().equals(req.getSectionId()));
			if(!status) {
				list.add(new Error("Please choose valid option for VehicleType","ErrorMsg","500"));
			}
		}else if("MOTOR_USAGE".equalsIgnoreCase(req.getType())) {
			List<Map<String,Object>> sectionList =claimIntimationRepository.getDeatilsByMobileNo(req.getMobileNo(),"MOTOR_USAGE");
			Boolean status =sectionList.stream().anyMatch(p ->p.get("BOT_OPTION_NO").toString().equals(req.getMotorUsageId()));
			if(!status) {
				list.add(new Error("Please choose valid option for MotorUsage","ErrorMsg","500"));
			}
		}else if("CLAIMTYPE".equalsIgnoreCase(req.getType())) {
			
			if(!"yes".equalsIgnoreCase(req.getClaimType()) && !"no".equalsIgnoreCase(req.getClaimType())) {
				list.add(new Error("Please choose valid claim type","ErrorMsg","500"));

			}
		}else if("AirtelPayNo".equalsIgnoreCase(req.getType())) {
			
			if(!req.getAirtelPayNo().matches("0?[0-9]{9}")) {
				list.add(new Error("Please enter valid mobile number which should be 9 digits","ErrorMsg","500"));
			}
		}
		
		if(list.size()>0) {
			throw new WhatsAppValidationException(list);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object generateQuote(InsuranceReq req) throws WhatsAppValidationException {
		    List<Error> errorList = new ArrayList<>();
			String response ="";
			String companyId ="100002";
			
			//==============================TIRA CHECK BLOCK START=============================================
			
			log.info("TIRA BLOCK START TIME : "+new Date());
			
			Map<String,String> tiraMap = new HashMap<String,String>();
			tiraMap.put("ReqChassisNumber", "");
			tiraMap.put("ReqRegNumber", req.getRegisrationNo());
			tiraMap.put("InsuranceId", companyId);
			tiraMap.put("BranchCode", "02");
			tiraMap.put("BrokerBranchCode", "01");
			tiraMap.put("ProductId", "5");
			tiraMap.put("CreatedBy", "guest");
			tiraMap.put("SavedFrom", "API");
			Map<String,Object> policyHolder =null;
			Map<String,Object> tiraResult =null;

			try {
				String tiraReq =mapper.writeValueAsString(tiraMap);
				String tiraApi =this.tiraApi;
				
				response= serviceImpl.callEwayApi(tiraApi,tiraReq);
				Map<String,Object> strToMap =mapper.readValue(response, Map.class);
				tiraResult =strToMap.get("Result")==null?null:
					mapper.readValue(mapper.writeValueAsString(strToMap.get("Result")), Map.class);
				
				if(tiraResult!=null) {
					 policyHolder =tiraResult.get("PolicyHolderInfo")==null?null:
						mapper.readValue(mapper.writeValueAsString(tiraResult.get("PolicyHolderInfo")), Map.class);
				}
				
				if(policyHolder==null) {
					String policyHolderApi =this.policyHolderApi;
					response= serviceImpl.callEwayApi(policyHolderApi,tiraReq);
					Map<String,Object> strToMap1 =mapper.readValue(response, Map.class);
					policyHolder =strToMap1.get("Result")==null?null:
						mapper.readValue(mapper.writeValueAsString(strToMap1.get("Result")), Map.class);
				}
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			log.info("TIRA BLOCK END : "+new Date());

			//==============================TIRA CHECK BLOCK END=============================================

			
			//==============================CUSTOMER CREATION BLOCK START=============================================

			log.info("CUSTOMER INSERT BLOCK START : "+new Date());
			
			String airtelPayno =req.getAirtelPayNo().startsWith("0")?req.getAirtelPayNo().substring(1):req.getAirtelPayNo();
			String nida_date_of_birth ="";
			Map<String,Object> customerMap= new HashMap<String,Object>();
			String customerRefNo="";
			if("2".equals(req.getIdType())) {
				String nida_no =req.getIdNumber();
				if(nida_no.length()>7) {
					String subStr =nida_no.substring(0, 8);
					String year =subStr.substring(0,4);
					String month =subStr.substring(4,6);
					String date =subStr.substring(6,8);
					nida_date_of_birth =date+"/"+month+"/"+year;
					System.out.println("NIDA ===> DATE OF BIRTH "+ nida_date_of_birth);
				}	
			}
			
			if(policyHolder==null) {
				String api =this.customerApi;
				String id=req.getIdType();
				customerMap.put("InsuranceId", "100002");
				customerMap.put("BranchCode", "02");
				customerMap.put("ProductId", "5");
				customerMap.put("BrokerBranchCode", "1");
				customerMap.put("ClientName", req.getCustomerName());
				customerMap.put("CreatedBy", "guest");
				customerMap.put("BusinessType", "1");
				customerMap.put("IdNumber", req.getIdNumber());
				customerMap.put("Clientstatus", "Y");
				customerMap.put("IdType", "");
				customerMap.put("Title", "1");
				customerMap.put("SaveOrSubmit", "Save");
				customerMap.put("Status", "Y");
				customerMap.put("MobileNo1",airtelPayno);
				customerMap.put("MobileCode1", "255");
				customerMap.put("PolicyHolderType", "1");
				customerMap.put("Nationality", "TZA");
				customerMap.put("Gender", "M");
				
				customerMap.put("CityName", "Ilala");
				customerMap.put("CityCode", "11000");
				customerMap.put("RegionCode", "02");
				customerMap.put("StateCode", "10000");
				customerMap.put("StateName", "Dar es Salaam");
				customerMap.put("Street", "7th FLOOR,Exim Tower,Ghana Avenue");
				customerMap.put("Address1", "P.O.Box 9942,Dar es Salaam");
				
				
				customerMap.put("IsTaxExempted", "N");
				String idType ="1".equals(id)?"4":"2".equals(id)?"1":"3".equals(id)?"3":
					"4".equals(id)?"6":"5".equals(id)?"2":"5";
				customerMap.put("PolicyHolderTypeid", idType);
				
				customerMap.put("DobOrRegDate", idType.equals("1")?nida_date_of_birth:"13/01/2004"); 	
				String customerReq =objectPrint.toJson(customerMap);
				response =serviceImpl.callEwayApi(api, customerReq);
			    Map<String,Object> customerRes = null;
			    Map<String,Object> result =null;
				try {
					customerRes=mapper.readValue(response, Map.class);
					result =mapper.readValue(mapper.writeValueAsString(customerRes.get("Result")) , Map.class);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				customerRefNo =result.get("SuccessId")==null?"":result.get("SuccessId").toString();
			}else {
				
				String api =this.customerApi;
				String id=req.getIdType();
				customerMap.put("InsuranceId", "100002");
				customerMap.put("BranchCode", "02");
				customerMap.put("ProductId", "5");
				customerMap.put("BrokerBranchCode", "1");
				customerMap.put("ClientName", req.getCustomerName());
				customerMap.put("CreatedBy", "guest");
				customerMap.put("BusinessType", "1");
				customerMap.put("IdNumber", req.getIdNumber());
				customerMap.put("Clientstatus", "Y");
				customerMap.put("IdType", "");
				customerMap.put("Title", "1");
				customerMap.put("SaveOrSubmit", "Save");
				customerMap.put("Status", "Y");
				customerMap.put("MobileNo1",airtelPayno);
				customerMap.put("MobileCode1","255");
				
				customerMap.put("PolicyHolderType", "1");
				customerMap.put("CityName", policyHolder.get("Districtname")==null?"Ilala":policyHolder.get("Districtname").toString());
				customerMap.put("CityCode", policyHolder.get("Districtcode")==null?"11000":policyHolder.get("Districtcode").toString());
				customerMap.put("Nationality", "TZA");
				customerMap.put("Gender", policyHolder.get("Gender")==null?"":policyHolder.get("Gender").toString());
				customerMap.put("RegionCode",policyHolder.get("Regioncode")==null?"02":policyHolder.get("Regioncode").toString());
				customerMap.put("StateCode",policyHolder.get("Districtcode")==null?"10000":policyHolder.get("Districtcode").toString());
				customerMap.put("StateName",policyHolder.get("Regionname")==null?"Dar es Salaam":policyHolder.get("Regionname").toString());
				customerMap.put("Street", policyHolder.get("Regionname")==null?"7th FLOOR,Exim Tower,Ghana Avenue":policyHolder.get("Regionname").toString());
				customerMap.put("Address1",policyHolder.get("Districtname")==null?"P.O.Box 9942,Dar es Salaam":policyHolder.get("Districtname").toString());
				customerMap.put("IsTaxExempted", "N");
				
				String idType ="1".equals(id)?"4":"2".equals(id)?"1":"3".equals(id)?"3":
					"4".equals(id)?"6":"5".equals(id)?"2":"5";
				customerMap.put("PolicyHolderTypeid", idType);
				
				customerMap.put("DobOrRegDate", idType.equals("1")?nida_date_of_birth:"13/01/2004"); 	
				String customerReq =objectPrint.toJson(customerMap);
				response =serviceImpl.callEwayApi(api, customerReq);
			    Map<String,Object> customerRes = null;
			    Map<String,Object> result =null;
				try {
					customerRes=mapper.readValue(response, Map.class);
					result =mapper.readValue(mapper.writeValueAsString(customerRes.get("Result")) , Map.class);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				customerRefNo =result.get("SuccessId")==null?"":result.get("SuccessId").toString();
			}
			
			
			String customerCreation =objectPrint.toJson(customerMap);
			
			
			System.out.println("CustomerCreation + ||" +customerCreation);
			
			log.info("CUSTOMER INSERT BLOCK END : "+new Date());

			
			if(StringUtils.isBlank(customerRefNo) && policyHolder==null) {
				errorList.add(new Error("CUSTOMER CREATION FAILED || CONTACT ADMIN..!","ErrorMsg","500"));
			}
			
			if(errorList.size()>0) {
				throw new WhatsAppValidationException(errorList);
			}
				
			//==============================CUSTOMER CREATION BLOCK END=============================================

			
			//==============================QUOTATION BLOCK START=============================================

			    Map<String,String> policyDate=findPolicyDates(tiraResult);
				
				String policyStartDate =policyDate.get("PolicyStartDate");
				String policyEndDate =policyDate.get("PolicyEndDate");
				String bodyTypeDesc =tiraResult.get("VehicleType")==null?"":tiraResult.get("VehicleType").toString();
				Map<String,String> motorUsageMap =new HashMap<>();
				motorUsageMap.put("CompanyId", companyId);
				motorUsageMap.put("BodyType", bodyTypeDesc);
				motorUsageMap.put("MotorUsageName", "Private or Normal");
				
				String motorUsageApi =this.vehUpdateApi;
				Gson gson = new Gson();
				String motorUsageReq =gson.toJson(motorUsageMap);
				response =serviceImpl.callEwayApi(motorUsageApi, motorUsageReq);
			    Map<String,Object> motorUsageRes = null;
				try {
					motorUsageRes=mapper.readValue(response, Map.class);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				System.out.println(motorUsageRes);
				
				String bodyType =motorUsageRes.get("BodyId")==null?"":motorUsageRes.get("BodyId").toString();
				
				if(StringUtils.isBlank(bodyType)) {
					errorList.add(new Error("BODYTYPE NOT FOUND || REGISTRATION NO : "+req.getRegisrationNo()+"|| BODY TYPE : "+bodyTypeDesc+" || CONTACT ADMIN..!","ErrorMsg",""));
					throw new WhatsAppValidationException(errorList);
				}
				
				log.info("SAVE MOTOR INSERT BLOCK START : "+new Date());

				Map<String,Object> sectionMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_SECTION",req.getSectionId());
				String sectionId =sectionMap.get("CODE")==null?"":sectionMap.get("CODE").toString();
				Map<String,Object> usageMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_USAGE",req.getMotorUsageId());
				String motorUsage =usageMap.get("CODE")==null?"":usageMap.get("CODE").toString();
				
				
				String saveMotorApi=this.saveMotorApi;
				Map<String,Object> motorMap =new HashMap<>();
				motorMap.put("BrokerBranchCode", "1");// login
				motorMap.put("AcExecutiveId", ""); 
				motorMap.put("CommissionType", ""); 
				motorMap.put("CustomerCode", "620499");// login
				motorMap.put("CustomerName", req.getCustomerName()); //login
				motorMap.put("BdmCode", "620499");  //login
				motorMap.put("BrokerCode", "10303"); //login
				motorMap.put("LoginId", "guest"); //login
				motorMap.put("SubUserType", "B2C Broker"); //login
				motorMap.put("ApplicationId", "1");
				motorMap.put("CustomerReferenceNo",customerRefNo);
				motorMap.put("RequestReferenceNo", "");
				motorMap.put("Idnumber", req.getIdNumber());
				motorMap.put("VehicleId", "1");
				motorMap.put("AcccessoriesSumInsured", "0");
				motorMap.put("AccessoriesInformation", "");
				motorMap.put("AdditionalCircumstances", "");
				motorMap.put("AxelDistance", "");
				motorMap.put("Chassisnumber", tiraResult.get("Chassisnumber")==null?"": tiraResult.get("Chassisnumber"));
				motorMap.put("Color", tiraResult.get("Color")==null?"": tiraResult.get("Color"));
				motorMap.put("CityLimit", "");
				motorMap.put("CoverNoteNo", "");
				motorMap.put("OwnerCategory", tiraResult.get("OwnerCategory")==null?"": tiraResult.get("OwnerCategory"));
				motorMap.put("CubicCapacity", "");
				motorMap.put("CreatedBy", "guest");
				motorMap.put("DrivenByDesc", "D");
				motorMap.put("EngineNumber", tiraResult.get("EngineNumber")==null?"": tiraResult.get("EngineNumber"));
				motorMap.put("FuelType", tiraResult.get("FuelType")==null?"": tiraResult.get("FuelType"));
				motorMap.put("Gpstrackinginstalled", "N");
				motorMap.put("Grossweight", tiraResult.get("Grossweight")==null?"": tiraResult.get("Grossweight"));
				motorMap.put("HoldInsurancePolicy", "N"); 
				motorMap.put("Insurancetype", sectionId); //dub
				motorMap.put("InsuranceId", "100002");
				motorMap.put("InsuranceClass", req.getTypeofInsurance());//req.getPolicyType());
				motorMap.put("InsurerSettlement", "");
				motorMap.put("InterestedCompanyDetails", "");
				motorMap.put("ManufactureYear", tiraResult.get("ManufactureYear")==null?"": tiraResult.get("ManufactureYear"));
				motorMap.put("ModelNumber", "");
				motorMap.put("MotorCategory", tiraResult.get("ReqMotorCategory")==null?"": tiraResult.get("ReqMotorCategory"));
				motorMap.put("Motorusage", motorUsage); //doubt
				motorMap.put("NcdYn", req.getClaimType().equalsIgnoreCase("yes")?"Y":"N");
				motorMap.put("NoOfClaims", "");
				motorMap.put("NumberOfAxels", tiraResult.get("NumberOfAxels")==null?"": tiraResult.get("NumberOfAxels"));
				motorMap.put("BranchCode", "02"); //login
				motorMap.put("AgencyCode", "10303");//ogin
				motorMap.put("ProductId", "5");
				motorMap.put("SectionId", sectionId);//Insurancetype as same
				motorMap.put("PolicyType", req.getTypeofInsurance());//req.getPolicyType());// policy yeare same as
				motorMap.put("RadioOrCasseteplayer", "");
				motorMap.put("RegistrationYear", "99999");
				motorMap.put("Registrationnumber", req.getRegisrationNo());
				motorMap.put("RoofRack", "");
				motorMap.put("SeatingCapacity", "");
				motorMap.put("SourceType", "B2C Broker");
				motorMap.put("SpotFogLamp", "");
				motorMap.put("Stickerno", "");
				motorMap.put("SumInsured", StringUtils.isBlank(req.getSumInsured())?"0":req.getSumInsured());
				motorMap.put("Tareweight", tiraResult.get("Tareweight")==null?"": tiraResult.get("Tareweight"));
				motorMap.put("TppdFreeLimit", "");
				motorMap.put("TppdIncreaeLimit", "0");
				motorMap.put("TrailerDetails", "");
				motorMap.put("Vehcilemodel", tiraResult.get("Vehcilemodel")==null?"": tiraResult.get("Vehcilemodel"));
				motorMap.put("VehicleType", bodyType);//tiraResult.get("VehicleType")==null?"": tiraResult.get("VehicleType"));
				motorMap.put("Vehiclemake", tiraResult.get("Vehiclemake")==null?"": tiraResult.get("Vehiclemake"));
				motorMap.put("WindScreenSumInsured", "0");
				motorMap.put("Windscreencoverrequired", "");
				motorMap.put("accident", "");
				motorMap.put("periodOfInsurance", "365");
				motorMap.put("accident", "");
				motorMap.put("periodOfInsurance", "");
				motorMap.put("PolicyStartDate", policyStartDate);
				motorMap.put("PolicyEndDate", policyEndDate);
				motorMap.put("Currency", "TZS");
				motorMap.put("ExchangeRate", "1.0");
				motorMap.put("HavePromoCode", "N");
				motorMap.put("CollateralYn", "");
				motorMap.put("BorrowerType", "");
				motorMap.put("CollateralName", "");
				motorMap.put("FirstLossPayee", "");
				motorMap.put("FleetOwnerYn", "N");
				motorMap.put("NoOfVehicles", "0");
				motorMap.put("NoOfComprehensives", "");
				motorMap.put("ClaimRatio", "");
				motorMap.put("SavedFrom", "Customer");
				motorMap.put("UserType", "Broker");
				motorMap.put("TiraCoverNoteNo", "");
				motorMap.put("EndorsementYn", "N");
				motorMap.put("EndorsementDate", "");
				motorMap.put("EndorsementEffectiveDate", "");
				motorMap.put("EndorsementRemarks", "");
				motorMap.put("EndorsementType", "");
				motorMap.put("EndorsementTypeDesc", "");
				motorMap.put("EndtCategoryDesc", "");
				motorMap.put("EndtCount", "");
				motorMap.put("EndtPrevPolicyNo", "");
				motorMap.put("EndtStatus", "");
				motorMap.put("IsFinanceEndt", "");
				motorMap.put("OrginalPolicyNo", "");
				motorMap.put("Status", "Y");
	
				Map<String,String> exchangeRateScenarioReq =new HashMap<>();
				exchangeRateScenarioReq.put("OldAcccessoriesSumInsured", "");
				exchangeRateScenarioReq.put("OldCurrency", "TZS");
				exchangeRateScenarioReq.put("OldExchangeRate", "1.0");
				exchangeRateScenarioReq.put("OldSumInsured", "");
				exchangeRateScenarioReq.put("OldTppdIncreaeLimit", "");
				exchangeRateScenarioReq.put("OldWindScreenSumInsured", "");
				Map<String,Object> exchangeRateScenario =new HashMap<>();
				exchangeRateScenario.put("ExchangeRateScenario", exchangeRateScenarioReq);
				motorMap.put("Scenarios", exchangeRateScenario);
				Map<String,Object> motorResult=null;
				List<Map<String,Object>> errors=null;
				try {
					String motorReq =mapper.writeValueAsString(motorMap);
					System.out.println(motorReq);
					response =serviceImpl.callEwayApi(saveMotorApi, motorReq);
					System.out.println(response);
					
					Map<String,Object> motorRes =mapper.readValue(response, Map.class);
					errors =motorRes.get("ErrorMessage")==null?null:
						mapper.readValue(mapper.writeValueAsString(motorRes.get("ErrorMessage")), List.class);
					if(errors.isEmpty())
						motorResult =motorRes.get("Result")==null?null:
							mapper.readValue(mapper.writeValueAsString(motorRes.get("Result")), Map.class);
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				String errorText="";
				if(!errors.isEmpty()) {
					 errorText =errors.stream().map(p ->p.get("Message").toString())
							.collect(Collectors.joining("||"));
				}
				
				if(StringUtils.isNotBlank(errorText)){
					errorList.add(new Error("*"+errorText+"*","ErrorMsg","500"));
				}
				
				if(errorList.size()>0) {
					throw new WhatsAppValidationException(errorList);

				}
				 
				log.info("SAVE MOTOR INSERT BLOCK END : "+new Date());

				String coverId ="";
				if(motorResult!=null) {
					
					log.info("CALCULATOR BLOCK START : "+new Date());
					
					Map<String,Object> calcMap = new HashMap<>();
					calcMap.put("InsuranceId", motorResult.get("InsuranceId")==null?"":motorResult.get("InsuranceId"));
					calcMap.put("BranchCode", "02");
					calcMap.put("AgencyCode", "10303");
					calcMap.put("SectionId", motorResult.get("SectionId")==null?"":motorResult.get("SectionId"));
					calcMap.put("ProductId", motorResult.get("ProductId")==null?"":motorResult.get("ProductId"));
					calcMap.put("MSRefNo", motorResult.get("MSRefNo")==null?"":motorResult.get("MSRefNo"));
					calcMap.put("VehicleId", motorResult.get("VehicleId")==null?"":motorResult.get("VehicleId"));
					calcMap.put("CdRefNo", motorResult.get("CdRefNo")==null?"":motorResult.get("CdRefNo"));
					calcMap.put("VdRefNo", motorResult.get("VdRefNo")==null?"":motorResult.get("VdRefNo"));
					calcMap.put("CreatedBy", motorResult.get("CreatedBy")==null?"":motorResult.get("CreatedBy"));
					calcMap.put("productId", motorResult.get("ProductId")==null?"":motorResult.get("ProductId"));
					calcMap.put("sectionId", motorResult.get("SectionId")==null?"":motorResult.get("SectionId"));
					calcMap.put("RequestReferenceNo", motorResult.get("RequestReferenceNo")==null?"":motorResult.get("RequestReferenceNo"));
					calcMap.put("EffectiveDate", policyStartDate);
					calcMap.put("PolicyEndDate", policyEndDate);
					calcMap.put("CoverModification", "N");
					
					String calcApi =this.calcApi;
					List<Map<String,Object>> coverList =null;
					Map<String,Object> calcRes=null;
					String refNo="";
					try {
						String calcReq =mapper.writeValueAsString(calcMap);
						log.info("CALC Request || "+calcRes);
						response =serviceImpl.callEwayApi(calcApi, calcReq);
						calcRes =mapper.readValue(response, Map.class);
						coverList=calcRes.get("CoverList")==null?null:
							mapper.readValue(mapper.writeValueAsString(calcRes.get("CoverList")), List.class);
						log.info("CALC Response || "+calcRes);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					Boolean bCover=coverList.stream().anyMatch(p ->p.get("CoverageType").toString().equalsIgnoreCase("B"));
					
					if(!coverList.isEmpty() && bCover) {
						Map<String,Object> viewCalcMap =new HashMap<String,Object>();
						viewCalcMap.put("ProductId", calcRes.get("ProductId")==null?"":calcRes.get("ProductId").toString());
						viewCalcMap.put("RequestReferenceNo", calcRes.get("RequestReferenceNo")==null?"":calcRes.get("RequestReferenceNo").toString());
						List<Map<String,Object>> view=null;
						Long premium=0L;
						Long vatTax =0L;
						Double vatPercentage=0D;
						List<Map<String,Object>> coverList3=null;
						try {
							String viewCalcReq =mapper.writeValueAsString(viewCalcMap);
							String viewCalc=this.viewCalcApi;
							response =serviceImpl.callEwayApi(viewCalc, viewCalcReq);
							System.out.println("PREMIUM RESPONSE ===>   "+response);
							Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
							 view =viewCalcRes.get("Result")==null?null:
								mapper.readValue(mapper.writeValueAsString(viewCalcRes.get("Result")), List.class);
							 
							 coverList3= view.get(0).get("CoverList")==null?null:
								 mapper.readValue(mapper.writeValueAsString( view.get(0).get("CoverList")), List.class);
						}catch (Exception e) {
							e.printStackTrace();
						}
							refNo=view.get(0).get("RequestReferenceNo")==null?"":view.get(0).get("RequestReferenceNo").toString();
									
							String referalRemarks =coverList3.stream()
									.filter(p -> p.get("CoverageType").equals("B"))
									.map(p ->p.get("ReferalDescription")==null?"":p.get("ReferalDescription").toString())
									.collect(Collectors.joining());
							System.out.println(referalRemarks);
						
							if(StringUtils.isNotBlank(referalRemarks))	{
								
								errorList.add(new Error("*QUOTATION HAS BEEN REFERRAL ("+refNo+") || CONTACT ADMIN..!*", "ErrorMsg", "101"));
							}
							
							if(errorList.size()>0) {
								throw new WhatsAppValidationException(errorList);

							}
							
							Map<String,Object> cover_list=coverList3.stream().filter(p ->p.get("CoverageType").equals("B"))
							.map(p ->p).findFirst().orElse(null);
							
							coverId=cover_list.get("CoverId")==null?"":cover_list.get("CoverId").toString();
							
							Map<String,Object> tax =null;
							try {
							List<Map<String,Object>> taxList =cover_list.get("Taxes")==null?null: 
											mapper.readValue(mapper.writeValueAsString(cover_list.get("Taxes")), List.class);
							tax=taxList.stream().filter(p ->p.get("TaxId").equals("1")).findFirst().orElse(null);
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
							
						premium =cover_list.get("PremiumExcluedTax")==null?0L:Double.valueOf(cover_list.get("PremiumExcluedTax").toString()).longValue();
						vatTax =tax.get("TaxAmount")==null?0L:Double.valueOf(tax.get("TaxAmount").toString()).longValue();
						vatPercentage =tax.get("TaxRate")==null?0L:Double.valueOf(tax.get("TaxRate").toString());
						coverId=cover_list.get("CoverId")==null?"5":cover_list.get("CoverId").toString();
							
						
						Long totalPremium =premium+vatTax;
						
						log.info("CALCULATOR BLOCK END : "+new Date());
		
						//==============================QUOTATION BLOCK END=============================================

									
						//==============================BUYPOLICY BLOCK START=============================================

						
						// user creation block
						
						log.info("USER CREATION BLOCK START : "+new Date());
												
						Map<String,Object> userCreateMap =new HashMap<>();
						userCreateMap.put("CompanyId", "100002");
						userCreateMap.put("CustomerId", customerRefNo);
						userCreateMap.put("ProductId", "5");
						userCreateMap.put("ReferenceNo", refNo);
						userCreateMap.put("UserMobileNo", req.getWhatsAppNo());
						userCreateMap.put("UserMobileCode", req.getWhatsAppCode());
						userCreateMap.put("AgencyCode", "10303");
						
						String userCreationReq =objectPrint.toJson(userCreateMap);
						
						log.info("User Creation Request || "+userCreationReq);
						response =serviceImpl.callEwayApi(this.ewayLoginCreateApi, userCreationReq);
						log.info("User Creation Response || "+response);

						log.info("USER CREATION BLOCK END : "+new Date());

						String exception="";
						
						log.info("BUYPOLICY  BLOCK START : "+new Date());
						
						// buypolicy block 
						Map<String,Object> coversMap =new HashMap<String,Object>();
						coversMap.put("CoverId", coverId);
						coversMap.put("SubCoverId", "");
						coversMap.put("SubCoverYn", "N");
						List<Map<String,Object>> coversMapList =new ArrayList<Map<String,Object>>();
						coversMapList.add(coversMap);
						Map<String,Object> vehicleMap =new HashMap<String,Object>();
						vehicleMap.put("SectionId", sectionId);
						vehicleMap.put("Id", "1");
						vehicleMap.put("Covers", coversMapList);
						List<Map<String,Object>> vehiMapList =new ArrayList<Map<String,Object>>();
						vehiMapList.add(vehicleMap);
						Map<String,Object> buypolicyMap =new HashMap<String,Object>();
						buypolicyMap.put("RequestReferenceNo", refNo);
						buypolicyMap.put("CreatedBy", "guest");
						buypolicyMap.put("ProductId", "5");
						buypolicyMap.put("ManualReferralYn", "N");
						buypolicyMap.put("Vehicles", vehiMapList);

						String buypolicyReq =objectPrint.toJson(buypolicyMap);
						
						System.out.println("buypolicyReq" +buypolicyReq);
						response =serviceImpl.callEwayApi(buyploicyApi, buypolicyReq);
						System.out.println("buypolicyRes" +response);

						Map<String,Object> buyPolicyResult =null;
							try {	
								Map<String,Object>	buyPolicyRes =mapper.readValue(response, Map.class);
								buyPolicyResult =buyPolicyRes.get("Result")==null?null:
									mapper.readValue(mapper.writeValueAsString(buyPolicyRes.get("Result")), Map.class);
							}catch (Exception e) {
								e.printStackTrace();
								exception=e.getMessage();
							}
							

							if(StringUtils.isNotBlank(exception)) {
								errorList.add(new Error(exception, "ErrorMsg", "101"));
							}
							
							if(errorList.size()>0) {
								throw new WhatsAppValidationException(errorList);

							}
						
							log.info("BUYPOLICY  BLOCK END : "+new Date());
							
							log.info("MAKE PAYMENT BLOCK START : "+new Date());
							
							// make payment
							Map<String,Object> makePaymentMap = new HashMap<String,Object>();
							makePaymentMap.put("CreatedBy", "guest");
							makePaymentMap.put("EmiYn", "N");
							makePaymentMap.put("InstallmentMonth", "");
							makePaymentMap.put("InstallmentPeriod", "");
							makePaymentMap.put("InsuranceId", "100002");
							makePaymentMap.put("Premium", totalPremium);
							makePaymentMap.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
							makePaymentMap.put("Remarks", "None");
							makePaymentMap.put("SubUserType", "B2C");
							makePaymentMap.put("UserType", "Broker");
								
							String makePaymentReq =objectPrint.toJson(makePaymentMap);
							
							System.out.println("makePaymentReq" +makePaymentReq);

							response =serviceImpl.callEwayApi(makePaymentApi, makePaymentReq);
							System.out.println("makePaymentRes" +response);

							Map<String,Object> makePaymentResult =null;
								try {	
									Map<String,Object>	makePaymentRes =mapper.readValue(response, Map.class);
									makePaymentResult =makePaymentRes.get("Result")==null?null:
										mapper.readValue(mapper.writeValueAsString(makePaymentRes.get("Result")), Map.class);
								}catch (Exception e) {
									e.printStackTrace();
									exception=e.getMessage();
								}
							
								
								if(StringUtils.isNotBlank(exception)) {
									errorList.add(new Error(exception, "ErrorMsg", "101"));
								}
								
								if(errorList.size()>0) {
									throw new WhatsAppValidationException(errorList);

								}
								
								// insert payment 
								
								Map<String,Object> insertPayment =new HashMap<String,Object>();
								insertPayment.put("CreatedBy", "guest");
								insertPayment.put("InsuranceId", "100002");
								insertPayment.put("EmiYn", "N");
								insertPayment.put("Premium", totalPremium);
								insertPayment.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
								insertPayment.put("Remarks", "None");
								insertPayment.put("PayeeName", req.getCustomerName());
								insertPayment.put("SubUserType", "B2C");
								insertPayment.put("UserType", "Broker");
								insertPayment.put("PaymentId", makePaymentResult.get("PaymentId"));
								insertPayment.put("PaymentType", "4");
								
								String insertPaymentReq =objectPrint.toJson(insertPayment);
								
								System.out.println("insertPaymentReq" +insertPaymentReq);

								response =serviceImpl.callEwayApi(insertPaymentApi, insertPaymentReq);
								
								System.out.println("insertPaymentRes" +response);
								
								Map<String,Object> insertPaymentResult =null;
								try {	
									Map<String,Object>	insertPaymentRes =mapper.readValue(response, Map.class);
									insertPaymentResult =insertPaymentRes.get("Result")==null?null:
										mapper.readValue(mapper.writeValueAsString(insertPaymentRes.get("Result")), Map.class);
								}catch (Exception e) {
									e.printStackTrace();
									exception=e.getMessage();
								}
								
								
								if(StringUtils.isNotBlank(exception)) {
									errorList.add(new Error(exception, "ErrorMsg", "101"));
								}
								
								if(errorList.size()>0) {
									throw new WhatsAppValidationException(errorList);

								}
							
							String merchantRefNo =insertPaymentResult.get("MerchantReference")==null?"":
									insertPaymentResult.get("MerchantReference").toString();
							
							String quoteNo =insertPaymentResult.get("QuoteNo")==null?"":
								insertPaymentResult.get("QuoteNo").toString();
							
							log.info("RequestRefNo : "+refNo+" ||  MerchantReference : "+merchantRefNo+" || QuoteNo : "+quoteNo+" ");
					
							
							log.info("MAKE PAYMENT BLOCK END : "+new Date());
							
							Map<String,String> paymentMap =new HashMap<>();
							paymentMap.put("MerchantRefNo", merchantRefNo);
							paymentMap.put("CompanyId", "100002");
							paymentMap.put("WhatsappCode", req.getWhatsAppCode());
							paymentMap.put("WhtsappNo", req.getWhatsAppNo());
							paymentMap.put("QuoteNo", quoteNo);
							
							String payJson =objectPrint.toJson(paymentMap);
							
							String encodeReq =Base64.getEncoder().encodeToString(payJson.getBytes());
							
							String paymnetUrl =ewayMotorPaymentLink+encodeReq;
							
							System.out.println("PAYMENT LINK :" +paymnetUrl);
							
						//==============================BUYPOLICY BLOCK END=============================================
							
						//whatsapp BOT Response		
						Map<String,String> map =new HashMap<>();
						map.put("referenceno", view.get(0).get("RequestReferenceNo")==null?"":view.get(0).get("RequestReferenceNo").toString());
						map.put("inceptiondate", policyStartDate);
						map.put("expirydate", policyEndDate);
						map.put("link", paymnetUrl);
						map.put("registration", tiraResult.get("Registrationnumber")==null?"":tiraResult.get("Registrationnumber").toString());
						map.put("chassis", tiraResult.get("Chassisnumber")==null?"":tiraResult.get("Chassisnumber").toString());
						map.put("suminsured", StringUtils.isBlank(req.getSumInsured()) ?"N/A":req.getSumInsured());
						map.put("usage",tiraResult.get("Motorusage")==null?"":tiraResult.get("Motorusage").toString());
						map.put("vehtype", tiraResult.get("VehicleType")==null?"":tiraResult.get("VehicleType").toString());
						map.put("color",tiraResult.get("Color")==null?"":tiraResult.get("Color").toString());
						map.put("premium", premium.toString());
						map.put("vatamt", vatTax.toString());
						map.put("totalpremium", totalPremium.toString());
						map.put("vat", String.valueOf(vatPercentage.longValue()));
						return map;
						
					}else {
						errorList.add(new Error("*SERVICE IS NOT RETURNED PREMIUM && COVERS ("+refNo+") || CONTACT ADMIN..!*", "ErrorMsg", "101"));
					}
					
				}
						
			
			if(errorList.size()>0) {
				throw new WhatsAppValidationException(errorList);

			}
			
		return null;
	}

	
	public String getTinyUrl(String RequestUrl) {
		String response ="";
		try {
	        RestTemplate restTemplate = new RestTemplate();
	        String apiUrl = turlApi;
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        String requestBody = "{\"RequestUrl\": \""+RequestUrl+"\"}"; // Example JSON request body
	        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
	        ResponseEntity<Object> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Object.class);
	        Object responseBody = responseEntity.getBody();
	        System.out.println("Response from API: " + responseBody);
			log.info("Encrypted URL result: " + response + " Encrypted URL " );
			Map<String,Object> object =(Map<String,Object>) responseBody;
			response =object.get("ShortUrl")==null?"":object.get("ShortUrl").toString();
			log.info("Encrypted URL result: " + response + " Encrypted URL " + response);
			return response;
		} catch (Exception e) {
			log.error(e);
		}
		return "";
		}
	
	private Map<String, String> findPolicyDates(Map<String, Object> tiraResult) {
		Map<String, String> response = new HashMap<>();
		String tiraPolicyEndDate =tiraResult.get("PolicyEndDate")==null?new SimpleDateFormat("dd/MM/yyyy").format(new Date()): (String)tiraResult.get("PolicyEndDate");
		
		LocalDate previousPolicyEndDate =LocalDate.parse(tiraPolicyEndDate.trim(),DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		LocalDate currentDate =LocalDate.now();
		
		LocalDate policyStartDate =null;
		
		if(previousPolicyEndDate.isBefore(currentDate)) {
			policyStartDate=LocalDate.now();

		}else if(previousPolicyEndDate.isEqual(currentDate)) {
			policyStartDate=LocalDate.now().plusDays(1);

		}else if(previousPolicyEndDate.isAfter(currentDate)) {
			policyStartDate=previousPolicyEndDate.plusDays(1);
		}
		LocalDate policyEndDate =policyStartDate.plusDays(364);
		DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		response.put("PolicyStartDate", formatters.format(policyStartDate));
		response.put("PolicyEndDate", formatters.format(policyEndDate));
		
		return response;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Object> checkRegistrationWithTira(String registerNo) {
		Map<String,Object> tiraResult =null;
		try {
			Map<String,String> tiraMap = new HashMap<String,String>();
			tiraMap.put("ReqChassisNumber", "");
			tiraMap.put("ReqRegNumber", registerNo);
			tiraMap.put("InsuranceId", "100002");
			tiraMap.put("BranchCode", "02");
			tiraMap.put("BrokerBranchCode", "01");
			tiraMap.put("ProductId", "5");
			tiraMap.put("CreatedBy", "guest");
			tiraMap.put("SavedFrom", "API");
			

			
			String tiraReq =mapper.writeValueAsString(tiraMap);
			String tiraApi =this.tiraApi;
				
			String response= serviceImpl.callEwayApi(tiraApi,tiraReq);
			Map<String,Object> strToMap =mapper.readValue(response, Map.class);
			tiraResult =strToMap.get("Result")==null?null:
			mapper.readValue(mapper.writeValueAsString(strToMap.get("Result")), Map.class);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return tiraResult;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getMotorSectionUsage(MotorSectionImageReq req) {
		String response ="";
		String returnRes ="";
		try {
			if("MOTOR_SECTION".equalsIgnoreCase(req.getType())) {
				Map<String,Object> sectionMap = new HashMap<>();
				sectionMap.put("ProductId", 5);
				sectionMap.put("InsuranceId", 100002);
				sectionMap.put("BranchCode", 01);
				String motorReq =objectPrint.toJson(sectionMap); 
				response=serviceImpl.callEwayApi(sectionApi, motorReq);
				Map<String,Object> sectionRes =mapper.readValue(response, Map.class);
				List<Map<String,Object>> result = sectionRes.get("Result")==null?null:
					(List<Map<String,Object>>)sectionRes.get("Result");
				if(result!=null) {
					
					Long serialNo =claimIntimationRepository.getSerialNo();
					Integer botOptionNo =1;
					List<ClaimIntimationEntity> intimationEntities =new ArrayList<>();
					for(Map<String,Object> map :result) {
						String code =map.get("Code")==null?null:map.get("Code").toString();
						String codeDesc =map.get("CodeDesc")==null?"":map.get("CodeDesc").toString();
						ClaimIntimationEntity intimationEntity =ClaimIntimationEntity.builder()
								.serialNo(serialNo)
								.botOptionNo(String.valueOf(botOptionNo))
								.mobileNo(req.getMobileNo())
								.code(code)
								.apiType("MOTOR_SECTION")
								.codeDesc(codeDesc)
								.status("Y")
								.entryDate(new Date())
								.build();
						intimationEntities.add(intimationEntity);
						botOptionNo++;
					}
					
				List<ClaimIntimationEntity> savedData =claimIntimationRepository.saveAll(intimationEntities);
				returnRes = savedData.stream().map(p ->{
					String optionNo =p.getBotOptionNo();
					String codeDesc =p.getCodeDesc();
					return "*Choose "+optionNo+"* : "+codeDesc+"";
				}).collect(Collectors.joining("\n"));	
				 
				}else {
					returnRes ="Something went wrong in this service "+sectionApi+"\nContact Admin...!";
				}
			}else if("MOTOR_USAGE".equalsIgnoreCase(req.getType())) {
				Map<String,Object> sectionMap = new HashMap<>();
				Map<String,Object>  resultMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_SECTION",req.getSectionId());
				sectionMap.put("SectionId", resultMap.get("CODE")==null?"":resultMap.get("CODE"));
				sectionMap.put("InsuranceId", 100002);
				sectionMap.put("BranchCode", 01);
				String motorReq =objectPrint.toJson(sectionMap); 
				response=serviceImpl.callEwayApi(usageApi, motorReq);
				Map<String,Object> sectionRes =mapper.readValue(response, Map.class);
				List<Map<String,Object>> result = sectionRes.get("Result")==null?null:
					(List<Map<String,Object>>)sectionRes.get("Result");
				if(result!=null) {
					
					Long serialNo =claimIntimationRepository.getSerialNo();
					Integer botOptionNo =1;
					List<ClaimIntimationEntity> intimationEntities =new ArrayList<>();
					for(Map<String,Object> map :result) {
						String code =map.get("Code")==null?null:map.get("Code").toString();
						String codeDesc =map.get("CodeDesc")==null?"":map.get("CodeDesc").toString();
						ClaimIntimationEntity intimationEntity =ClaimIntimationEntity.builder()
								.serialNo(serialNo)
								.botOptionNo(String.valueOf(botOptionNo))
								.mobileNo(req.getMobileNo())
								.code(code)
								.apiType("MOTOR_USAGE")
								.codeDesc(codeDesc)
								.status("Y")
								.entryDate(new Date())
								.build();
						intimationEntities.add(intimationEntity);
						botOptionNo++;
					}
					
				List<ClaimIntimationEntity> savedData =claimIntimationRepository.saveAll(intimationEntities);
				returnRes = savedData.stream().map(p ->{
					String optionNo =p.getBotOptionNo();
					String codeDesc =p.getCodeDesc();
					return "*Choose "+optionNo+"* : "+codeDesc+"";
				}).collect(Collectors.joining("\n"));	
				 
				}else {
					returnRes ="Something went wrong in this service "+usageApi+"\nContact Admin...!";
				}
			}
			
			Map<String,String> returnMap = new HashMap<>();
			returnMap.put("Response", returnRes);
			return returnMap;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String buypolicy(String request) throws WhatsAppValidationException {
		List<Error> errorList = new ArrayList<>(2);
		String exception ="",response="";
		// getting request
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
				
		String selcomPaymentReqApi =this.ewayMotorSelcomPaymentApi+merchantRefNo;
				
		log.info("selcomPaymentReqApi || " +selcomPaymentReqApi);

		Map<String,String> selMap =new HashMap<>();
		selMap.put("InsuranceId", CompanyId);
		String selcomReq =objectPrint.toJson(selMap);
		response =serviceImpl.callEwayApi(selcomPaymentReqApi, selcomReq);
				
		log.info("selcomPaymentReqApiRes" +response);
				
		String url =ewaySelcomPaymentCheckApi+quoteNo;
				
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
				
		Thread_User_Creation user_Creation = new Thread_User_Creation( request, url,
				serviceImpl,  "PAYMENT_TRIGGER", merchantRefNo,documentInfoMap);
				user_Creation.setName("PAYMENT_TRIGGER");
				user_Creation.start();
			
				return redirectUrl;
	}

	@Override
	public Object b2cGenerateQuote(B2CQuoteRequest req) throws WhatsAppValidationException {
		 List<Error> errorList = new ArrayList<>();
			String response ="";
			String companyId ="100002";
			
			log.info("b2cGenerateQuote request "+objectPrint.toJson(req));
			
			Map<String,String> vehiMap =Stream.of(req.getVehicleForm().split(","))
			.map( p-> p.split(":"))
			.collect(Collectors.toMap(t ->(t[0]).trim(), t ->(t[1]).trim()));
			
			log.info("b2cGenerateQuote map object "+objectPrint.toJson(vehiMap));
			
			String registrationNo =StringUtils.isBlank(vehiMap.get("Registration No"))?"":vehiMap.get("Registration No");
			String policyType =StringUtils.isBlank(vehiMap.get("Policy Type"))?"":vehiMap.get("Policy Type");
			String vehicleType =StringUtils.isBlank(vehiMap.get("Vehicle Type"))?"":vehiMap.get("Vehicle Type");
			String usageOfVehicle =StringUtils.isBlank(vehiMap.get("Usage Of Vehicle"))?"":vehiMap.get("Usage Of Vehicle");
			String idType =StringUtils.isBlank(vehiMap.get("Id Type"))?"":vehiMap.get("Id Type");
			String idNumber =StringUtils.isBlank(vehiMap.get("Id Number"))?"":vehiMap.get("Id Number");
			String customerName =StringUtils.isBlank(vehiMap.get("Customer Name"))?"":vehiMap.get("Customer Name");
			String claimYn =StringUtils.isBlank(vehiMap.get("Claim Yn"))?"":vehiMap.get("Claim Yn");
			
			
			String policyTypeId =claimIntimationRepository.getPolicyTypeId(companyId, "5", policyType);
			String vehicleTypeId =claimIntimationRepository.getBodyTypeId(companyId, vehicleType);
			String usageOfVehicleId =claimIntimationRepository.getVehicleUsageId(companyId, usageOfVehicle);
			
			
			if(StringUtils.isBlank(policyTypeId) ||  StringUtils.isBlank(vehicleTypeId) || StringUtils.isBlank(usageOfVehicleId)) {
				errorList.add(new Error("POLICY_TYPE_ID  || BODY_TYPEID || VEHICLE_USAGE_ID || NOT FOUND..!","ErrorMsg","500"));
			}
			
			if(errorList.size()>0) {
				throw new WhatsAppValidationException(errorList);
			}
			
			
			//==============================TIRA CHECK BLOCK START=============================================
			
			log.info("TIRA BLOCK START TIME : "+new Date());
			
			Map<String,String> tiraMap = new HashMap<String,String>();
			tiraMap.put("ReqChassisNumber", "");
			tiraMap.put("ReqRegNumber", registrationNo);
			tiraMap.put("InsuranceId", companyId);
			tiraMap.put("BranchCode", "02");
			tiraMap.put("BrokerBranchCode", "01");
			tiraMap.put("ProductId", "5");
			tiraMap.put("CreatedBy", "guest");
			tiraMap.put("SavedFrom", "API");
			Map<String,Object> policyHolder =null;
			Map<String,Object> tiraResult =null;

			try {
				String tiraReq =mapper.writeValueAsString(tiraMap);
				String tiraApi =this.tiraApi;
				
				response= serviceImpl.callEwayApi(tiraApi,tiraReq);
				Map<String,Object> strToMap =mapper.readValue(response, Map.class);
				tiraResult =strToMap.get("Result")==null?null:
					mapper.readValue(mapper.writeValueAsString(strToMap.get("Result")), Map.class);
				
				if(tiraResult!=null) {
					 policyHolder =tiraResult.get("PolicyHolderInfo")==null?null:
						mapper.readValue(mapper.writeValueAsString(tiraResult.get("PolicyHolderInfo")), Map.class);
				}
				
				if(policyHolder==null) {
					String policyHolderApi =this.policyHolderApi;
					response= serviceImpl.callEwayApi(policyHolderApi,tiraReq);
					Map<String,Object> strToMap1 =mapper.readValue(response, Map.class);
					policyHolder =strToMap1.get("Result")==null?null:
						mapper.readValue(mapper.writeValueAsString(strToMap1.get("Result")), Map.class);
				}
			
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			log.info("TIRA BLOCK END : "+new Date());

			//==============================TIRA CHECK BLOCK END=============================================

			
			//==============================CUSTOMER CREATION BLOCK START=============================================

			log.info("CUSTOMER INSERT BLOCK START : "+new Date());
			
			String airtelPayno =req.getAirtelPayNo().startsWith("0")?req.getAirtelPayNo().substring(1):req.getAirtelPayNo();
			
			Map<String,Object> customerMap= new HashMap<String,Object>();
			String customerRefNo="";
			if(policyHolder==null) {
				String api =this.customerApi;
				String id=idType;
				customerMap.put("InsuranceId", "100002");
				customerMap.put("BranchCode", "01");
				customerMap.put("ProductId", "5");
				customerMap.put("BrokerBranchCode", "1");
				customerMap.put("ClientName", customerName);
				customerMap.put("CreatedBy", "guest");
				customerMap.put("BusinessType", "1");
				customerMap.put("IdNumber", idNumber);
				customerMap.put("Clientstatus", "Y");
				customerMap.put("IdType", "");
				customerMap.put("Title", "1");
				customerMap.put("SaveOrSubmit", "Save");
				customerMap.put("Status", "Y");
				customerMap.put("MobileNo1",airtelPayno);
				customerMap.put("MobileCode1", "255");
				customerMap.put("PolicyHolderType", "1");
				customerMap.put("Nationality", "TZA");
				customerMap.put("Gender", "M");
				
				customerMap.put("CityName", "Ilala");
				customerMap.put("CityCode", "11000");
				customerMap.put("RegionCode", "02");
				customerMap.put("StateCode", "10000");
				customerMap.put("StateName", "Dar es Salaam");
				customerMap.put("Street", "7th FLOOR,Exim Tower,Ghana Avenue");
				customerMap.put("Address1", "P.O.Box 9942,Dar es Salaam");
				customerMap.put("DobOrRegDate", "13/01/2004"); 	
				
				customerMap.put("IsTaxExempted", "N");
			
				customerMap.put("PolicyHolderTypeid", id);
				
				String customerReq =objectPrint.toJson(customerMap);
				response =serviceImpl.callEwayApi(api, customerReq);
			    Map<String,Object> customerRes = null;
			    Map<String,Object> result =null;
				try {
					customerRes=mapper.readValue(response, Map.class);
					result =mapper.readValue(mapper.writeValueAsString(customerRes.get("Result")) , Map.class);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				customerRefNo =result.get("SuccessId")==null?"":result.get("SuccessId").toString();
			}else {
				
				String api =this.customerApi;
				String id=idType;
				customerMap.put("InsuranceId", "100002");
				customerMap.put("BranchCode", "01");
				customerMap.put("ProductId", "5");
				customerMap.put("BrokerBranchCode", "1");
				customerMap.put("ClientName", customerName);
				customerMap.put("CreatedBy", "guest");
				customerMap.put("BusinessType", "1");
				customerMap.put("IdNumber", idNumber);
				customerMap.put("Clientstatus", "Y");
				customerMap.put("IdType", "");
				customerMap.put("Title", "1");
				customerMap.put("SaveOrSubmit", "Save");
				customerMap.put("Status", "Y");
				customerMap.put("MobileNo1",airtelPayno);
				customerMap.put("MobileCode1","255");
				
				customerMap.put("PolicyHolderType", "1");
				customerMap.put("CityName", policyHolder.get("Districtname")==null?"Ilala":policyHolder.get("Districtname").toString());
				customerMap.put("CityCode", policyHolder.get("Districtcode")==null?"11000":policyHolder.get("Districtcode").toString());
				customerMap.put("Nationality", "TZA");
				customerMap.put("Gender", policyHolder.get("Gender")==null?"":policyHolder.get("Gender").toString());
				customerMap.put("RegionCode",policyHolder.get("Regioncode")==null?"02":policyHolder.get("Regioncode").toString());
				customerMap.put("StateCode",policyHolder.get("Districtcode")==null?"10000":policyHolder.get("Districtcode").toString());
				customerMap.put("StateName",policyHolder.get("Regionname")==null?"Dar es Salaam":policyHolder.get("Regionname").toString());
				customerMap.put("Street", policyHolder.get("Regionname")==null?"7th FLOOR,Exim Tower,Ghana Avenue":policyHolder.get("Regionname").toString());
				customerMap.put("Address1",policyHolder.get("Districtname")==null?"P.O.Box 9942,Dar es Salaam":policyHolder.get("Districtname").toString());
				customerMap.put("IsTaxExempted", "N");
				
	
				customerMap.put("PolicyHolderTypeid", id);
				
				String customerReq =objectPrint.toJson(customerMap);
				response =serviceImpl.callEwayApi(api, customerReq);
			    Map<String,Object> customerRes = null;
			    Map<String,Object> result =null;
				try {
					customerRes=mapper.readValue(response, Map.class);
					result =mapper.readValue(mapper.writeValueAsString(customerRes.get("Result")) , Map.class);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				
				customerRefNo =result.get("SuccessId")==null?"":result.get("SuccessId").toString();
			}
			
			
			String customerCreation =objectPrint.toJson(customerMap);
			
			
			System.out.println("CustomerCreation + ||" +customerCreation);
			
			log.info("CUSTOMER INSERT BLOCK END : "+new Date());

			
			if(StringUtils.isBlank(customerRefNo) && policyHolder==null) {
				errorList.add(new Error("CUSTOMER CREATION FAILED || CONTACT ADMIN..!","ErrorMsg","500"));
			}
			
			if(errorList.size()>0) {
				throw new WhatsAppValidationException(errorList);
			}
				
			//==============================CUSTOMER CREATION BLOCK END=============================================

			
			//==============================QUOTATION BLOCK START=============================================

			    Map<String,String> policyDate=findPolicyDates(tiraResult);
				
				String policyStartDate =policyDate.get("PolicyStartDate");
				String policyEndDate =policyDate.get("PolicyEndDate");
				/*String bodyTypeDesc =tiraResult.get("VehicleType")==null?"":tiraResult.get("VehicleType").toString();
				Map<String,String> motorUsageMap =new HashMap<>();
				motorUsageMap.put("CompanyId", companyId);
				motorUsageMap.put("BodyType", bodyTypeDesc);
				motorUsageMap.put("MotorUsageName", "Private or Normal");
				
				String motorUsageApi =this.vehUpdateApi;
				Gson gson = new Gson();
				String motorUsageReq =gson.toJson(motorUsageMap);
				response =serviceImpl.callEwayApi(motorUsageApi, motorUsageReq);
			    Map<String,Object> motorUsageRes = null;
				try {
					motorUsageRes=mapper.readValue(response, Map.class);
				} catch (Exception e1) {
					e1.printStackTrace();
				} 
				System.out.println(motorUsageRes);
				*/
				String bodyType =vehicleTypeId;//motorUsageRes.get("BodyId")==null?"":motorUsageRes.get("BodyId").toString();
				
				if(StringUtils.isBlank(bodyType)) {
					errorList.add(new Error("BODYTYPE NOT FOUND || REGISTRATION NO : "+registrationNo+"|| BODY TYPE || CONTACT ADMIN..!","ErrorMsg",""));
					throw new WhatsAppValidationException(errorList);
				}
				
				log.info("SAVE MOTOR INSERT BLOCK START : "+new Date());

				//Map<String,Object> sectionMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_SECTION",req.getSectionId());
				String sectionId =policyTypeId;//sectionMap.get("CODE")==null?"":sectionMap.get("CODE").toString();
				//Map<String,Object> usageMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_USAGE",req.getMotorUsageId());
				String motorUsage =usageOfVehicleId;//usageMap.get("CODE")==null?"":usageMap.get("CODE").toString();
				
				
				String saveMotorApi=this.saveMotorApi;
				Map<String,Object> motorMap =new HashMap<>();
				motorMap.put("BrokerBranchCode", "1");// login
				motorMap.put("AcExecutiveId", ""); 
				motorMap.put("CommissionType", ""); 
				motorMap.put("CustomerCode", "620499");// login
				motorMap.put("CustomerName", customerName); //login
				motorMap.put("BdmCode", "620499");  //login
				motorMap.put("BrokerCode", "10303"); //login
				motorMap.put("LoginId", "guest"); //login
				motorMap.put("SubUserType", "B2C Broker"); //login
				motorMap.put("ApplicationId", "1");
				motorMap.put("CustomerReferenceNo",customerRefNo);
				motorMap.put("RequestReferenceNo", "");
				motorMap.put("Idnumber", idNumber);
				motorMap.put("VehicleId", "1");
				motorMap.put("AcccessoriesSumInsured", "0");
				motorMap.put("AccessoriesInformation", "");
				motorMap.put("AdditionalCircumstances", "");
				motorMap.put("AxelDistance", "");
				motorMap.put("Chassisnumber", tiraResult.get("Chassisnumber")==null?"": tiraResult.get("Chassisnumber"));
				motorMap.put("Color", tiraResult.get("Color")==null?"": tiraResult.get("Color"));
				motorMap.put("CityLimit", "");
				motorMap.put("CoverNoteNo", "");
				motorMap.put("OwnerCategory", tiraResult.get("OwnerCategory")==null?"": tiraResult.get("OwnerCategory"));
				motorMap.put("CubicCapacity", "");
				motorMap.put("CreatedBy", "guest");
				motorMap.put("DrivenByDesc", "D");
				motorMap.put("EngineNumber", tiraResult.get("EngineNumber")==null?"": tiraResult.get("EngineNumber"));
				motorMap.put("FuelType", tiraResult.get("FuelType")==null?"": tiraResult.get("FuelType"));
				motorMap.put("Gpstrackinginstalled", "N");
				motorMap.put("Grossweight", tiraResult.get("Grossweight")==null?"": tiraResult.get("Grossweight"));
				motorMap.put("HoldInsurancePolicy", "N"); 
				motorMap.put("Insurancetype", sectionId); //dub
				motorMap.put("InsuranceId", "100002");
				motorMap.put("InsuranceClass", req.getTypeofInsurance());//req.getPolicyType());
				motorMap.put("InsurerSettlement", "");
				motorMap.put("InterestedCompanyDetails", "");
				motorMap.put("ManufactureYear", tiraResult.get("ManufactureYear")==null?"": tiraResult.get("ManufactureYear"));
				motorMap.put("ModelNumber", "");
				motorMap.put("MotorCategory", tiraResult.get("ReqMotorCategory")==null?"": tiraResult.get("ReqMotorCategory"));
				motorMap.put("Motorusage", motorUsage); //doubt
				motorMap.put("NcdYn", claimYn.equalsIgnoreCase("Yes")?"Y":"N");
				motorMap.put("NoOfClaims", "");
				motorMap.put("NumberOfAxels", tiraResult.get("NumberOfAxels")==null?"": tiraResult.get("NumberOfAxels"));
				motorMap.put("BranchCode", "01"); //login
				motorMap.put("AgencyCode", "10303");//ogin
				motorMap.put("ProductId", "5");
				motorMap.put("SectionId", sectionId);//Insurancetype as same
				motorMap.put("PolicyType", req.getTypeofInsurance());//req.getPolicyType());// policy yeare same as
				motorMap.put("RadioOrCasseteplayer", "");
				motorMap.put("RegistrationYear", "99999");
				motorMap.put("Registrationnumber",registrationNo);
				motorMap.put("RoofRack", "");
				motorMap.put("SeatingCapacity", "");
				motorMap.put("SourceType", "B2C Broker");
				motorMap.put("SpotFogLamp", "");
				motorMap.put("Stickerno", "");
				motorMap.put("SumInsured", StringUtils.isBlank(req.getSumInsured())?"0":req.getSumInsured());
				motorMap.put("Tareweight", tiraResult.get("Tareweight")==null?"": tiraResult.get("Tareweight"));
				motorMap.put("TppdFreeLimit", "");
				motorMap.put("TppdIncreaeLimit", "0");
				motorMap.put("TrailerDetails", "");
				motorMap.put("Vehcilemodel", tiraResult.get("Vehcilemodel")==null?"": tiraResult.get("Vehcilemodel"));
				motorMap.put("VehicleType", bodyType);//tiraResult.get("VehicleType")==null?"": tiraResult.get("VehicleType"));
				motorMap.put("Vehiclemake", tiraResult.get("Vehiclemake")==null?"": tiraResult.get("Vehiclemake"));
				motorMap.put("WindScreenSumInsured", "0");
				motorMap.put("Windscreencoverrequired", "");
				motorMap.put("accident", "");
				motorMap.put("periodOfInsurance", "365");
				motorMap.put("accident", "");
				motorMap.put("periodOfInsurance", "");
				motorMap.put("PolicyStartDate", policyStartDate);
				motorMap.put("PolicyEndDate", policyEndDate);
				motorMap.put("Currency", "TZS");
				motorMap.put("ExchangeRate", "1.0");
				motorMap.put("HavePromoCode", "N");
				motorMap.put("CollateralYn", "");
				motorMap.put("BorrowerType", "");
				motorMap.put("CollateralName", "");
				motorMap.put("FirstLossPayee", "");
				motorMap.put("FleetOwnerYn", "N");
				motorMap.put("NoOfVehicles", "0");
				motorMap.put("NoOfComprehensives", "");
				motorMap.put("ClaimRatio", "");
				motorMap.put("SavedFrom", "Customer");
				motorMap.put("UserType", "Broker");
				motorMap.put("TiraCoverNoteNo", "");
				motorMap.put("EndorsementYn", "N");
				motorMap.put("EndorsementDate", "");
				motorMap.put("EndorsementEffectiveDate", "");
				motorMap.put("EndorsementRemarks", "");
				motorMap.put("EndorsementType", "");
				motorMap.put("EndorsementTypeDesc", "");
				motorMap.put("EndtCategoryDesc", "");
				motorMap.put("EndtCount", "");
				motorMap.put("EndtPrevPolicyNo", "");
				motorMap.put("EndtStatus", "");
				motorMap.put("IsFinanceEndt", "");
				motorMap.put("OrginalPolicyNo", "");
				motorMap.put("Status", "Y");
	
				Map<String,String> exchangeRateScenarioReq =new HashMap<>();
				exchangeRateScenarioReq.put("OldAcccessoriesSumInsured", "");
				exchangeRateScenarioReq.put("OldCurrency", "TZS");
				exchangeRateScenarioReq.put("OldExchangeRate", "1.0");
				exchangeRateScenarioReq.put("OldSumInsured", "");
				exchangeRateScenarioReq.put("OldTppdIncreaeLimit", "");
				exchangeRateScenarioReq.put("OldWindScreenSumInsured", "");
				Map<String,Object> exchangeRateScenario =new HashMap<>();
				exchangeRateScenario.put("ExchangeRateScenario", exchangeRateScenarioReq);
				motorMap.put("Scenarios", exchangeRateScenario);
				Map<String,Object> motorResult=null;
				List<Map<String,Object>> errors=null;
				try {
					String motorReq =mapper.writeValueAsString(motorMap);
					System.out.println(motorReq);
					response =serviceImpl.callEwayApi(saveMotorApi, motorReq);
					System.out.println(response);
					
					Map<String,Object> motorRes =mapper.readValue(response, Map.class);
					errors =motorRes.get("ErrorMessage")==null?null:
						mapper.readValue(mapper.writeValueAsString(motorRes.get("ErrorMessage")), List.class);
					if(errors.isEmpty())
						motorResult =motorRes.get("Result")==null?null:
							mapper.readValue(mapper.writeValueAsString(motorRes.get("Result")), Map.class);
					
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				String errorText="";
				if(!errors.isEmpty()) {
					 errorText =errors.stream().map(p ->p.get("Message").toString())
							.collect(Collectors.joining("||"));
				}
				
				if(StringUtils.isNotBlank(errorText)){
					errorList.add(new Error("*"+errorText+"*","ErrorMsg","500"));
				}
				
				if(errorList.size()>0) {
					throw new WhatsAppValidationException(errorList);

				}
				 
				log.info("SAVE MOTOR INSERT BLOCK END : "+new Date());

				String coverId ="";
				if(motorResult!=null) {
					
					log.info("CALCULATOR BLOCK START : "+new Date());
					
					Map<String,Object> calcMap = new HashMap<>();
					calcMap.put("InsuranceId", motorResult.get("InsuranceId")==null?"":motorResult.get("InsuranceId"));
					calcMap.put("BranchCode", "01");
					calcMap.put("AgencyCode", "10303");
					calcMap.put("SectionId", motorResult.get("SectionId")==null?"":motorResult.get("SectionId"));
					calcMap.put("ProductId", motorResult.get("ProductId")==null?"":motorResult.get("ProductId"));
					calcMap.put("MSRefNo", motorResult.get("MSRefNo")==null?"":motorResult.get("MSRefNo"));
					calcMap.put("VehicleId", motorResult.get("VehicleId")==null?"":motorResult.get("VehicleId"));
					calcMap.put("CdRefNo", motorResult.get("CdRefNo")==null?"":motorResult.get("CdRefNo"));
					calcMap.put("VdRefNo", motorResult.get("VdRefNo")==null?"":motorResult.get("VdRefNo"));
					calcMap.put("CreatedBy", motorResult.get("CreatedBy")==null?"":motorResult.get("CreatedBy"));
					calcMap.put("productId", motorResult.get("ProductId")==null?"":motorResult.get("ProductId"));
					calcMap.put("sectionId", motorResult.get("SectionId")==null?"":motorResult.get("SectionId"));
					calcMap.put("RequestReferenceNo", motorResult.get("RequestReferenceNo")==null?"":motorResult.get("RequestReferenceNo"));
					calcMap.put("EffectiveDate", policyStartDate);
					calcMap.put("PolicyEndDate", policyEndDate);
					calcMap.put("CoverModification", "N");
					
					String calcApi =this.calcApi;
					List<Map<String,Object>> coverList =null;
					Map<String,Object> calcRes=null;
					String refNo="";
					try {
						String calcReq =mapper.writeValueAsString(calcMap);
						log.info("CALC Request || "+calcRes);
						response =serviceImpl.callEwayApi(calcApi, calcReq);
						calcRes =mapper.readValue(response, Map.class);
						coverList=calcRes.get("CoverList")==null?null:
							mapper.readValue(mapper.writeValueAsString(calcRes.get("CoverList")), List.class);
						log.info("CALC Response || "+calcRes);
					}catch (Exception e) {
						e.printStackTrace();
					}
					
					Boolean bCover=coverList.stream().anyMatch(p ->p.get("CoverageType").toString().equalsIgnoreCase("B"));
					
					if(!coverList.isEmpty() && bCover) {
						Map<String,Object> viewCalcMap =new HashMap<String,Object>();
						viewCalcMap.put("ProductId", calcRes.get("ProductId")==null?"":calcRes.get("ProductId").toString());
						viewCalcMap.put("RequestReferenceNo", calcRes.get("RequestReferenceNo")==null?"":calcRes.get("RequestReferenceNo").toString());
						List<Map<String,Object>> view=null;
						Long premium=0L;
						Long vatTax =0L;
						Double vatPercentage=0D;
						List<Map<String,Object>> coverList3=null;
						try {
							String viewCalcReq =mapper.writeValueAsString(viewCalcMap);
							String viewCalc=this.viewCalcApi;
							response =serviceImpl.callEwayApi(viewCalc, viewCalcReq);
							System.out.println("PREMIUM RESPONSE ===>   "+response);
							Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
							 view =viewCalcRes.get("Result")==null?null:
								mapper.readValue(mapper.writeValueAsString(viewCalcRes.get("Result")), List.class);
							 
							 coverList3= view.get(0).get("CoverList")==null?null:
								 mapper.readValue(mapper.writeValueAsString( view.get(0).get("CoverList")), List.class);
						}catch (Exception e) {
							e.printStackTrace();
						}
							refNo=view.get(0).get("RequestReferenceNo")==null?"":view.get(0).get("RequestReferenceNo").toString();
									
							String referalRemarks =coverList3.stream()
									.filter(p -> p.get("CoverageType").equals("B"))
									.map(p ->p.get("ReferalDescription")==null?"":p.get("ReferalDescription").toString())
									.collect(Collectors.joining());
							System.out.println(referalRemarks);
						
							if(StringUtils.isNotBlank(referalRemarks))	{
								
								errorList.add(new Error("*QUOTATION HAS BEEN REFERRAL ("+refNo+") || CONTACT ADMIN..!*", "ErrorMsg", "101"));
							}
							
							if(errorList.size()>0) {
								throw new WhatsAppValidationException(errorList);

							}
							
							Map<String,Object> cover_list=coverList3.stream().filter(p ->p.get("CoverageType").equals("B"))
							.map(p ->p).findFirst().orElse(null);
							
							coverId=cover_list.get("CoverId")==null?"":cover_list.get("CoverId").toString();
							
							Map<String,Object> tax =null;
							try {
							List<Map<String,Object>> taxList =cover_list.get("Taxes")==null?null: 
											mapper.readValue(mapper.writeValueAsString(cover_list.get("Taxes")), List.class);
							tax=taxList.stream().filter(p ->p.get("TaxId").equals("1")).findFirst().orElse(null);
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
							
						premium =cover_list.get("PremiumExcluedTax")==null?0L:Double.valueOf(cover_list.get("PremiumExcluedTax").toString()).longValue();
						vatTax =tax.get("TaxAmount")==null?0L:Double.valueOf(tax.get("TaxAmount").toString()).longValue();
						vatPercentage =tax.get("TaxRate")==null?0L:Double.valueOf(tax.get("TaxRate").toString());
						coverId=cover_list.get("CoverId")==null?"5":cover_list.get("CoverId").toString();
							
						
						Long totalPremium =premium+vatTax;
						
						log.info("CALCULATOR BLOCK END : "+new Date());
		
						//==============================QUOTATION BLOCK END=============================================

									
						//==============================BUYPOLICY BLOCK START=============================================

						
						// user creation block
						
						log.info("USER CREATION BLOCK START : "+new Date());
												
						Map<String,Object> userCreateMap =new HashMap<>();
						userCreateMap.put("CompanyId", "100002");
						userCreateMap.put("CustomerId", customerRefNo);
						userCreateMap.put("ProductId", "5");
						userCreateMap.put("ReferenceNo", refNo);
						userCreateMap.put("UserMobileNo", req.getWhatsAppNo());
						userCreateMap.put("UserMobileCode", req.getWhatsAppCode());
						userCreateMap.put("AgencyCode", "10303");
						
						String userCreationReq =objectPrint.toJson(userCreateMap);
						
						log.info("User Creation Request || "+userCreationReq);
						response =serviceImpl.callEwayApi(this.ewayLoginCreateApi, userCreationReq);
						log.info("User Creation Response || "+response);

						log.info("USER CREATION BLOCK END : "+new Date());

						String exception="";
						
						log.info("BUYPOLICY  BLOCK START : "+new Date());
						
						// buypolicy block 
						Map<String,Object> coversMap =new HashMap<String,Object>();
						coversMap.put("CoverId", coverId);
						coversMap.put("SubCoverId", "");
						coversMap.put("SubCoverYn", "N");
						List<Map<String,Object>> coversMapList =new ArrayList<Map<String,Object>>();
						coversMapList.add(coversMap);
						Map<String,Object> vehicleMap =new HashMap<String,Object>();
						vehicleMap.put("SectionId", sectionId);
						vehicleMap.put("Id", "1");
						vehicleMap.put("Covers", coversMapList);
						List<Map<String,Object>> vehiMapList =new ArrayList<Map<String,Object>>();
						vehiMapList.add(vehicleMap);
						Map<String,Object> buypolicyMap =new HashMap<String,Object>();
						buypolicyMap.put("RequestReferenceNo", refNo);
						buypolicyMap.put("CreatedBy", "guest");
						buypolicyMap.put("ProductId", "5");
						buypolicyMap.put("ManualReferralYn", "N");
						buypolicyMap.put("Vehicles", vehiMapList);

						String buypolicyReq =objectPrint.toJson(buypolicyMap);
						
						System.out.println("buypolicyReq" +buypolicyReq);
						response =serviceImpl.callEwayApi(buyploicyApi, buypolicyReq);
						System.out.println("buypolicyRes" +response);

						Map<String,Object> buyPolicyResult =null;
							try {	
								Map<String,Object>	buyPolicyRes =mapper.readValue(response, Map.class);
								buyPolicyResult =buyPolicyRes.get("Result")==null?null:
									mapper.readValue(mapper.writeValueAsString(buyPolicyRes.get("Result")), Map.class);
							}catch (Exception e) {
								e.printStackTrace();
								exception=e.getMessage();
							}
							

							if(StringUtils.isNotBlank(exception)) {
								errorList.add(new Error(exception, "ErrorMsg", "101"));
							}
							
							if(errorList.size()>0) {
								throw new WhatsAppValidationException(errorList);

							}
						
							log.info("BUYPOLICY  BLOCK END : "+new Date());
							
							log.info("MAKE PAYMENT BLOCK START : "+new Date());
							
							// make payment
							Map<String,Object> makePaymentMap = new HashMap<String,Object>();
							makePaymentMap.put("CreatedBy", "guest");
							makePaymentMap.put("EmiYn", "N");
							makePaymentMap.put("InstallmentMonth", "");
							makePaymentMap.put("InstallmentPeriod", "");
							makePaymentMap.put("InsuranceId", "100002");
							makePaymentMap.put("Premium", totalPremium);
							makePaymentMap.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
							makePaymentMap.put("Remarks", "None");
							makePaymentMap.put("SubUserType", "B2C");
							makePaymentMap.put("UserType", "Broker");
								
							String makePaymentReq =objectPrint.toJson(makePaymentMap);
							
							System.out.println("makePaymentReq" +makePaymentReq);

							response =serviceImpl.callEwayApi(makePaymentApi, makePaymentReq);
							System.out.println("makePaymentRes" +response);

							Map<String,Object> makePaymentResult =null;
								try {	
									Map<String,Object>	makePaymentRes =mapper.readValue(response, Map.class);
									makePaymentResult =makePaymentRes.get("Result")==null?null:
										mapper.readValue(mapper.writeValueAsString(makePaymentRes.get("Result")), Map.class);
								}catch (Exception e) {
									e.printStackTrace();
									exception=e.getMessage();
								}
							
								
								if(StringUtils.isNotBlank(exception)) {
									errorList.add(new Error(exception, "ErrorMsg", "101"));
								}
								
								if(errorList.size()>0) {
									throw new WhatsAppValidationException(errorList);

								}
								
								// insert payment 
								
								Map<String,Object> insertPayment =new HashMap<String,Object>();
								insertPayment.put("CreatedBy", "guest");
								insertPayment.put("InsuranceId", "100002");
								insertPayment.put("EmiYn", "N");
								insertPayment.put("Premium", totalPremium);
								insertPayment.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
								insertPayment.put("Remarks", "None");
								insertPayment.put("PayeeName", customerName);
								insertPayment.put("SubUserType", "B2C");
								insertPayment.put("UserType", "Broker");
								insertPayment.put("PaymentId", makePaymentResult.get("PaymentId"));
								insertPayment.put("PaymentType", "4");
								
								String insertPaymentReq =objectPrint.toJson(insertPayment);
								
								System.out.println("insertPaymentReq" +insertPaymentReq);

								response =serviceImpl.callEwayApi(insertPaymentApi, insertPaymentReq);
								
								System.out.println("insertPaymentRes" +response);
								
								Map<String,Object> insertPaymentResult =null;
								try {	
									Map<String,Object>	insertPaymentRes =mapper.readValue(response, Map.class);
									insertPaymentResult =insertPaymentRes.get("Result")==null?null:
										mapper.readValue(mapper.writeValueAsString(insertPaymentRes.get("Result")), Map.class);
								}catch (Exception e) {
									e.printStackTrace();
									exception=e.getMessage();
								}
								
								
								if(StringUtils.isNotBlank(exception)) {
									errorList.add(new Error(exception, "ErrorMsg", "101"));
								}
								
								if(errorList.size()>0) {
									throw new WhatsAppValidationException(errorList);

								}
							
							String merchantRefNo =insertPaymentResult.get("MerchantReference")==null?"":
									insertPaymentResult.get("MerchantReference").toString();
							
							String quoteNo =insertPaymentResult.get("QuoteNo")==null?"":
								insertPaymentResult.get("QuoteNo").toString();
							
							log.info("RequestRefNo : "+refNo+" ||  MerchantReference : "+merchantRefNo+" || QuoteNo : "+quoteNo+" ");
					
							
							log.info("MAKE PAYMENT BLOCK END : "+new Date());
							
							Map<String,String> paymentMap =new HashMap<>();
							paymentMap.put("MerchantRefNo", merchantRefNo);
							paymentMap.put("CompanyId", "100002");
							paymentMap.put("WhatsappCode", req.getWhatsAppCode());
							paymentMap.put("WhtsappNo", req.getWhatsAppNo());
							paymentMap.put("QuoteNo", quoteNo);
							
							String payJson =objectPrint.toJson(paymentMap);
							
							String encodeReq =Base64.getEncoder().encodeToString(payJson.getBytes());
							
							String paymnetUrl =ewayMotorPaymentLink+encodeReq;
							
							System.out.println("PAYMENT LINK :" +paymnetUrl);
							
						//==============================BUYPOLICY BLOCK END=============================================
							
						//whatsapp BOT Response		
						Map<String,String> map =new HashMap<>();
						map.put("referenceno", view.get(0).get("RequestReferenceNo")==null?"":view.get(0).get("RequestReferenceNo").toString());
						map.put("inceptiondate", policyStartDate);
						map.put("expirydate", policyEndDate);
						map.put("link", paymnetUrl);
						map.put("registration", tiraResult.get("Registrationnumber")==null?"":tiraResult.get("Registrationnumber").toString());
						map.put("chassis", tiraResult.get("Chassisnumber")==null?"":tiraResult.get("Chassisnumber").toString());
						map.put("suminsured", StringUtils.isBlank(req.getSumInsured()) ?"N/A":req.getSumInsured());
						map.put("usage",tiraResult.get("Motorusage")==null?"":tiraResult.get("Motorusage").toString());
						map.put("vehtype", tiraResult.get("VehicleType")==null?"":tiraResult.get("VehicleType").toString());
						map.put("color",tiraResult.get("Color")==null?"":tiraResult.get("Color").toString());
						map.put("premium", premium.toString());
						map.put("vatamt", vatTax.toString());
						map.put("totalpremium", totalPremium.toString());
						map.put("vat", String.valueOf(vatPercentage.longValue()));
						return map;
						
					}else {
						errorList.add(new Error("*SERVICE IS NOT RETURNED PREMIUM && COVERS ("+refNo+") || CONTACT ADMIN..!*", "ErrorMsg", "101"));
					}
					
				}
						
			
			if(errorList.size()>0) {
				throw new WhatsAppValidationException(errorList);

			}
			
		return null;
	}
	
	public Map<String,Object> flowCreateQuote(FlowCreateQuoteReq reqq) {
		Map<String,Object> flowRes = new HashMap<>();
	    List<Error> errorList = new ArrayList<>();
		String response ="";
		String companyId ="100002";
		String errorText ="";
		//==============================TIRA CHECK BLOCK START=============================================
		
		log.info("TIRA BLOCK START TIME : "+new Date());
		
		Map<String,String> tiraMap = new HashMap<String,String>();
		tiraMap.put("ReqChassisNumber", "");
		tiraMap.put("ReqRegNumber", reqq.getRegisrationNo());
		tiraMap.put("InsuranceId", companyId);
		tiraMap.put("BranchCode", "02");
		tiraMap.put("BrokerBranchCode", "01");
		tiraMap.put("ProductId", "5");
		tiraMap.put("CreatedBy", "guest");
		tiraMap.put("SavedFrom", "API");
		Map<String,Object> policyHolder =null;
		Map<String,Object> tiraResult =null;

		try {
			String tiraReq =mapper.writeValueAsString(tiraMap);
			String tiraApi =this.tiraApi;
			
			response= serviceImpl.callEwayApi(tiraApi,tiraReq);
			Map<String,Object> strToMap =mapper.readValue(response, Map.class);
			tiraResult =strToMap.get("Result")==null?null:
				mapper.readValue(mapper.writeValueAsString(strToMap.get("Result")), Map.class);
			
			if(tiraResult!=null) {
				 policyHolder =tiraResult.get("PolicyHolderInfo")==null?null:
					mapper.readValue(mapper.writeValueAsString(tiraResult.get("PolicyHolderInfo")), Map.class);
			}
			
			if(policyHolder==null) {
				String policyHolderApi =this.policyHolderApi;
				response= serviceImpl.callEwayApi(policyHolderApi,tiraReq);
				Map<String,Object> strToMap1 =mapper.readValue(response, Map.class);
				policyHolder =strToMap1.get("Result")==null?null:
					mapper.readValue(mapper.writeValueAsString(strToMap1.get("Result")), Map.class);
			}
			
			String errorMessage =tiraMap.get("ErrorMessage")==null?"":tiraMap.get("ErrorMessage").toString();

			if(StringUtils.isNotBlank(errorMessage)){
				flowRes.put("ErrorDesc", errorMessage);
				return flowRes;
			}
		
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("TIRA BLOCK END : "+new Date());

		//==============================TIRA CHECK BLOCK END=============================================

		
		//==============================CUSTOMER CREATION BLOCK START=============================================

		log.info("CUSTOMER INSERT BLOCK START : "+new Date());
		
		String airtelPayno =reqq.getAirtelPayNo().startsWith("0")?reqq.getAirtelPayNo().substring(1):reqq.getAirtelPayNo();
		String nida_date_of_birth ="";
		Map<String,Object> customerMap= new HashMap<String,Object>();
		String customerRefNo="";
		if("1".equals(reqq.getIdType())) {
			String nida_no =reqq.getIdNumber();
			if(nida_no.length()>7) {
				String subStr =nida_no.substring(0, 8);
				String year =subStr.substring(0,4);
				String month =subStr.substring(4,6);
				String date =subStr.substring(6,8);
				nida_date_of_birth =date+"/"+month+"/"+year;
				System.out.println("NIDA ===> DATE OF BIRTH "+ nida_date_of_birth);
			}	
		}
		
		if(policyHolder==null) {
			String api =this.customerApi;
			String id=reqq.getIdType();
			customerMap.put("InsuranceId", "100002");
			customerMap.put("BranchCode", "02");
			customerMap.put("ProductId", "5");
			customerMap.put("BrokerBranchCode", "1");
			customerMap.put("ClientName", reqq.getCustomerName());
			customerMap.put("CreatedBy", "guest");
			customerMap.put("BusinessType", "1");
			customerMap.put("IdNumber", reqq.getIdNumber());
			customerMap.put("Clientstatus", "Y");
			customerMap.put("IdType", "");
			customerMap.put("Title", "1");
			customerMap.put("SaveOrSubmit", "Save");
			customerMap.put("Status", "Y");
			customerMap.put("MobileNo1",airtelPayno);
			customerMap.put("MobileCode1", "255");
			customerMap.put("PolicyHolderType", "1");
			customerMap.put("Nationality", "TZA");
			customerMap.put("Gender", "M");
			
			customerMap.put("CityName", "Ilala");
			customerMap.put("CityCode", "11000");
			customerMap.put("RegionCode", "02");
			customerMap.put("StateCode", "10000");
			customerMap.put("StateName", "Dar es Salaam");
			customerMap.put("Street", "7th FLOOR,Exim Tower,Ghana Avenue");
			customerMap.put("Address1", "P.O.Box 9942,Dar es Salaam");
			
			customerMap.put("PolicyHolderTypeid", id);

			customerMap.put("IsTaxExempted", "N");
			
			
			customerMap.put("DobOrRegDate", id.equals("1")?nida_date_of_birth:"13/01/2004"); 	
			
			String customerReq =objectPrint.toJson(customerMap);
			response =serviceImpl.callEwayApi(api, customerReq);
		    Map<String,Object> customerRes = null;
		    Map<String,Object> result =null;
			try {
				customerRes=mapper.readValue(response, Map.class);
				result =mapper.readValue(mapper.writeValueAsString(customerRes.get("Result")) , Map.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			
			customerRefNo =result.get("SuccessId")==null?"":result.get("SuccessId").toString();
		}else {
			
			String api =this.customerApi;
			String id=reqq.getIdType();
			customerMap.put("InsuranceId", "100002");
			customerMap.put("BranchCode", "02");
			customerMap.put("ProductId", "5");
			customerMap.put("BrokerBranchCode", "1");
			customerMap.put("ClientName", reqq.getCustomerName());
			customerMap.put("CreatedBy", "guest");
			customerMap.put("BusinessType", "1");
			customerMap.put("IdNumber", reqq.getIdNumber());
			customerMap.put("Clientstatus", "Y");
			customerMap.put("IdType", "");
			customerMap.put("Title", "1");
			customerMap.put("SaveOrSubmit", "Save");
			customerMap.put("Status", "Y");
			customerMap.put("MobileNo1",airtelPayno);
			customerMap.put("MobileCode1","255");
			
			customerMap.put("PolicyHolderType", "1");
			customerMap.put("CityName", policyHolder.get("Districtname")==null?"Ilala":policyHolder.get("Districtname").toString());
			customerMap.put("CityCode", policyHolder.get("Districtcode")==null?"11000":policyHolder.get("Districtcode").toString());
			customerMap.put("Nationality", "TZA");
			customerMap.put("Gender", policyHolder.get("Gender")==null?"":policyHolder.get("Gender").toString());
			customerMap.put("RegionCode",policyHolder.get("Regioncode")==null?"02":policyHolder.get("Regioncode").toString());
			customerMap.put("StateCode",policyHolder.get("Districtcode")==null?"10000":policyHolder.get("Districtcode").toString());
			customerMap.put("StateName",policyHolder.get("Regionname")==null?"Dar es Salaam":policyHolder.get("Regionname").toString());
			customerMap.put("Street", policyHolder.get("Regionname")==null?"7th FLOOR,Exim Tower,Ghana Avenue":policyHolder.get("Regionname").toString());
			customerMap.put("Address1",policyHolder.get("Districtname")==null?"P.O.Box 9942,Dar es Salaam":policyHolder.get("Districtname").toString());
			customerMap.put("IsTaxExempted", "N");
			
		
			customerMap.put("PolicyHolderTypeid", id);
			customerMap.put("DobOrRegDate", id.equals("1")?nida_date_of_birth:"13/01/2004"); 	
			String customerReq =objectPrint.toJson(customerMap);
			response =serviceImpl.callEwayApi(api, customerReq);
		    Map<String,Object> customerRes = null;
		    Map<String,Object> result =null;
			try {
				customerRes=mapper.readValue(response, Map.class);
				result =mapper.readValue(mapper.writeValueAsString(customerRes.get("Result")) , Map.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			
			customerRefNo =result.get("SuccessId")==null?"":result.get("SuccessId").toString();
		}
		
		
		String customerCreation =objectPrint.toJson(customerMap);
		
		
		System.out.println("CustomerCreation + ||" +customerCreation);
		
		log.info("CUSTOMER INSERT BLOCK END : "+new Date());

		
		if(StringUtils.isBlank(customerRefNo) && policyHolder==null) {
				flowRes.put("ErrorDesc", "CUSTOMER CREATION FAILED || CONTACT ADMIN..!");
				return flowRes;
			
			//errorList.add(new Error("CUSTOMER CREATION FAILED || CONTACT ADMIN..!","ErrorMsg","500"));
		}
		
		//if(errorList.size()>0) {
			//throw new WhatsAppValidationException(errorList);
		//}
			
		//==============================CUSTOMER CREATION BLOCK END=============================================

		
		//==============================QUOTATION BLOCK START=============================================

		    Map<String,String> policyDate=findPolicyDates(tiraResult);
			
			String policyStartDate =policyDate.get("PolicyStartDate");
			String policyEndDate =policyDate.get("PolicyEndDate");
			String bodyTypeDesc =tiraResult.get("VehicleType")==null?"":tiraResult.get("VehicleType").toString();
			Map<String,String> motorUsageMap =new HashMap<>();
			motorUsageMap.put("CompanyId", companyId);
			motorUsageMap.put("BodyType", bodyTypeDesc);
			motorUsageMap.put("MotorUsageName", "Private or Normal");
			
			/*String motorUsageApi =this.vehUpdateApi;
			Gson gson = new Gson();
			String motorUsageReq =gson.toJson(motorUsageMap);
			response =serviceImpl.callEwayApi(motorUsageApi, motorUsageReq);
		    Map<String,Object> motorUsageRes = null;
			try {
				motorUsageRes=mapper.readValue(response, Map.class);
			} catch (Exception e1) {
				e1.printStackTrace();
			} 
			System.out.println(motorUsageRes);
			
			String bodyType =motorUsageRes.get("BodyId")==null?"":motorUsageRes.get("BodyId").toString();
			
			if(StringUtils.isBlank(bodyType)) {
				errorList.add(new Error("BODYTYPE NOT FOUND || REGISTRATION NO : "+req.getRegisrationNo()+"|| BODY TYPE : "+bodyTypeDesc+" || CONTACT ADMIN..!","ErrorMsg",""));
				throw new WhatsAppValidationException(errorList);
			}*/
			
			log.info("SAVE MOTOR INSERT BLOCK START : "+new Date());

		//	Map<String,Object> sectionMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_SECTION",req.getSectionId());
			//String sectionId =sectionMap.get("CODE")==null?"":sectionMap.get("CODE").toString();
			//Map<String,Object> usageMap =claimIntimationRepository.getClaimDeatils(req.getMobileNo(),"MOTOR_USAGE",req.getMotorUsageId());
		//	String motorUsage =usageMap.get("CODE")==null?"":usageMap.get("CODE").toString();
			
			
			String saveMotorApi=this.saveMotorApi;
			Map<String,Object> motorMap =new HashMap<>();
			motorMap.put("BrokerBranchCode", "1");// login
			motorMap.put("AcExecutiveId", ""); 
			motorMap.put("CommissionType", ""); 
			motorMap.put("CustomerCode", "620499");// login
			motorMap.put("CustomerName", reqq.getCustomerName()); //login
			motorMap.put("BdmCode", "620499");  //login
			motorMap.put("BrokerCode", "10303"); //login
			motorMap.put("LoginId", "guest"); //login
			motorMap.put("SubUserType", "B2C Broker"); //login
			motorMap.put("ApplicationId", "1");
			motorMap.put("CustomerReferenceNo",customerRefNo);
			motorMap.put("RequestReferenceNo", "");
			motorMap.put("Idnumber", reqq.getIdNumber());
			motorMap.put("VehicleId", "1");
			motorMap.put("AcccessoriesSumInsured", "0");
			motorMap.put("AccessoriesInformation", "");
			motorMap.put("AdditionalCircumstances", "");
			motorMap.put("AxelDistance", "");
			motorMap.put("Chassisnumber", tiraResult.get("Chassisnumber")==null?"": tiraResult.get("Chassisnumber"));
			motorMap.put("Color", tiraResult.get("Color")==null?"": tiraResult.get("Color"));
			motorMap.put("CityLimit", "");
			motorMap.put("CoverNoteNo", "");
			motorMap.put("OwnerCategory", tiraResult.get("OwnerCategory")==null?"": tiraResult.get("OwnerCategory"));
			motorMap.put("CubicCapacity", "");
			motorMap.put("CreatedBy", "guest");
			motorMap.put("DrivenByDesc", "D");
			motorMap.put("EngineNumber", tiraResult.get("EngineNumber")==null?"": tiraResult.get("EngineNumber"));
			motorMap.put("FuelType", tiraResult.get("FuelType")==null?"": tiraResult.get("FuelType"));
			motorMap.put("Gpstrackinginstalled", "N");
			motorMap.put("Grossweight", tiraResult.get("Grossweight")==null?"": tiraResult.get("Grossweight"));
			motorMap.put("HoldInsurancePolicy", "N"); 
			motorMap.put("Insurancetype", reqq.getSectionId()); //dub
			motorMap.put("InsuranceId", "100002");
			motorMap.put("InsuranceClass", reqq.getTypeofInsurance());//req.getPolicyType());
			motorMap.put("InsurerSettlement", "");
			motorMap.put("InterestedCompanyDetails", "");
			motorMap.put("ManufactureYear", tiraResult.get("ManufactureYear")==null?"": tiraResult.get("ManufactureYear"));
			motorMap.put("ModelNumber", "");
			motorMap.put("MotorCategory", tiraResult.get("ReqMotorCategory")==null?"": tiraResult.get("ReqMotorCategory"));
			motorMap.put("Motorusage", reqq.getMotorUsageId()); //doubt
			motorMap.put("NcdYn", reqq.getClaimType().equals("1")?"Y":"N");
			motorMap.put("NoOfClaims", "");
			motorMap.put("NumberOfAxels", tiraResult.get("NumberOfAxels")==null?"": tiraResult.get("NumberOfAxels"));
			motorMap.put("BranchCode", "02"); //login
			motorMap.put("AgencyCode", "10303");//ogin
			motorMap.put("ProductId", "5");
			motorMap.put("SectionId", reqq.getSectionId());//Insurancetype as same
			motorMap.put("PolicyType", reqq.getTypeofInsurance());//req.getPolicyType());// policy yeare same as
			motorMap.put("RadioOrCasseteplayer", "");
			motorMap.put("RegistrationYear", "99999");
			motorMap.put("Registrationnumber", reqq.getRegisrationNo());
			motorMap.put("RoofRack", "");
			motorMap.put("SeatingCapacity", "");
			motorMap.put("SourceType", "B2C Broker");
			motorMap.put("SpotFogLamp", "");
			motorMap.put("Stickerno", "");
			motorMap.put("SumInsured", StringUtils.isBlank(reqq.getSumInsured())?"0":reqq.getSumInsured());
			motorMap.put("Tareweight", tiraResult.get("Tareweight")==null?"": tiraResult.get("Tareweight"));
			motorMap.put("TppdFreeLimit", "");
			motorMap.put("TppdIncreaeLimit", "0");
			motorMap.put("TrailerDetails", "");
			motorMap.put("Vehcilemodel", tiraResult.get("Vehcilemodel")==null?"": tiraResult.get("Vehcilemodel"));
			motorMap.put("VehicleType", reqq.getBodyType());//tiraResult.get("VehicleType")==null?"": tiraResult.get("VehicleType"));
			motorMap.put("Vehiclemake", tiraResult.get("Vehiclemake")==null?"": tiraResult.get("Vehiclemake"));
			motorMap.put("WindScreenSumInsured", "0");
			motorMap.put("Windscreencoverrequired", "");
			motorMap.put("accident", "");
			motorMap.put("periodOfInsurance", "365");
			motorMap.put("accident", "");
			motorMap.put("periodOfInsurance", "");
			motorMap.put("PolicyStartDate", policyStartDate);
			motorMap.put("PolicyEndDate", policyEndDate);
			motorMap.put("Currency", "TZS");
			motorMap.put("ExchangeRate", "1.0");
			motorMap.put("HavePromoCode", "N");
			motorMap.put("CollateralYn", "");
			motorMap.put("BorrowerType", "");
			motorMap.put("CollateralName", "");
			motorMap.put("FirstLossPayee", "");
			motorMap.put("FleetOwnerYn", "N");
			motorMap.put("NoOfVehicles", "0");
			motorMap.put("NoOfComprehensives", "");
			motorMap.put("ClaimRatio", "");
			motorMap.put("SavedFrom", "Customer");
			motorMap.put("UserType", "Broker");
			motorMap.put("TiraCoverNoteNo", "");
			motorMap.put("EndorsementYn", "N");
			motorMap.put("EndorsementDate", "");
			motorMap.put("EndorsementEffectiveDate", "");
			motorMap.put("EndorsementRemarks", "");
			motorMap.put("EndorsementType", "");
			motorMap.put("EndorsementTypeDesc", "");
			motorMap.put("EndtCategoryDesc", "");
			motorMap.put("EndtCount", "");
			motorMap.put("EndtPrevPolicyNo", "");
			motorMap.put("EndtStatus", "");
			motorMap.put("IsFinanceEndt", "");
			motorMap.put("OrginalPolicyNo", "");
			motorMap.put("Status", "Y");

			Map<String,String> exchangeRateScenarioReq =new HashMap<>();
			exchangeRateScenarioReq.put("OldAcccessoriesSumInsured", "");
			exchangeRateScenarioReq.put("OldCurrency", "TZS");
			exchangeRateScenarioReq.put("OldExchangeRate", "1.0");
			exchangeRateScenarioReq.put("OldSumInsured", "");
			exchangeRateScenarioReq.put("OldTppdIncreaeLimit", "");
			exchangeRateScenarioReq.put("OldWindScreenSumInsured", "");
			Map<String,Object> exchangeRateScenario =new HashMap<>();
			exchangeRateScenario.put("ExchangeRateScenario", exchangeRateScenarioReq);
			motorMap.put("Scenarios", exchangeRateScenario);
			Map<String,Object> motorResult=null;
			List<Map<String,Object>> errors=null;
			try {
				String motorReq =mapper.writeValueAsString(motorMap);
				System.out.println(motorReq);
				response =serviceImpl.callEwayApi(saveMotorApi, motorReq);
				System.out.println(response);
				
				Map<String,Object> motorRes =mapper.readValue(response, Map.class);
				errors =motorRes.get("ErrorMessage")==null?null:
					mapper.readValue(mapper.writeValueAsString(motorRes.get("ErrorMessage")), List.class);
				if(errors.isEmpty())
					motorResult =motorRes.get("Result")==null?null:
						mapper.readValue(mapper.writeValueAsString(motorRes.get("Result")), Map.class);
				
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			String errorTex="";
			if(!errors.isEmpty()) {
				 errorText =errors.stream().map(p ->p.get("Message").toString())
						.collect(Collectors.joining("||"));
			}
			
			if(StringUtils.isNotBlank(errorTex)){
				//errorList.add(new Error("*"+errorText+"*","ErrorMsg","500"));
				flowRes.put("ErrorDesc", errorTex);
				return flowRes;
			}
			
			//if(errorList.size()>0) {
			//	throw new WhatsAppValidationException(errorList);

			//}
			
			
			 
			log.info("SAVE MOTOR INSERT BLOCK END : "+new Date());

			String coverId ="";
			if(motorResult!=null) {
				
				log.info("CALCULATOR BLOCK START : "+new Date());
				
				Map<String,Object> calcMap = new HashMap<>();
				calcMap.put("InsuranceId", motorResult.get("InsuranceId")==null?"":motorResult.get("InsuranceId"));
				calcMap.put("BranchCode", "02");
				calcMap.put("AgencyCode", "10303");
				calcMap.put("SectionId", motorResult.get("SectionId")==null?"":motorResult.get("SectionId"));
				calcMap.put("ProductId", motorResult.get("ProductId")==null?"":motorResult.get("ProductId"));
				calcMap.put("MSRefNo", motorResult.get("MSRefNo")==null?"":motorResult.get("MSRefNo"));
				calcMap.put("VehicleId", motorResult.get("VehicleId")==null?"":motorResult.get("VehicleId"));
				calcMap.put("CdRefNo", motorResult.get("CdRefNo")==null?"":motorResult.get("CdRefNo"));
				calcMap.put("VdRefNo", motorResult.get("VdRefNo")==null?"":motorResult.get("VdRefNo"));
				calcMap.put("CreatedBy", motorResult.get("CreatedBy")==null?"":motorResult.get("CreatedBy"));
				calcMap.put("productId", motorResult.get("ProductId")==null?"":motorResult.get("ProductId"));
				calcMap.put("sectionId", motorResult.get("SectionId")==null?"":motorResult.get("SectionId"));
				calcMap.put("RequestReferenceNo", motorResult.get("RequestReferenceNo")==null?"":motorResult.get("RequestReferenceNo"));
				calcMap.put("EffectiveDate", policyStartDate);
				calcMap.put("PolicyEndDate", policyEndDate);
				calcMap.put("CoverModification", "N");
				
				String calcApi =this.calcApi;
				List<Map<String,Object>> coverList =null;
				Map<String,Object> calcRes=null;
				String refNo="";
				try {
					String calcReq =mapper.writeValueAsString(calcMap);
					log.info("CALC Request || "+calcRes);
					response =serviceImpl.callEwayApi(calcApi, calcReq);
					calcRes =mapper.readValue(response, Map.class);
					coverList=calcRes.get("CoverList")==null?null:
						mapper.readValue(mapper.writeValueAsString(calcRes.get("CoverList")), List.class);
					log.info("CALC Response || "+calcRes);
				}catch (Exception e) {
					e.printStackTrace();
				}
				
				Boolean bCover=coverList.stream().anyMatch(p ->p.get("CoverageType").toString().equalsIgnoreCase("B"));
				
				if(!coverList.isEmpty() && bCover) {
					Map<String,Object> viewCalcMap =new HashMap<String,Object>();
					viewCalcMap.put("ProductId", calcRes.get("ProductId")==null?"":calcRes.get("ProductId").toString());
					viewCalcMap.put("RequestReferenceNo", calcRes.get("RequestReferenceNo")==null?"":calcRes.get("RequestReferenceNo").toString());
					List<Map<String,Object>> view=null;
					Long premium=0L;
					Long vatTax =0L;
					Double vatPercentage=0D;
					List<Map<String,Object>> coverList3=null;
					try {
						String viewCalcReq =mapper.writeValueAsString(viewCalcMap);
						String viewCalc=this.viewCalcApi;
						response =serviceImpl.callEwayApi(viewCalc, viewCalcReq);
						System.out.println("PREMIUM RESPONSE ===>   "+response);
						Map<String,Object> viewCalcRes =mapper.readValue(response, Map.class);
						 view =viewCalcRes.get("Result")==null?null:
							mapper.readValue(mapper.writeValueAsString(viewCalcRes.get("Result")), List.class);
						 
						 coverList3= view.get(0).get("CoverList")==null?null:
							 mapper.readValue(mapper.writeValueAsString( view.get(0).get("CoverList")), List.class);
					}catch (Exception e) {
						e.printStackTrace();
					}
						refNo=view.get(0).get("RequestReferenceNo")==null?"":view.get(0).get("RequestReferenceNo").toString();
								
						String referalRemarks =coverList3.stream()
								.filter(p -> p.get("CoverageType").equals("B"))
								.map(p ->p.get("ReferalDescription")==null?"":p.get("ReferalDescription").toString())
								.collect(Collectors.joining());
						System.out.println(referalRemarks);
					
						if(StringUtils.isNotBlank(referalRemarks))	{
							flowRes.put("ErrorDesc", "QUOTATION HAS BEEN REFERRAL ("+refNo+") || CONTACT ADMIN..!*");
							return flowRes;
							//errorList.add(new Error("*QUOTATION HAS BEEN REFERRAL ("+refNo+") || CONTACT ADMIN..!*", "ErrorMsg", "101"));
						}
						
						//if(errorList.size()>0) {
							//throw new WhatsAppValidationException(errorList);

						//}
						
						Map<String,Object> cover_list=coverList3.stream().filter(p ->p.get("CoverageType").equals("B"))
						.map(p ->p).findFirst().orElse(null);
						
						coverId=cover_list.get("CoverId")==null?"":cover_list.get("CoverId").toString();
						
						Map<String,Object> tax =null;
						try {
						List<Map<String,Object>> taxList =cover_list.get("Taxes")==null?null: 
										mapper.readValue(mapper.writeValueAsString(cover_list.get("Taxes")), List.class);
						tax=taxList.stream().filter(p ->p.get("TaxId").equals("1")).findFirst().orElse(null);
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}
						
					premium =cover_list.get("PremiumExcluedTax")==null?0L:Double.valueOf(cover_list.get("PremiumExcluedTax").toString()).longValue();
					vatTax =tax.get("TaxAmount")==null?0L:Double.valueOf(tax.get("TaxAmount").toString()).longValue();
					vatPercentage =tax.get("TaxRate")==null?0L:Double.valueOf(tax.get("TaxRate").toString());
					coverId=cover_list.get("CoverId")==null?"5":cover_list.get("CoverId").toString();
						
					
					Long totalPremium =premium+vatTax;
					
					log.info("CALCULATOR BLOCK END : "+new Date());
	
					//==============================QUOTATION BLOCK END=============================================

								
					//==============================BUYPOLICY BLOCK START=============================================

					
					/*// user creation block
					
					log.info("USER CREATION BLOCK START : "+new Date());
											
					Map<String,Object> userCreateMap =new HashMap<>();
					userCreateMap.put("CompanyId", "100002");
					userCreateMap.put("CustomerId", customerRefNo);
					userCreateMap.put("ProductId", "5");
					userCreateMap.put("ReferenceNo", refNo);
					userCreateMap.put("UserMobileNo", reqq.getWhatsAppNo());
					userCreateMap.put("UserMobileCode", reqq.getWhatsAppCode());
					userCreateMap.put("AgencyCode", "10303");
					
					String userCreationReq =objectPrint.toJson(userCreateMap);
					
					log.info("User Creation Request || "+userCreationReq);
					response =serviceImpl.callEwayApi(this.ewayLoginCreateApi, userCreationReq);
					log.info("User Creation Response || "+response);

					log.info("USER CREATION BLOCK END : "+new Date());

					String exception="";
					
					log.info("BUYPOLICY  BLOCK START : "+new Date());
					
					// buypolicy block 
					Map<String,Object> coversMap =new HashMap<String,Object>();
					coversMap.put("CoverId", coverId);
					coversMap.put("SubCoverId", "");
					coversMap.put("SubCoverYn", "N");
					List<Map<String,Object>> coversMapList =new ArrayList<Map<String,Object>>();
					coversMapList.add(coversMap);
					Map<String,Object> vehicleMap =new HashMap<String,Object>();
					vehicleMap.put("SectionId", reqq.getSectionId());
					vehicleMap.put("Id", "1");
					vehicleMap.put("Covers", coversMapList);
					List<Map<String,Object>> vehiMapList =new ArrayList<Map<String,Object>>();
					vehiMapList.add(vehicleMap);
					Map<String,Object> buypolicyMap =new HashMap<String,Object>();
					buypolicyMap.put("RequestReferenceNo", refNo);
					buypolicyMap.put("CreatedBy", "guest");
					buypolicyMap.put("ProductId", "5");
					buypolicyMap.put("ManualReferralYn", "N");
					buypolicyMap.put("Vehicles", vehiMapList);

					String buypolicyReq =objectPrint.toJson(buypolicyMap);
					
					System.out.println("buypolicyReq" +buypolicyReq);
					response =serviceImpl.callEwayApi(buyploicyApi, buypolicyReq);
					System.out.println("buypolicyRes" +response);

					Map<String,Object> buyPolicyResult =null;
						try {	
							Map<String,Object>	buyPolicyRes =mapper.readValue(response, Map.class);
							buyPolicyResult =buyPolicyRes.get("Result")==null?null:
								mapper.readValue(mapper.writeValueAsString(buyPolicyRes.get("Result")), Map.class);
						}catch (Exception e) {
							e.printStackTrace();
							exception=e.getMessage();
						}
						

						if(StringUtils.isNotBlank(exception)) {
							flowRes.put("ErrorDesc", exception);
							return flowRes;
						
							//errorList.add(new Error(exception, "ErrorMsg", "101"));
						}
						
						//if(errorList.size()>0) {
							//throw new WhatsAppValidationException(errorList);

						//}
					
						log.info("BUYPOLICY  BLOCK END : "+new Date());
						
						log.info("MAKE PAYMENT BLOCK START : "+new Date());
						
						// make payment
						Map<String,Object> makePaymentMap = new HashMap<String,Object>();
						makePaymentMap.put("CreatedBy", "guest");
						makePaymentMap.put("EmiYn", "N");
						makePaymentMap.put("InstallmentMonth", "");
						makePaymentMap.put("InstallmentPeriod", "");
						makePaymentMap.put("InsuranceId", "100002");
						makePaymentMap.put("Premium", totalPremium);
						makePaymentMap.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
						makePaymentMap.put("Remarks", "None");
						makePaymentMap.put("SubUserType", "B2C");
						makePaymentMap.put("UserType", "Broker");
							
						String makePaymentReq =objectPrint.toJson(makePaymentMap);
						
						System.out.println("makePaymentReq" +makePaymentReq);

						response =serviceImpl.callEwayApi(makePaymentApi, makePaymentReq);
						System.out.println("makePaymentRes" +response);

						Map<String,Object> makePaymentResult =null;
							try {	
								Map<String,Object>	makePaymentRes =mapper.readValue(response, Map.class);
								makePaymentResult =makePaymentRes.get("Result")==null?null:
									mapper.readValue(mapper.writeValueAsString(makePaymentRes.get("Result")), Map.class);
							}catch (Exception e) {
								e.printStackTrace();
								exception=e.getMessage();
							}
						
							
							if(StringUtils.isNotBlank(exception)) {
								//errorList.add(new Error(exception, "ErrorMsg", "101"));
								flowRes.put("ErrorDesc", exception);
								return flowRes;
							}
							
							//if(errorList.size()>0) {
							//	throw new WhatsAppValidationException(errorList);

							//}
							
							// insert payment 
							
							Map<String,Object> insertPayment =new HashMap<String,Object>();
							insertPayment.put("CreatedBy", "guest");
							insertPayment.put("InsuranceId", "100002");
							insertPayment.put("EmiYn", "N");
							insertPayment.put("Premium", totalPremium);
							insertPayment.put("QuoteNo", buyPolicyResult.get("QuoteNo"));
							insertPayment.put("Remarks", "None");
							insertPayment.put("PayeeName", reqq.getCustomerName());
							insertPayment.put("SubUserType", "B2C");
							insertPayment.put("UserType", "Broker");
							insertPayment.put("PaymentId", makePaymentResult.get("PaymentId"));
							insertPayment.put("PaymentType", "4");
							
							String insertPaymentReq =objectPrint.toJson(insertPayment);
							
							System.out.println("insertPaymentReq" +insertPaymentReq);

							response =serviceImpl.callEwayApi(insertPaymentApi, insertPaymentReq);
							
							System.out.println("insertPaymentRes" +response);
							
							Map<String,Object> insertPaymentResult =null;
							try {	
								Map<String,Object>	insertPaymentRes =mapper.readValue(response, Map.class);
								insertPaymentResult =insertPaymentRes.get("Result")==null?null:
									mapper.readValue(mapper.writeValueAsString(insertPaymentRes.get("Result")), Map.class);
							}catch (Exception e) {
								e.printStackTrace();
								exception=e.getMessage();
							}
							
							
							if(StringUtils.isNotBlank(exception)) {
								
								flowRes.put("ErrorDesc", exception);
								return flowRes;
								//errorList.add(new Error(exception, "ErrorMsg", "101"));
							}
							
							//if(errorList.size()>0) {
								//throw new WhatsAppValidationException(errorList);

							//}
						
						String merchantRefNo =insertPaymentResult.get("MerchantReference")==null?"":
								insertPaymentResult.get("MerchantReference").toString();
						
						String quoteNo =insertPaymentResult.get("QuoteNo")==null?"":
							insertPaymentResult.get("QuoteNo").toString();
						
						log.info("RequestRefNo : "+refNo+" ||  MerchantReference : "+merchantRefNo+" || QuoteNo : "+quoteNo+" ");
				
						
						log.info("MAKE PAYMENT BLOCK END : "+new Date());
						
						Map<String,String> paymentMap =new HashMap<>();
						paymentMap.put("MerchantRefNo", merchantRefNo);
						paymentMap.put("CompanyId", "100002");
						paymentMap.put("WhatsappCode", reqq.getWhatsAppCode());
						paymentMap.put("WhtsappNo", reqq.getWhatsAppNo());
						paymentMap.put("QuoteNo", quoteNo);
						
						String payJson =objectPrint.toJson(paymentMap);
						
						String encodeReq =Base64.getEncoder().encodeToString(payJson.getBytes());
						
						String paymnetUrl =ewayMotorPaymentLink+encodeReq;
						
						System.out.println("PAYMENT LINK :" +paymnetUrl);
						
					//==============================BUYPOLICY BLOCK END=============================================
						*/
					//whatsapp BOT Response		
					Map<String,Object> map =new HashMap<>();
					map.put("referenceno", view.get(0).get("RequestReferenceNo")==null?"":view.get(0).get("RequestReferenceNo").toString());
					map.put("inceptiondate", policyStartDate);
					map.put("expirydate", policyEndDate);
					//map.put("link", paymnetUrl);
					map.put("registration", tiraResult.get("Registrationnumber")==null?"":tiraResult.get("Registrationnumber").toString());
					map.put("chassis", tiraResult.get("Chassisnumber")==null?"":tiraResult.get("Chassisnumber").toString());
					map.put("suminsured", StringUtils.isBlank(reqq.getSumInsured()) ?"N/A":reqq.getSumInsured());
					map.put("usage",tiraResult.get("Motorusage")==null?"":tiraResult.get("Motorusage").toString());
					map.put("vehtype", tiraResult.get("VehicleType")==null?"":tiraResult.get("VehicleType").toString());
					map.put("color",tiraResult.get("Color")==null?"":tiraResult.get("Color").toString());
					map.put("premium", premium.toString());
					map.put("vatamt", vatTax.toString());
					map.put("totalpremium", totalPremium.toString());
					map.put("vat", String.valueOf(vatPercentage.longValue()));
					
					flowRes.put("Response", map);
					
					return flowRes;
					
				}else {
					flowRes.put("ErrorDesc", "*SERVICE IS NOT RETURNED PREMIUM && COVERS ("+refNo+") || CONTACT ADMIN..!*");
					return flowRes;
					//errorList.add(new Error("*SERVICE IS NOT RETURNED PREMIUM && COVERS ("+refNo+") || CONTACT ADMIN..!*", "ErrorMsg", "101"));
				}
				}		
			
					
		
	return null;

	}
	
	
	public Map<String,Object> getWhatsappFlowMaster() {
		try {
			
			String token =this.aysyncThread.getEwayToken();
			
			CompletableFuture<List<Map<String,String>>> policyholderId =aysyncThread.getPolicyHolderId(token);
			CompletableFuture<List<Map<String,String>>> section =aysyncThread.getSection(token);
			CompletableFuture<List<Map<String,String>>> bodyType =aysyncThread.getBodyType(token);
			
			CompletableFuture.allOf(policyholderId,section,bodyType).join();
			

			Map<String,Object> data =new HashMap<String, Object>();
			data.put("sectionName", section.get());
			data.put("bodyType", bodyType.get());
			data.put("idType", policyholderId.get());
		
			return data;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
