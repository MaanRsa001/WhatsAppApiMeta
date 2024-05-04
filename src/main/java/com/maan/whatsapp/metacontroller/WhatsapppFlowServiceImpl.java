package com.maan.whatsapp.metacontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.claimintimation.ClaimIntimationEntity;
import com.maan.whatsapp.claimintimation.ClaimIntimationServiceImpl;
import com.maan.whatsapp.service.common.CommonService;

@Service
public class WhatsapppFlowServiceImpl implements WhatsapppFlowService{
	
	Logger log = LogManager.getLogger(WhatsapppFlowServiceImpl.class);
	
	ObjectMapper mapper = new ObjectMapper();
	
	public static Gson printReq =new Gson();
	
	@Autowired
	private CommonService cs;
	
	@Autowired
	private ClaimIntimationServiceImpl apicall;
	

	@SuppressWarnings("unchecked")
	@Override
	public String claimIntimation(Map<String, Object> data) {
		String response ="";
		String errorDesc ="";
		try {
			
			String type =data.get("type")==null?"":data.get("type").toString();
			
			if("VALIDATE_REGISTRATION_NO".equalsIgnoreCase(type)) {
				
				Map<String,Object> map = new HashMap<String, Object>();
				String registration_no =data.get("registration_no")==null?"":data.get("registration_no").toString();
				map.put("ChassisNo", registration_no);
				map.put("InsuranceId", "100002");
				String api = cs.getwebserviceurlProperty().getProperty("get.policy.details.bychassisNo");
			
				String request = printReq.toJson(map);
				log.info("Claim Intimation API " + api);
				log.info("Claim Intimation Policy Request " + request);
				response =apicall.callApi(api, request);
				log.info("Claim Intimation response " + response);
	
				mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
				List<Map<String, Object>> claimList = mapper.readValue(response, List.class);

				if (!CollectionUtils.isEmpty(claimList)) {
					List<ClaimIntimationEntity> saveList = new ArrayList<ClaimIntimationEntity>(5);
					List<Map<String, Object>> errorList = (List<Map<String, Object>>) claimList.get(0)
							.get("Errors");
					Boolean errorStatus = CollectionUtils.isEmpty(errorList) ? true : false;
				
					if (errorStatus) {
						String policy_no = "",conatct_person_name="";
						List<Map<String,Object>> return_list =new ArrayList<>();
						
						for (Map<String, Object> dataReq : claimList) {
							Map<String, Object> vd = (Map<String, Object>) dataReq.get("VehicleInfo");
							Map<String, Object> pd = (Map<String, Object>) dataReq.get("PolicyInfo");
							
							String vehicle_model_desc =	vd.get("Vehiclemodeldesc") == null ? ""
									: vd.get("Vehiclemodeldesc").toString();
						
							String chassis_no =	vd.get("ChassisNo") == null ? ""
								: vd.get("ChassisNo").toString();
					
							 policy_no =pd.get("PolicyNo") == null ? "" : pd.get("PolicyNo").toString();
							
							 conatct_person_name =pd.get("Contactpername") == null ? "" : pd.get("Contactpername").toString();
							
							Map<String,Object> r = new HashMap<>();
							r.put("id", policy_no+"/"+registration_no+"/"+chassis_no);
							r.put("title",vehicle_model_desc+"/"+chassis_no+"/"+registration_no);
							
							return_list.add(r);
						}
						
						

					} else {
						errorDesc = "No Record Found..Please try again";
					}
				} else {
					errorDesc = "No Record Found..Please try again..";
				}
				
				
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}


	@Override
	public String createShortTermPolicy(Map<String, Object> data) {
		String response ="";
		try {
			
			String component_action =data.get("component_action")==null?"":data.get("component_action").toString();
			
			if("SAVE_CUSTOMER".equalsIgnoreCase(component_action)) {
				String chassis_no =data.get("chassis_no")==null?"":data.get("chassis_no").toString();
				String body_type =data.get("body_type")==null?"":data.get("body_type").toString();
				String make =data.get("make")==null?"":data.get("make").toString();
				String model =data.get("model")==null?"":data.get("model").toString();
				String engine_capacity =data.get("engine_capacity")==null?"":data.get("engine_capacity").toString();
				String manufacture_year =data.get("manufacture_year")==null?"":data.get("manufacture_year").toString();
				String fuel_used =data.get("fuel_used")==null?"":data.get("fuel_used").toString();
				String vehicle_color =data.get("vehicle_color")==null?"":data.get("vehicle_color").toString();
				String vehicle_usage =data.get("vehicle_usage")==null?"":data.get("vehicle_usage").toString();
				String motor_category =data.get("motor_category")==null?"":data.get("motor_category").toString();
				String seating_capacity =data.get("seating_capacity")==null?"":data.get("seating_capacity").toString();
				String isbroker =data.get("isbroker")==null?"":data.get("isbroker").toString();
				String broker_loginid =data.get("broker_loginid")==null?"":data.get("broker_loginid").toString();
				
				
				
			
			
			}
		
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return response;
	}

}
