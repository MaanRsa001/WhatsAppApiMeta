package com.maan.whatsapp.claimintimation;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ClaimIntimationEntityId.class)
@Table(name = "WAHTSAPP_CLAIM_INTIMATION_SAVE_RES")
@Builder
public class ClaimIntimationEntity {
  
	
	@Id
	@Column(name = "MOBILE_NO")
	private String mobileNo;
	
	@Id
	@Column(name = "SERIAL_NO")
	private Long serialNo;
	
	@Id
	@Column(name = "BOT_OPTION_NO")
	private String botOptionNo;
	
	//
	@Column(name = "POLICY_NO")
	private String policyNo;
	
	@Column(name = "API_TYPE")
	private String apiType;
	
	@Column(name = "VEHICLE_TYPE")
	private String vehiType;
	
	@Column(name = "VEHICLE_MODEL")
	private String vehiModel;
	
	@Column(name = "PLATE_NO")
	private String plateNo;
	
	@Column(name = "CHASSIS_NO")
	private String chassisNo;
	
	@Column(name = "SUM_INSURED")
	private String sumInsured;
	
	@Column(name = "VEH_REG_NO")
	private String vehRegNo;
	
	@Column(name = "MANUFACTURE_YEAR")
	private String manufactureYear;
	
	@Column(name = "CONTACT_PERSON_NAME")
	private String contactPersonName;
	
	@Column(name = "POLICY_FROM")
	private String policyFrom;
	
	@Column(name = "POLICY_TO")
	private String policyTo;
	
	@Column(name = "CIVIL_ID")
	private String civilId;
	
	@Column(name = "PRODUCT")
	private String product;
	
	@Column(name = "ENTRY_DATE")
	private Date entryDate;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name ="CODE")
	private String code;
	
	@Column(name ="CODE_DESC")
	private String codeDesc;
	
	@Column(name ="INSURANCE_ID")
	private String insuranceId;
	
	@Column(name ="BRANCH_CODE")
	private String branchCode;
	
	@Column(name ="REGION_CODE")
	private String regionCode;
	
	@Column(name ="DIVN_CODE")
	private String divnCode;
	
	// 
	
	@Column(name ="CLAIM_REF_NO")
	private String claimRefNo;
	
	@Column(name ="CLAIM_STATUS")
	private String claimStatus;
	
	@Column(name ="ACCIDENT_DATE")
	private String accidentDate;
	
}


