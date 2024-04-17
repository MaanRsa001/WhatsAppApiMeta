package com.maan.whatsapp.request.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClaimDocReq {
	
	@JsonProperty("InsuranceId")
    private String companyId;
    
    @JsonProperty("Description")
    private String docName;
    
    @JsonProperty("DocTypeId")
    private String docId;
    
    @JsonProperty("ClaimNo")
    private String claimNo;
    
    @JsonProperty("PartyNo")
    private String partyId;
    
    @JsonProperty("LossId")
    private String lossId;
    
    @JsonProperty("file")
    private String file;
    
    @JsonProperty("FileName")
    private String filename;
    
}
