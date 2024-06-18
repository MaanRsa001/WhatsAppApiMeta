package com.maan.whatsapp.service.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.claimintimation.TokenResponse;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class CommonService {

	private Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private CommonService cs;
	
	private OkHttpClient httpClient =new OkHttpClient.Builder()
			.readTimeout(60,TimeUnit.SECONDS)
			.connectTimeout(60, TimeUnit.SECONDS)
			.build();
	
	private okhttp3.MediaType mediaType =okhttp3.MediaType.parse("application/json");
	
	private ObjectMapper mapper = new ObjectMapper();
	
	public String emailValidate(final String mailId) {
		String returnval = "";
		try {
			final String mailid = mailId.trim();
			if (mailid.length() > 0) {
				final char charac[] = mailid.toCharArray();
				if (!Character.isLetter(charac[0]) || mailid.contains(" ")) {
					returnval = "invalid";
				} else if (mailid.indexOf('@') == -1) {
					returnval = "invalid";
				} else if ((mailid.substring(0, mailid.indexOf('@'))).length() < 2
						|| (mailid.substring(mailid.indexOf('@') + 1)).length() < 3
						|| (mailid.substring(mailid.indexOf('@') + 1)).indexOf('.') == -1) {
					returnval = "invalid";
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return returnval;
	}

	public Date formatdate(String date) {
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
			Date d = sdf.parse(date);

			return d;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public String formatdatewithtime(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			Date d = sdf.parse(date);
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.S");
			date = sdf2.format(d);
		} catch (Exception e) {
			log.error(e);
		}
		return date;
	}

	public Date formatdate2(String date) {
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			date = sdf.format(sdf.parse(date));
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.S");
			d = sdf2.parse(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public String formatdatewithouttime(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			Date d = sdf.parse(date);
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			date = sdf2.format(d);
		} catch (Exception e) {
			log.error(e);
		}
		return date;
	}

	public String formatdatewithouttime(Date date) {
		String dates = "";
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			dates = sdf2.format(date);
		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}

	public Date formatdatewithouttime2(String date) {
		Date d = null;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
			d = sdf2.parse(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public String formatdatewithtime2(Date date) {
		String dates = "";
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy kk.mm.ss");
			dates = sdf2.format(date);
		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}

	public String formatdatewithtime3(Date date) {
		String dates = "";
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy_kk.mm.ss");
			dates = sdf2.format(date);
		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}
	
	public String formatdatewithtime4(Date date) {
		String dates = "";
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
			dates = sdf2.format(date);
		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}

	public String formatdatewithtime4(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			Date d = sdf.parse(date);
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
			date = sdf2.format(d);
		} catch (Exception e) {
			log.error(e);
		}
		return date;
	}

	public Date formatdatewithouttime3(String date) {
		Date d = null;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
			d = sdf2.parse(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public Date formatdatewithouttime4(String date) {
		Date d = null;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
			d = sdf2.parse(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public String formatdate_ddMMMyyyy(Date date) {
		String d = null;
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
			d = sdf2.format(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public String ddMMMyyyy(String date) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date d = sdf.parse(date);
			SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MMM-yyyy");
			date = sdf2.format(d);
		} catch (Exception e) {
			log.error(e);
		}
		return date;
	}

	public Date formatdatewithouttime5(String date) {
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			d = sdf.parse(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public Date formatdatewithouttime6(String date) {
		Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			d = sdf.parse(date);
		} catch (Exception e) {
			log.error(e);
		}
		return d;
	}

	public String formatdatewithMMM(Date date) {
		String dates = "";
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMMyyyy");
			dates = sdf2.format(date);
		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}

	public String getDateFormat(String date) {
		try {
			ParsePosition pos = new ParsePosition(0);
			SimpleDateFormat originalFormatter = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat newFormatter = new SimpleDateFormat("dd/MM/yyyy");
			return newFormatter.format(originalFormatter.parse(date, pos));
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	public String claimDateFormat(String date) {
		String dates = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'+04:00'");
			Date parse = sdf.parse(date);

			SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

			dates = sdf2.format(parse);
		} catch (Exception e) {
			log.error(e);
		}
		return dates;
	}

	public Date watiDateFormat(String date) {
		Date parse = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			//sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

			parse = sdf.parse(date);

		} catch (Exception e) {
			log.error(e);
		}
		return parse;
	}

	public String fileDateFormat() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyssSSSSSS");
			String format = sdf.format(new Date());

			return format;
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	public Date particular_date(int count) {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, count);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String date = sdf.format(cal.getTime());
		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			log.error(e);
		}
		return d;
	}

	public String addDays(String oldDate, int day) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Calendar c = Calendar.getInstance();
			try {
				// Setting the date to the given date
				c.setTime(sdf.parse(oldDate));
			} catch (ParseException e) {
				log.error(e);
			}
			// Number of Days to add
			c.add(Calendar.DAY_OF_MONTH, day);
			// Date after adding the days to the given date
			String newDate = sdf.format(c.getTime());
			return newDate;
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	public Date addDays(int day) {
		try {
			Calendar c = Calendar.getInstance();
			Date date = null;
			try {
				// Setting the date to the given date
				c.setTime(new Date());
			} catch (Exception e) {
				log.error(e);
			}
			// Number of Days to add
			c.add(Calendar.DAY_OF_MONTH, day);
			// Date after adding the days to the given date
			date = c.getTime();
			return date;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public Date addMinutes(int minutes) {
		try {
			Date date = null;

			Calendar calendar = Calendar.getInstance();
			// Add minutes to current date
			calendar.add(Calendar.MINUTE, minutes);

			date = calendar.getTime();

			return date;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	public Date addMinutes(Date inpdate, int minutes) {
		try {
			Date date = null;

			Calendar calendar = Calendar.getInstance();
			
			calendar.setTime(inpdate);
			// Add minutes to current date
			calendar.add(Calendar.MINUTE, minutes);

			date = calendar.getTime();

			return date;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public Date addHours(Date inpdate, int hours) {
		try {
			Date date = null;

			Calendar calendar = Calendar.getInstance();

			calendar.setTime(inpdate);
			// Add hours to current date
			calendar.add(Calendar.HOUR, hours);

			date = calendar.getTime();

			return date;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public Date getfirstDateOfPreviousMonth() {
		try {
			Date firstDateOfPreviousMonth = null;
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.add(Calendar.MONTH, -1);
			aCalendar.set(Calendar.DATE, 1);
			firstDateOfPreviousMonth = aCalendar.getTime();
			return firstDateOfPreviousMonth;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public Date getlastDateOfPreviousMonth() {
		try {
			Date lastDateOfPreviousMonth = null;
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.add(Calendar.MONTH, -1);
			aCalendar.set(Calendar.DATE, 1);
			aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			lastDateOfPreviousMonth = aCalendar.getTime();
			return lastDateOfPreviousMonth;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public String get_DateCountStatus(String date, int count) {
		try {
			String status = "N";

			LocalDate today = LocalDate.now();
			LocalDate startdate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
			long days = ChronoUnit.DAYS.between(startdate, today);

			if (days <= count) {
				status = "Y";
			}

			return status;
		} catch (Exception e) {
			log.error(e);
		}
		return "N";
	}

	public String reqPrint(Object response) {
		ObjectMapper mapper = new ObjectMapper();
		String resp = "";
		try {
			// log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
			// resp = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
			resp = mapper.writeValueAsString(response);
			//log.info(mapper.writeValueAsString(response));
		} catch (Exception e) {
			log.error(e);
		}
		return resp;
	}

	public Properties getwebserviceurlProperty() {
		InputStream input = getClass().getClassLoader().getResourceAsStream("WebServiceUrl.properties");
		Properties prop = new Properties();
		try {
			prop.load(input);
		} catch (Exception e) {
			log.error(e);
		}
		return prop;
	}

	public Properties getapplicationProperty() {
		InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties");
		Properties prop = new Properties();
		try {
			prop.load(input);
		} catch (Exception e) {
			log.error(e);
		}
		return prop;
	}

	public String copyFile(String from, String to) {
		String path = "";
		try {
			log.info("copyFile--> from: " + from);
			log.info("copyFile--> to: " + to);
			Path src = Paths.get(from);
			Path dest = Paths.get(to);

			boolean exist = src.toFile().exists();
			log.info("copyFile--> exist: " + exist);

			if (!exist) {
				Thread.sleep(10000);
				Path copy = Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
				path = copy.toAbsolutePath().toString();
			} else {
				Path copy = Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
				path = copy.toAbsolutePath().toString();
			}
			log.info("copyFile--> path: " + path);
			return path;
		} catch (Exception e) {
			log.error(e);
			// path="C:/Users/New/Pictures/pic/2.jpg";
		}
		return path;
	}

	public String copyUrltoFile(String urlFile, String toPath) {
		try {

			File file = new File(toPath);

			if (!file.exists()) {
				URL url = new URL(urlFile);
				FileUtils.copyURLToFile(url, new File(toPath));
			}

			return toPath;
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	public DataSource datasourceSpring() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(getapplicationProperty().getProperty("spring.datasource.driverClassName"));
		dataSource.setUrl(getapplicationProperty().getProperty("spring.datasource.url"));
		dataSource.setUsername(getapplicationProperty().getProperty("spring.datasource.username"));
		dataSource.setPassword(getapplicationProperty().getProperty("spring.datasource.password"));
		return dataSource;
	}

	public DataSource datasourceContext() {
		try {
			JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
			bean.setJndiName(getapplicationProperty().getProperty("spring.datasource.jndi-name"));
			bean.setProxyInterface(DataSource.class);
			bean.setLookupOnStartup(false);
			bean.afterPropertiesSet();
			return (DataSource) bean.getObject();
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	public List<Map<String, Object>> getCoverList(List<Map<String, Object>> list, String key, String value,
			String type) {
		try {
			List<Map<String, Object>> response = new ArrayList<>();

			if (list != null) {

				if (type.equalsIgnoreCase("COND")) {
					List<Predicate<Map<String, Object>>> ml = new ArrayList<>();

					ml.add(map -> map.get(key).toString().equalsIgnoreCase(value));

					response = list.stream().filter(ml.stream().reduce(m -> true, Predicate::and))
							.collect(Collectors.toList());
				}
			}

			return response;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
		Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

		return t -> {
			List<?> keys = Arrays.stream(keyExtractors)
										.map(ke -> ke.apply(t))
										.collect(Collectors.toList());

			return seen.putIfAbsent(keys, Boolean.TRUE) == null;
		};
	}

	public Map<String, Object> callApi(String url, String auth, String method, Object request) {
		Map<String, Object> response = new HashMap<>();
		try {

			log.info("callApi--> URL: " + url);

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			if("Bearer".equalsIgnoreCase(auth)) {
				headers.set("Authorization",auth+" "+getBearerToken());
			}else {
				headers.set("Authorization", auth);
			}
			ResponseEntity<String> resEnt = null;

			if (method.equalsIgnoreCase("GET")) {

				HttpEntity<String> entity = new HttpEntity<String>(headers);

				resEnt = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

				response.put("Code", resEnt.getStatusCodeValue());
				response.put("Response", resEnt.getBody());

			} else if (method.equalsIgnoreCase("POST")) {

				HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);

				resEnt = restTemplate.postForEntity(url, entityReq, String.class);

				response.put("Code", resEnt.getStatusCodeValue());
				response.put("Response", resEnt.getBody());
				

			}

			return response;
		} catch (HttpClientErrorException e) {
			log.error(e);
			reqPrint(e.getResponseBodyAsString());

			response.put("Code", e.getRawStatusCode());
			response.put("Response", e.getResponseBodyAsString());
		} catch (Exception e) {
			log.error(e);
		}
		return response;
	}
	
	/*//@PostConstruct
	public void getMetadata() {
		File file =new File("C:\\Users\\MAANSAROVAR04\\Documents\\Received Files\\1679730814922.jpg");
		try {
			
			// Metadata metadata = ImageMetadataReader.readMetadata(file);
	            // See whether it has GPS data
	        //
			//log.info("Metadata response :"+reqPrint(metadata));
			
	          try {
	              Metadata metadata1 = ImageMetadataReader.readMetadata(file);
	             
	              log.info("metadata1" +reqPrint(metadata1));
	              
	              ExifSubIFDDirectory directory = metadata1.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
	              log.info("directory" +reqPrint(directory));
	              Date exifDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
	             
	              LocalDate systemDate =LocalDate.now();
	            		 
	              LocalDate date = exifDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	              if(date.isBefore(systemDate) || date.isAfter(systemDate)) {
	            	  System.out.println("INVALID");
	              }else {
	            	  System.out.println("VALID");

	              }
	              
	              
	              
	              //print(metadata1, "metadata1");

	          } catch (ImageProcessingException e) {
	          } catch (IOException e) {
	          }

	          //
	          // SCENARIO 2: SPECIFIC FILE TYPE
	          //
	          // If you know the file to be a JPEG, you may invoke the JpegMetadataReader, rather than the generic reader
	          // used in approach 1.  Similarly, if you knew the file to be a TIFF/RAW image you might use TiffMetadataReader,
	          // PngMetadataReader for PNG files, BmpMetadataReader for BMP files, or GifMetadataReader for GIF files.
	          //
	          // Using the specific reader offers a very, very slight performance improvement.
	          //
	          try {
	              Metadata metadata2 = JpegMetadataReader.readMetadata(file);

	              log.info("metadata2" +reqPrint(metadata2));

	             // print(metadata2, "metadata2");
	          } catch (JpegProcessingException e) {
	              print(e);
	          } catch (IOException e) {
	              print(e);
	          }

	          //
	          // APPROACH 3: SPECIFIC METADATA TYPE
	          //
	          // If you only wish to read a subset of the supported metadata types, you can do this by
	          // passing the set of readers to use.
	          //
	          // This currently only applies to JPEG file processing.
	          //
	          try {
	              // We are only interested in handling
	              Iterable<JpegSegmentMetadataReader> readers = Arrays.asList(new ExifReader(), new IptcReader());

	              Metadata metadata3 = JpegMetadataReader.readMetadata(file, readers);

	              //print(metadata3, "metadata3");
	              log.info("metadata3" +reqPrint(metadata3));

	          } catch (JpegProcessingException e) {
	              print(e);
	          } catch (IOException e) {
	              print(e);
	          }
		
		}catch (Exception e) {
				e.printStackTrace();
			}
	     
		
	}
	
	 private static void print(Metadata metadata, String method)
     {
         System.out.println();
         System.out.println("-------------------------------------------------");
         System.out.print(' ');
         System.out.print(method);
         System.out.println("-------------------------------------------------");
         System.out.println();

         //
         // A Metadata object contains multiple Directory objects
         //
         for (Directory directory : metadata.getDirectories()) {

             //
             // Each Directory stores values in Tag objects
             //
             for (Tag tag : directory.getTags()) {
                 System.out.println(tag);
             }

             //
             // Each Directory may also contain error messages
             //
             for (String error : directory.getErrors()) {
                 System.err.println("ERROR: " + error);
             }
         }
     }
		
	 private static void print(Exception exception)
	    {
	        System.err.println("EXCEPTION: " + exception);
	    }*/
	
	private String getBearerToken(){
		String token="";
		try {
			Response response =null;
			Map<String,Object> tokReq =new HashMap<String,Object>();
			tokReq.put("InsuranceId", cs.getwebserviceurlProperty().getProperty("InsuranceId"));
			tokReq.put("LoginType", cs.getwebserviceurlProperty().getProperty("LoginType"));
			tokReq.put("Password", cs.getwebserviceurlProperty().getProperty("Password"));
			tokReq.put("UserId", cs.getwebserviceurlProperty().getProperty("UserId"));
		
			String tokenJsonReq =new Gson().toJson(tokReq);
			String tokenApi =cs.getwebserviceurlProperty().getProperty("token.api");
			RequestBody tokenReqBody =RequestBody.create(tokenJsonReq, mediaType);
			Request tokenReq =new Request.Builder()
					.url(tokenApi)
					.post(tokenReqBody)
					.build();
			response=httpClient.newCall(tokenReq).execute();
			String obj =response.body().string();
			TokenResponse tokenRes =mapper.readValue(obj, TokenResponse.class);
			
			token =tokenRes.getTokenResponse().getToken();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return token;
	}
}
