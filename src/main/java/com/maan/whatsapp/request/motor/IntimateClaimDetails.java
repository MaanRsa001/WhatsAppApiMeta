package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntimateClaimDetails {
	
	@JsonProperty("Error")
	private String error;
	@JsonProperty("OtpId")
	private String OtpId;
	@JsonProperty("Otp")
	private String Otp;
	@JsonProperty("PolicyNo")
	private String PolicyNo;
	@JsonProperty("PolicyFromDate")
	private String PolicyFromDate;
	@JsonProperty("PolicyToDate")
	private String PolicyToDate;
	@JsonProperty("InsuredName")
	private String InsuredName;
	@JsonProperty("Address1")
	private String Address1;
	@JsonProperty("Address2")
	private String Address2;
	@JsonProperty("Address3")
	private String Address3;
	@JsonProperty("VehiclePlateNo")
	private String VehiclePlateNo;
	@JsonProperty("VehiclePlateChar")
	private String VehiclePlateChar;
	@JsonProperty("ChassisNo")
	private String ChassisNo;
	@JsonProperty("EngineNo")
	private String EngineNo;
	@JsonProperty("DateOfRegistration")
	private String DateOfRegistration;
	@JsonProperty("VehicleType")
	private String VehicleType;
	@JsonProperty("DriverName")
	private String DriverName;
	@JsonProperty("DriverDOB")
	private String DriverDOB;
	@JsonProperty("LicenseType")
	private String LicenseType;
	@JsonProperty("MakeModel")
	private String MakeModel;
	@JsonProperty("ClaimReqRefNo")
	private String ClaimReqRefNo;
	@JsonProperty("TypeofInsurance")
	private String TypeofInsurance;
	@JsonProperty("PlateType")
	private String PlateType;
	@JsonProperty("NatureOfLoss")
	private String NatureOfLoss;
	@JsonProperty("CauseOfLoss")
	private String CauseOfLoss;
	@JsonProperty("AccidentDate")
	private String AccidentDate;
	@JsonProperty("AccidentPlace")
	private String AccidentPlace;
	@JsonProperty("AccidentTime")
	private String AccidentTime;
	@JsonProperty("VehicleSpeed")
	private String VehicleSpeed;
	@JsonProperty("AccidentDesc")
	private String AccidentDesc;
	@JsonProperty("DamagesOfAccident")
	private String DamagesOfAccident;
	@JsonProperty("WhereInspected")
	private String WhereInspected;
	@JsonProperty("AnyDamageTpYN")
	private String AnyDamageTpYN;
	@JsonProperty("PropertyDamageDesc")
	private String PropertyDamageDesc;
	@JsonProperty("PersonInjureYN")
	private String PersonInjureYN;
	@JsonProperty("PropertyDamageYN")
	private String PropertyDamageYN;
	@JsonProperty("AccidentType")
	private String AccidentType;
	@JsonProperty("AccidentTypeOthers")
	private String AccidentTypeOthers;
	@JsonProperty("EmailId")
	private String EmailId;
	@JsonProperty("ClaimStatus")
	private String ClaimStatus;
	@JsonProperty("ClaimRemarks")
	private String ClaimRemarks;
	@JsonProperty("driverofcar")
	private String driverofcar;
	@JsonProperty("mobileNo")
	private String mobileNo;
	@JsonProperty("notifyDate")
	private String notifyDate;
	@JsonProperty("DriverLicenseNo")
	private String DriverLicenseNo;
	@JsonProperty("LicenseCategory")
	private String LicenseCategory;
	@JsonProperty("Sex")
	private String Sex;
	@JsonProperty("Nationality")
	private String Nationality;
	@JsonProperty("OtherMajorAccidentType")
	private String OtherMajorAccidentType;
	@JsonProperty("DamagedVehilces")
	private String DamagedVehilces;
	@JsonProperty("UpdatedBy")
	private String UpdatedBy;
	@JsonProperty("UpdatedDate")
	private String UpdatedDate;
	@JsonProperty("ClaimBranch")
	private String ClaimBranch;
	@JsonProperty("ClaimRegNo")
	private String ClaimRegNo;
	@JsonProperty("TPDamageReason")
	private String TPDamageReason;
	@JsonProperty("TPDamageReasonOthers")
	private String TPDamageReasonOthers;
	@JsonProperty("TpDamVehicleCountry")
	private String TpDamVehicleCountry;
	@JsonProperty("TinyUrl")
	private String TinyUrl;
	
	
	

}
