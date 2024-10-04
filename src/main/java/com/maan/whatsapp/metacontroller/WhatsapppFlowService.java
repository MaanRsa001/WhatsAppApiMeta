package com.maan.whatsapp.metacontroller;

import java.util.Map;

public interface WhatsapppFlowService {

	String claimIntimation(Map<String, Object> data);

	String createShortTermPolicy(Map<String, Object> data);

	Map<String, Object> quotation_flow_screen_data();

	String createVehicleQuotation(Map<String, Object> request);

	Map<String, Object> stp_flow_screen_data();

	Map<String, Object> claimIntimateScreenData();

	String inalipaClaimIntimation(Map<String, Object> request);

	Object getInalipaClaimTypes();

	Map<String, Object> InalipaIntimateScreenData();

	String preinspectionUpload(Map<String, Object> image);

	Map<String, Object> preinspectionScreenData(String mobile_no);
	
	String shortTermPolicy(Map<String, Object> request);

	String createQuote(Map<String, Object> request);



}
