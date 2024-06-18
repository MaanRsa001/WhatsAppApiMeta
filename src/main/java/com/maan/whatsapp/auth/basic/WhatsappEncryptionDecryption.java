package com.maan.whatsapp.auth.basic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.maan.whatsapp.meta.MetaEncryptDecryptRes;
import com.maan.whatsapp.service.common.CommonService;

@Component
//@PropertySource("classpath:WebServiceUrl.properties")
public class WhatsappEncryptionDecryption {
		
	
	
	private static String metaSecurityKeyFilePath ="";
	
	static {
		CommonService cs = new CommonService();
		metaSecurityKeyFilePath =cs.getwebserviceurlProperty().getProperty("metaSecurityKeyFilePath").trim();
	}
	
	private static class DecryptionInfo {
        public final String clearPayload;
        public final byte[] clearAesKey;

        public DecryptionInfo(String clearPayload, byte[] clearAesKey) {
            this.clearPayload = clearPayload;
            this.clearAesKey = clearAesKey;
        }
        
        
    }

    private static final int AES_KEY_SIZE = 128;
    private static final String KEY_GENERATOR_ALGORITHM = "AES";
    private static final String AES_CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final String RSA_ENCRYPT_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final String RSA_MD_NAME = "SHA-256";
    private static final String RSA_MGF = "MGF1";

     public static String whatsappEncryptionDecryption(Map<Object,Object> req) throws IOException {
            String response ="";
            int responseCode;
            try {
            	
            	org.json.JSONObject requestJson = new org.json.JSONObject(req);
    	 		 
                final byte[] encrypted_flow_data = Base64.getDecoder().decode((String) requestJson.get("encrypted_flow_data"));
                final byte[] encrypted_aes_key = Base64.getDecoder().decode((String) requestJson.get("encrypted_aes_key"));
                final byte[] initial_vector = Base64.getDecoder().decode((String) requestJson.get("initial_vector"));
                
                final DecryptionInfo decryptionInfo = decryptRequestPayload(encrypted_flow_data, encrypted_aes_key, initial_vector);
                final JSONObject clearRequestData = new JSONObject(decryptionInfo.clearPayload); 
               
         
                Map<String,Object> data = new HashMap<String, Object>();
                data.put("name", "MaansarovarTech");
                data.put("email", "jbaskar96@gmail.com");
                Map<String,Object> enRes = new HashMap<String, Object>();
                enRes.put("version", clearRequestData.get("version"));
                enRes.put("screen", "QUOTE_RESPONSE");
                enRes.put("data", data);
                String clearResponse= new Gson().toJson(enRes);
                System.out.println("RETURN RESPONSE ==>"+ clearResponse);
                
                response = encryptAndEncodeResponse(clearResponse, decryptionInfo.clearAesKey, flipIv(initial_vector));
               
                responseCode = 200;
            } catch (Exception ex) {
                ex.printStackTrace();
                responseCode = 500;
            }
            
            return response;
        
    }

    private static DecryptionInfo decryptRequestPayload(byte[] encrypted_flow_data, byte[] encrypted_aes_key, byte[] initial_vector) throws Exception {

    	
    	RSAPrivateKey privateKey = readPrivateKeyFromPkcs8UnencryptedPem(metaSecurityKeyFilePath); // for local
    	
        //final RSAPrivateKey privateKey = readPrivateKeyFromPkcs8UnencryptedPem("/home/ewayportal/commonpath/privatenew.pem"); // for live and uat

    	final byte[] aes_key = decryptUsingRSA(privateKey, encrypted_aes_key);
        return new DecryptionInfo(decryptUsingAES(encrypted_flow_data, aes_key, initial_vector), aes_key);
    }

    private static String decryptUsingAES(final byte[] encrypted_payload, final byte[] aes_key, final byte[] iv) throws GeneralSecurityException {
        final GCMParameterSpec paramSpec = new GCMParameterSpec(AES_KEY_SIZE, iv);
        final Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aes_key, KEY_GENERATOR_ALGORITHM), paramSpec);
        final byte[] data = cipher.doFinal(encrypted_payload);
        return new String(data, StandardCharsets.UTF_8);
    }

    private static byte[] decryptUsingRSA(final RSAPrivateKey privateKey, final byte[] payload) throws GeneralSecurityException {
        final Cipher cipher = Cipher.getInstance(RSA_ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey, new OAEPParameterSpec(RSA_MD_NAME, RSA_MGF, MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));
        byte[] cipherText = cipher.doFinal(payload);

        return cipherText;
    }
  
    private static RSAPrivateKey readPrivateKeyFromPkcs8UnencryptedPem(String filePath) throws Exception {
        final String prefix = "-----BEGIN PRIVATE KEY-----";
        final String suffix = "-----END PRIVATE KEY-----";
        String key = new String(Files.readAllBytes(new File(filePath).toPath()), StandardCharsets.UTF_8);
       // KeyStore.getInstance(new File(getImagePath()+"com\\maan\\eway\\config\\certificate.pfx"), password.toCharArray());
        if (!key.contains(prefix)) {
            throw new IllegalStateException("Expecting unencrypted private key in PKCS8 format starting with " + prefix);
        }
        String privateKeyPEM = key.replace(prefix, "").replaceAll("[\\r\\n]", "").replace(suffix, "");
        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        PrivateKey privateKsy = keyFactory.generatePrivate(keySpec);
        return (RSAPrivateKey) privateKsy;
    }

    private static String encryptAndEncodeResponse(final String clearResponse, final byte[] aes_key, final byte[] iv) throws GeneralSecurityException {
        final GCMParameterSpec paramSpec = new GCMParameterSpec(AES_KEY_SIZE, iv);
        final Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aes_key, KEY_GENERATOR_ALGORITHM), paramSpec);
        final byte[] encryptedData = cipher.doFinal(clearResponse.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    private static byte[] flipIv(final byte[] iv) {
        final byte[] result = new byte[iv.length];
        for (int i = 0; i < iv.length; i++) {
            result[i] = (byte) (iv[i] ^ 0xFF);
        }
        return result;
    }
    
    public static  MetaEncryptDecryptRes metaDecryption(Map<String,Object> req ) {
    	MetaEncryptDecryptRes response = new MetaEncryptDecryptRes();
    	try {
    		org.json.JSONObject requestJson = new org.json.JSONObject(req);
	 		 
            final byte[] encrypted_flow_data = Base64.getDecoder().decode((String) requestJson.get("encrypted_flow_data"));
            final byte[] encrypted_aes_key = Base64.getDecoder().decode((String) requestJson.get("encrypted_aes_key"));
            final byte[] initial_vector = Base64.getDecoder().decode((String) requestJson.get("initial_vector"));
            
            final DecryptionInfo decryptionInfo = decryptRequestPayload(encrypted_flow_data, encrypted_aes_key, initial_vector);
            response.setEncrypted_aes_key(decryptionInfo.clearAesKey);
            response.setInitial_vector(flipIv(initial_vector));
            response.setEncrypted_flow_data(decryptionInfo.clearPayload);
            return response;
    	}catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
    }
    
    
    public static String metaEncryption(MetaEncryptDecryptRes req ) {
    	try {
    		
    	   String response = encryptAndEncodeResponse(req.getEncrypted_flow_data(), req.getEncrypted_aes_key(), req.getInitial_vector());
           return response;
    	}catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
    }


}
