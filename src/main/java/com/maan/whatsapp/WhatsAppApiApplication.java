package com.maan.whatsapp;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.querydsl.jpa.impl.JPAQueryFactory;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
public class WhatsAppApiApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WhatsAppApiApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(WhatsAppApiApplication.class, args);
	}
	
	String private_pem_file ="MIIFJDBWBgkqhkiG9w0BBQ0wSTAxBgkqhkiG9w0BBQwwJAQQQbUwaEFzgipcw5Bw\n"
			+ "Z3PzRAICCAAwDAYIKoZIhvcNAgkFADAUBggqhkiG9w0DBwQIDQPVzaCAGdcEggTI\n"
			+ "/4gcTProKbMoANM+qDbbMW3HCvqQocHI9C2QWGEeCSbXhQw8pBKketn5P1luyluV\n"
			+ "gcmBIgqwGcDHjOHz3ohzDv5gIKDrvO4VDZmdRKinYtcLH+7BQpoGgg8K+1i1bOTT\n"
			+ "HpSZD3uLH3rJnxR1JeWBD/6TTgvYhhNZCxAfMlrZjWt8fjECjJ9LBvKssKzbLulY\n"
			+ "fQ15FegwT5ItJFsArDavDeJfMqbFJ9v7bUP0MVzQ/zZyAz6rmNFBb2fu397tnzUe\n"
			+ "g3jX9kl+uPcyjcm5Leus0D5khSVYtN2m85Ty8+mc9ABEckt3efsnTtprUV0y1YeK\n"
			+ "Rhn+9ukoJXL/QlGQGfSlloTrjmOgYFZs6+tc975XgeUUMyC3nML91AChHEdtu8wi\n"
			+ "riBS1zPShr+Wl7rLuE5Q0OgdtJP5CsDhY24jr8TxaS8sAJWU7BrQhbiUO/TXoEHB\n"
			+ "g0XiiHvpKWYBQC5+TuFgB3w4zsyNtSY5CIfDwn6j3+fhB2pNdZ8fT+zKqtDASQfZ\n"
			+ "6avZlFI3EDGoNMSJVA7whpUiaoUOaUEy43C6BVKkKSxUawzCtuahJ47p1Tr7v9q5\n"
			+ "e8YXNbySEshcFXvcQdHPqBYTB2axnwc1GtM7PrzeQeehf+c7gH1BKI3h9HGHMaRU\n"
			+ "wbw8/rUKGHfs8+sCaLiV9Jx9RFukqF3oeEwI5bREh9xo5GPfcb5zAXRAe0qXdsDn\n"
			+ "fecyTzLYeGAQyt3yQ62e6Yj0xO9vMwU8O2EK59cVvnpO/dLBOz0nACf78JzyjusY\n"
			+ "BgDKsl359+WNs5bvRoBSPu5ROTWLTmIqGTeT+dXZR1RzXPnlOiirF+NmD/9qc+xs\n"
			+ "xR4MmnO2bksKAE1kZVdr1410gAL+MNLwnlGErblszZqTxXchkGCPXilqRW48hPTG\n"
			+ "GRV+EM7iWGzyRqJbFkVLfPQ2lgNbvrWySXv5PXJt4ZSA4jyO2H30B/3E3lmYmYjH\n"
			+ "ZHprX1y3EQQIL7k+MCQs+CERBU3alWKLZqeIHrF7uAYo9cUJH4gGemmTaSyYugpJ\n"
			+ "BPmGeUlmAL0YC8wFsFoZE0DQG/hF18a6POeNzjb/CPbY4KvtYg0XXKScbE/ht1x6\n"
			+ "AID/1jKjldRyjVssdcGTlcdTam6voZOkGJh0w/LaT1srxzcPhT7nlyxXGxAN8SJl\n"
			+ "kPy53FYgpZw42jrfPCz1NSWL66wU74RQHa3xNCopcVuIR5mGP8BoQYsumlxHpsMr\n"
			+ "AUXRzyjJjWymW3fT+qfdoAsO9h6mXMSsSZOwmLBvCaMt+bpTS3psl2VOzgXvoHQ3\n"
			+ "KnIHmzk3zoFhLqM/73inlYC6YYwI23Y6EnSx8ZUGySwlyJqMVwWZgaa3n3u9o8a+\n"
			+ "CbMJHWFguftYWoCiBqYHtNcD/Bz0S/j5nd2qo+KipxocsjF9pOM99jWxRUnx7FTu\n"
			+ "v9/ZywkV8wTXyBK+uHb6prLoGgH43oZayoh50hryFn1LHNjKG/MeVzCg7InpZaoz\n"
			+ "8+Bc75x0oCvlR7Ua4T6hVDjKpjvdKpSHVSJegwr6q5PEiDInxQRXx8VCHKvbd9E1\n"
			+ "BRkBys4ox/8FqSf0fqLo/LYBbYPfWG1UEGX4hHb0fNuR6hRfcQtt8H2dL8kFvfmy\n"
			+ "vWDcdmte443QSPATjUeo/lUDjJ3qKt5I";

	@Bean
	public Docket productApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("Card Vault Service")
				.apiInfo(apiInfo())
				.securitySchemes(Lists.newArrayList(apiKey()))
				.select()
				.apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
				.apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.cloud")))
				.apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.data.rest.webmvc")))
				// .apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("WhatsApp Api's").description(
				"WhatsApp Api's service Developed in spring boot. This is the centralized micro service. Objective of this application is to consume all services "
						+ "The APIs are web service methods based on REST protocol."
						+ " The APIs will be called using secure HTTPS protocol.\n")
				.license("Apache License Version 2.0").version("1.1.0").build();
	}

	@Bean
	public SecurityConfiguration securityInfo() {
		return SecurityConfigurationBuilder.builder()
				.clientId("WhatsAppApi Client Id")
				.clientSecret("WhatsAppApi Client Id")
				.realm("WhatsAppApi Realm")
				.appName("WhatsAppApi")
				.scopeSeparator(" ")
				.useBasicAuthenticationWithAccessCodeGrant(true)
				.build();
	}

	private ApiKey apiKey() {
		return new ApiKey("Authorization", "Authorization", "header");
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
					.allowedOriginPatterns("*")
					.allowCredentials(true);
			}
		};
	}
	
	@Bean
	public Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("WhatsAppExecutor_");
		executor.initialize();

		return executor;
	}

	@Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
	
	
	@Bean
	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper =new ObjectMapper();
		return mapper;
	}

	
	String decode_pem ="0$0V	*H\n"
			+ "\n"
			+ "0I01	*H\n"
			+ "\f0$A0hAs*\\ÐpgsD\b�0\f\b*H\n"
			+ "	�0\b*H\n"
			+ "\b\n"
			+ "͠L)(�>61m\n"
			+ "-Xa	&ׅ\f<z?Yn[Ɂ\"\n"
			+ "ǌވs` \n"
			+ "DbB\n"
			+ "Xl{zɟu%N؆Y2Zٍk|~1K򬰬.X}\n"
			+ "y0O-$[�6\n"
			+ "_2'mC1\\6r>Aog5xI~2ɹ->d%Xݦ�DrKwy'NkQ]2ՇF(%rBQc`Vl\\W3 �Gm\" R3҆˸NP\n"
			+ "cn#i/,�Ѕ;נAE{)f@.~N`|8̍&9\b~jMuOʪIٔR714ĉT\"jiA2pR),Tk\f¶':ڹ{5\\{AϨf5;>A;}A(q1T<\n"
			+ "wh}D[]xL\bDhcqst@{Jv}2O2x`Co3<;a\n"
			+ "zN;='�'�ʲ]即FR>Q95Nb*7GTs\\:(fjsl\fsnK\n"
			+ "�MdeWk׍t0Ql͚w!`^)jEn<~XlF[EK|6[I{=rmᔀ<}Ydzk_\\\b/>0$,!Mڕbf{(	zii,\n"
			+ "IyIf�Z@Eƺ<6\bb\n"
			+ "\\lO\\z�2r[,uSjntO[+7>,W\n"
			+ "\"eV 8:<,5%P4*)q[G?hA.\\G+E(ɍl[wݠ\\ĬIo	-SKzleNt7*r97΁a.?xa\bv:t,%ȚW{ƾ		a`XZKݪ⢧1}=5EIT	vކZʈy}KW0e3\\t+G>T8ʦ;*U\"^\n"
			+ "Ĉ2'WBw5('~mXmTev|ۑ_qm}/`vk^HGU*H";
	
	@Bean
	public Gson getGson() {
		Gson json = new Gson();
		return json;
	}
	
	
	//@PostConstruct
	public void encode() throws Exception {
		URL url = new URL("https://lookaside.fbsbx.com/whatsapp_business/attachments/?mid=380145248337267&ext=1712739192&hash=ATsvuOlrssIyGXIj01o86nYpr9CWt0kQD61pLyMOMDW82A") ;
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer EAAFjP8nCyKwBOwm56D8cdAL2Ar9e7ZCQaU3yn6BP23GACuGoFZBo8rrnSZBkTBuPAFGUXQh0TBvg8QGHsM8qaGkUnSN5SfRV8HUsHyKJANSmGz4l77mXOpZAdfKZCCWlwoYZCIoFtZBekPGelKODSYOPdvLPHD4XbGn6ooFK172ByksoTBmnXRnAG1Tmj7qZCHev");
        
        InputStream is =conn.getInputStream();
        
        byte array[] =IOUtils.toByteArray(is);
        
        String base64 =Base64.getEncoder().encodeToString(array);
        java.io.File file = new java.io.File("C:\\Users\\MAANSAROVAR04\\Desktop\\meatImag.txt");
        file.createNewFile();
        try {
            FileWriter myWriter = new FileWriter(file);
            myWriter.write(base64);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
          } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
        
       // System.out.println(base64);
        
	}

	
	//@PostConstruct
	private void str () {
		try {
	 		 Gson json = new Gson();
	 		 
	 		 String str ="{\"encrypted_flow_data\":\"OSYKsyhZDdUwbjbAinpqfDOfg/O/nY5N0x/EtZbNMxOxrU/jxfosq4pPsmepuMj/UljrlKmmTV9ujIv3MMj4TQOdglHtOtZ6d3bvZoMG+urL1VQXTnRJMa/o0WEq5CAD5MFGqV/lJajTJBQ5eU4e/kukrqPfK4sNFLt5Mxssn8kI3eobd5tSARGVsyqfZ+39ru7/DG/UAGcOExBtLHbwqk5vqXGfsZcQ+GwFyYC1r/RrF/nOMEZgk2cyOjM4axbiXIPmj/Ii3K5tAtGIgoag+gdmH978TlUX2nCfhdb9aeUItVTn3kG7a7tHsP47bhOrQtBjS4OIJ/+DCFmOJjnz62glATE1yZRbYMERGBNZHXLA3gsvPLQtIcudHUT6ORkWCNQ+3ZkgB8sLjX/HPbeeGwxoe98/sLUxgWsZEHadq+Qwae4sRir/ekg9CHaK\",\"encrypted_aes_key\":\"WszzrjLraqxjS73Dy31h0hzkLUqBi3BoIu2+Vv1I19dhm4M018HvFWzUw+dzMK3baRD/bOZhHcljbJKjV8vB/iCfS+zLWX/LIuYeG77dr7uzuKUN1pTGCEINY/7F+XTGhVzdT+O0Qx21P+r1MhrXlo4MzdEkKNgLyuCkNujToxhSRFPT/oF2eKjfFFMr05zUT2oxc7Oxc1o4bgwLRmMACE0/io4X4FbS4FE8QIvQ/bTXvHRMZB4z1TVsV5id9EjHswcYEzSoE8d2LbGFYGfbMvtr1zjzJnUrx5Pz1X8cWa/NicWgPgbLAKvYJ4NSlG/VuC0Ea0jGsYEncAitftgHOQ\\u003d\\u003d\",\"initial_vector\":\"Mi8HPP/vyl8DcQmwNICQtQ\\u003d\\u003d\"}";
	         JSONObject body  = new JSONObject(str);

	 		 String encrypted_aes_key = body.getString("encrypted_aes_key");
	         String encrypted_flow_data = body.getString("encrypted_flow_data");
	         String initial_vector = body.getString("initial_vector");
	         PrivateKey privateKey = getPrivateKey(private_pem_file, "");
	         byte[] decryptedAesKey = null;
	         try {
	             // decrypt AES key created by client
	             decryptedAesKey = decryptPrivateKey(privateKey, Base64.getDecoder().decode(encrypted_aes_key));
	         } catch (Exception error) {
	             System.err.println(error);
	             /*
	              * Failed to decrypt. Please verify your private key.
	              * If you change your public key. You need to return HTTP status code 421 to refresh the public key on the client
	              */
	             throw new Exception("Failed to decrypt the request. Please verify your private key.");
	         }

	         // decrypt flow data
	         byte[] flowDataBuffer = Base64.getDecoder().decode(encrypted_flow_data);
	         byte[] initialVectorBuffer = Base64.getDecoder().decode(initial_vector);

	         final int TAG_LENGTH = 16;
	         byte[] encrypted_flow_data_body = new byte[flowDataBuffer.length - TAG_LENGTH];
	         byte[] encrypted_flow_data_tag = new byte[TAG_LENGTH];
	         System.arraycopy(flowDataBuffer, 0, encrypted_flow_data_body, 0, flowDataBuffer.length - TAG_LENGTH);
	         System.arraycopy(flowDataBuffer, flowDataBuffer.length - TAG_LENGTH, encrypted_flow_data_tag, 0, TAG_LENGTH);

	         Cipher decipher = Cipher.getInstance("AES/GCM/NoPadding");
	         GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, initialVectorBuffer);
	         decipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptedAesKey, "AES"), gcmParameterSpec);
	         decipher.updateAAD(new byte[0]);
	         byte[] decryptedBytes = decipher.doFinal(encrypted_flow_data_body);

	         String decryptedJSONString = new String(decryptedBytes, "UTF-8");

	         JSONObject decryptedBody = new JSONObject(decryptedJSONString);
	   
	
	         System.out.println(json.toJson(decryptedBody));
	         
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 private static PrivateKey getPrivateKey(String privatePem, String passphrase) throws NoSuchAlgorithmException, InvalidKeySpecException {
		 byte[] decodedBytes = Base64.getDecoder().decode(privatePem);
		 
		 PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedBytes);
	        
	        return java.security.KeyFactory.getInstance("RSA").generatePrivate(keySpec);
	    }

	    private static byte[] decryptPrivateKey(PrivateKey privateKey, byte[] encryptedAesKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
	        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
	        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
	        cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
	        return cipher.doFinal(encryptedAesKey);
	    }
}
