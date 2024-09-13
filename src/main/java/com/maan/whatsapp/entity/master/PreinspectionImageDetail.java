package com.maan.whatsapp.entity.master;


import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
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
@Table(name = "WH_PREINSPECTION_IMAGE_DETAIL")
public class PreinspectionImageDetail {

	@Id
	@Column(name = "SNO")
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "preinspection_son")
	@SequenceGenerator(name ="preinspection_son",sequenceName = "preinspection_seq",allocationSize = 1)
	private Long sno;
	
	@Column(name = "TRANID")
	private Long tranId;
	
	@Column(name = "IMAGENAME")
	private String imageName;
	
	@Column(name = "IMAGEFILEPATH")
	private String imageFilePath;
	
	@Column(name = "ENTRY_DATE")
	private Date entry_date;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "ORIGINAL_FILE_NAME")
	private String originalFileName;
	
	@Column(name = "EXIF_IMAGE_STATUS")
	private String exifImageStatus;
	
	@Column(name = "DOC_ID")
	private Integer docId;
}
