package com.maan.whatsapp.entity.master;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "WH_PREINSPECTION_DATA_DETAIL")
public class PreinspectionDataDetail {
	
	
	@Id
	@Column(name = "TRANID")
	private Long tranId;
	
	@Column(name = "REGISTRAIONNO")
	private String registrationNo;
	
	@Column(name = "CHASSISNO")
	private String chassisNo;
	
	@Column(name = "MOBILENO")
	private String mobileNo;
	
	@Column(name = "ENTRY_DATE")
	private Date entry_date;
	
	@Column(name = "DOCUMENT_TYPE")
	private String documnetType;
	
	@Column(name = "STATUS")
	private String status;
	
	

}
