package com.maan.whatsapp.entity.master;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "WHATSAPP_TRANSACTION_DETAILS")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class WhatsappClaimDocumentSetup {
		    
	    @EmbeddedId
	    private WhatsappClaimDocumentSetupPk claimPk;
	    
	    @Column(name = "PROCESS_YN")
	    private String processYn;
	    
	    @Column(name = "STATUS")
	    private String status;

	    @Column(name = "ENTRY_DATE")
	    private Date entryDate;
	    
	    @Column(name = "PARTY_ID")
	    private String partyId;
	    
	    @Column(name = "LOSS_ID")
	    private String lossId;
	    
	    @Column(name = "ISSENT_YN")
	    private String sentYn;
	    
	    @Column(name = "FILE_YN")
	    private String fileYn;
	    
	    @Column(name = "DOC_DESC")
	    private String docDesc;
	    
	    @Column(name = "DOC_NAME")
	    private String docName;
	    
		@Column(name = "CLAIM_NO")
		private String claimNo;
		
		@Column(name = "REMARKS")
		private String remarks;
	   
	    
}
