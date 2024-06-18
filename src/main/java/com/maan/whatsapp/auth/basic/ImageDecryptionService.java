
package com.maan.whatsapp.auth.basic;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class ImageDecryptionService {

	    
	    static {
	        Security.addProvider(new BouncyCastleProvider());
	    }
	
	    public byte[] decryptMedia(Map<String, Object> image) throws Exception {
	    	
	    	String cdn_url =image.get("cdn_url")==null?"":image.get("cdn_url").toString();
			Map<String, Object> encrypted_metadata =image.get("encryption_metadata")==null?null:(Map<String, Object>)image.get("encryption_metadata");
			String encryption_key =encrypted_metadata.get("encryption_key")==null?"":encrypted_metadata.get("encryption_key").toString();
			String hmac_key =encrypted_metadata.get("hmac_key")==null?"":encrypted_metadata.get("hmac_key").toString();
			String iv_base64 =encrypted_metadata.get("iv")==null?"":encrypted_metadata.get("iv").toString();
			//String plaintext_hash =encrypted_metadata.get("plaintext_hash")==null?"":encrypted_metadata.get("plaintext_hash").toString();
			String encrypted_hash =encrypted_metadata.get("encrypted_hash")==null?"":encrypted_metadata.get("encrypted_hash").toString();
		  
	        byte[] hmacKey = Base64.getDecoder().decode(hmac_key);
	        byte[] iv = Base64.getDecoder().decode(iv_base64);
	        byte[] cipherKey = Base64.getDecoder().decode(encryption_key);
	                
	        byte[] cdnFile = downloadFile(cdn_url);
	        byte[] ciphertext = Arrays.copyOfRange(cdnFile, 0, cdnFile.length - 10);
	        byte[] hmac10 = Arrays.copyOfRange(cdnFile, cdnFile.length - 10, cdnFile.length);
	
	        if (!validateSHA256(cdnFile, encrypted_hash)) {
	            throw new IllegalArgumentException("SHA256 hash of the CDN file does not match the expected value.");
	        }
	
	        if (!validateHMAC(ciphertext, hmacKey, iv, hmac10)) {
	            throw new IllegalArgumentException("HMAC validation failed.");
	        }
	
	        byte[] decrypted = decrypt(ciphertext, cipherKey, iv);
	        byte[] decryptedMedia = removePKCS7Padding(decrypted);
	
	       
	        //if (!validateSHA256(decryptedMedia, plaintextHash)) {
	          //  throw new IllegalArgumentException("SHA256 hash of the decrypted media does not match the expected value.");
	       // }
	
	        
			return decryptedMedia;
	    
	    }
	        
	        public static byte[] downloadFile(String url) {
	            RestTemplate restTemplate = new RestTemplate();
	            return restTemplate.getForObject(url, byte[].class);
	        }
	
	        public static boolean validateSHA256(byte[] data, String expectedHash) throws Exception {
	            MessageDigest digest = MessageDigest.getInstance("SHA-256");
	            byte[] hash = digest.digest(data);
	            String hashHex = Base64.getEncoder().encodeToString(hash);
	            return hashHex.equalsIgnoreCase(expectedHash);
	        }
	
	        public static boolean validateHMAC(byte[] data, byte[] hmacKey, byte[] iv, byte[] expectedHmac) throws Exception {
	            Mac mac = Mac.getInstance("HmacSHA256", "BC");
	            SecretKeySpec keySpec = new SecretKeySpec(hmacKey, "HmacSHA256");
	            mac.init(keySpec);
	            mac.update(iv);
	            mac.update(data);
	            byte[] hmac = mac.doFinal();
	            return Arrays.equals(Arrays.copyOf(hmac, 10), expectedHmac);
	        }
	
	        public static byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv) throws Exception {
	            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
	            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
	            IvParameterSpec ivSpec = new IvParameterSpec(iv);
	            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
	            return cipher.doFinal(ciphertext);
	        }
	
	        public static byte[] removePKCS7Padding(byte[] decrypted) {
	            int paddingLength = decrypted[decrypted.length - 1];
	            return Arrays.copyOf(decrypted, decrypted.length - paddingLength);
	        }
	
	        public static String base64ToHex(String base64String) {
	            // Decode the Base64 string to get the byte array
	            byte[] byteArray = Base64.getDecoder().decode(base64String);
	            
	            // Convert the byte array to a hexadecimal string
	            return Hex.encodeHexString(byteArray);
	        }
	       
}