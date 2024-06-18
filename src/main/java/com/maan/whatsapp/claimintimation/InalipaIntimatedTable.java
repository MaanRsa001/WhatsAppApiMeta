package com.maan.whatsapp.claimintimation;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "whatsapp_inalipa_intimated_table")
@DynamicInsert
@DynamicUpdate
public class InalipaIntimatedTable implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CLAIM_NO")
	private String claimNo;
	
	@Column(name = "POLICY_NO")
	private String policyNo;
	
	@Column(name = "POLICY_START_DATE")
	private Date policyStartDate;
	
	@Column(name = "POLICY_END_DATE")
	private Date policyEndDate;
	
	@Column(name = "MOBILE_NO")
	private Long mobileNo;
	
	@Column(name = "ACCIDENT_DATE")
	private Date accidentDate;
	
	@Column(name = "CLAIM_TYPE")
	private String ClaimType;
	
	@Column(name = "INTIMATED_DATE")
	private Date intimatedDate;
	
	@Column(name = "INTIMATED_MOBILE_NO")
	private Integer intimatedMobileNo;
	
	@Column(name = "CLAIM_ID")
	private String claimId;
	
}
