package com.maan.whatsapp.meta;

import lombok.Data;

@Data
public class MetaEncryptDecryptRes {
	
	private String encrypted_flow_data;
	
	private byte[] encrypted_aes_key;
	
	private byte[] initial_vector;

}
