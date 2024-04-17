package com.maan.whatsapp.service.wati;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.w3c.dom.stylesheets.MediaList;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.maan.whatsapp.entity.master.QWhatsappTemplateMaster;
import com.maan.whatsapp.entity.master.QWhatsappTemplateMasterPK;
import com.maan.whatsapp.entity.master.WAMessageMaster;
import com.maan.whatsapp.entity.master.WhatsappTemplateMaster;
import com.maan.whatsapp.entity.whatsapp.WhatsappContactData;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDetail;
import com.maan.whatsapp.repository.whatsapp.WhatsappContactDataRepo;
import com.maan.whatsapp.repository.whatsapp.WhatsappRequestDetailRepo;
import com.maan.whatsapp.request.whatsapp.ButtonHeaderReq;
import com.maan.whatsapp.request.whatsapp.ButtonsNameReq;
import com.maan.whatsapp.request.whatsapp.WAWatiReq;
import com.maan.whatsapp.request.whatsapp.WhatsAppButtonReq;
import com.maan.whatsapp.response.error.Errors;
import com.maan.whatsapp.response.motor.ButtonMediaReq;
import com.maan.whatsapp.response.wati.sendsesfile.MessageFileRes;
import com.maan.whatsapp.response.wati.sendsesfile.SendSessionFile;
import com.maan.whatsapp.response.wati.sendsesmsg.MessageSendRes;
import com.maan.whatsapp.response.wati.sendsesmsg.SendSessionMsg;
import com.maan.whatsapp.service.common.CommonService;
import com.maan.whatsapp.service.motor.MotorService;
import com.maan.whatsapp.service.motor.MotorServiceImpl;
import com.maan.whatsapp.service.whatsapptemplate.WhatsapptemplateServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vdurmont.emoji.EmojiParser;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class WatiApiCall {

	@Autowired
	private CommonService cs;
	@Autowired
	private MotorService motSer;

	@Autowired
	private WhatsappRequestDetailRepo detailRepo;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JPAQueryFactory jpa;
	
	private Logger log = LogManager.getLogger(getClass());
	
	@Autowired
	private WhatsappContactDataRepo contactDataRepo ;

	@SuppressWarnings("unchecked")
	@Async
	public CompletableFuture<String> sendMsg(OkHttpClient okhttp, RequestBody body, String commonurl, String msgurl,
			String fileurl, String auth, WhatsappRequestDetail detail, String waid, Date reqtime) {
		try {

			String fileyn = StringUtils.isBlank(detail.getFile_yn()) ? "" : detail.getFile_yn();
			String apiCall = StringUtils.isBlank(detail.getIsapicall()) ? "N" : detail.getIsapicall();
			String isValidationApi = StringUtils.isBlank(detail.getIsvalidationapi()) ? "N" : detail.getIsvalidationapi();
            String isbuttonMsg =StringUtils.isBlank(detail.getIsButtonMsg()) ? "N" : detail.getIsButtonMsg();
            String isResMsg =StringUtils.isBlank(detail.getIsResMsg()) ? "N" : detail.getIsResMsg();
            String isResMsgApi=StringUtils.isBlank(detail.getIsResMsgApi()) ? "" : detail.getIsResMsgApi();
            String isTemplateMsg =StringUtils.isBlank(detail.getIsTemplateMsg())?"N":detail.getIsTemplateMsg();
            if("Y".equalsIgnoreCase(isResMsg)) {
            	String msg =detail.getMessage();
            	if(StringUtils.isNotEmpty(isResMsgApi)) {
            		String apiResp = motSer.callMotorApi(detail, waid);
            		Map<String,Object> data =objectMapper.readValue(apiResp, Map.class);
            		for(Map.Entry<String, Object> entry :data.entrySet()) {
            			if(msg.contains(entry.getKey())) {
            				msg=msg.replace("{"+entry.getKey()+"}", entry.getValue()==null?"":entry.getValue().toString());
            			}
            		}
            		detail.setIsResMsg("N");
            		detail.setMessage(msg);
            	}else if(StringUtils.isEmpty(isResMsgApi)) {
            		Long currentStageCode =detail.getReqDetPk().getCurrentstage();
            		Long subStageCode =detail.getReqDetPk().getCurrentsubstage()-1;
            		String mesText=detailRepo.getMessageText(waid,currentStageCode.toString(),subStageCode.toString());
            		Map<String,Object> data =objectMapper.readValue(mesText, Map.class);
            		for(Map.Entry<String, Object> entry :data.entrySet()) {
            			if(msg.contains(entry.getKey())) {
            				msg=msg.replace("{"+entry.getKey()+"}", entry.getValue()==null?"":entry.getValue().toString());
            			}
            		}
            		detail.setIsResMsg("N");
            		detail.setMessage(msg);
            	}
            	
            }
            
            else if (apiCall.equalsIgnoreCase("Y") && isValidationApi.equalsIgnoreCase("N") 
					&& "N".equals(detail.getIsReponseYn())) {

				String apiResp = "";

				apiResp = motSer.callMotorApi(detail, waid);

				detail.setMessage(apiResp.trim());
				
				if("Y".equalsIgnoreCase(isTemplateMsg))
					isTemplateMsg=detailRepo.getTemplateStatus(waid,detail.getReqDetPk().getCurrentstage().toString()
							,detail.getReqDetPk().getCurrentsubstage().toString(),detail.getRemarks());
				
			}/*else if(apiCall.equalsIgnoreCase("N") && isValidationApi.equalsIgnoreCase("N") && "N".equalsIgnoreCase(detail.getIsreplyyn())) {
				
				String tran_id =motorServiceImpl.setSaveRequest(detail, waid);	
				String link =cs.getwebserviceurlProperty().getProperty("wa.preins.screen.link").replace("{TranId}", tran_id);
				
				Long stageCode = detail.getReqDetPk().getCurrentstage();
				Long subStageCode = detail.getReqDetPk().getCurrentsubstage();
				
				WhatsappTemplateMaster temp_master =getTempMasterStageContent(detail.getRemarks(), "90016",
						Long.valueOf(waid), stageCode, subStageCode);
				
				String res =StringUtils.isBlank(temp_master.getButtonBody())?"":temp_master.getButtonBody();
			
				detail.setMessage(res.replace("{TinyUrl}",tempServiceImpl.getTinyUrl(link)));		
			} */
            
            String msg ="";
            if("N".equalsIgnoreCase(isTemplateMsg)) {
            	msg = motSer.getTreeStructMsg(detail);
    			msg =setEmojiResponse(msg);
            }else {
            	msg=detail.getMessage();
            }
            
            if("Y".equalsIgnoreCase(detail.getFormpageYn())) {
            	String formPageUrl =detail.getFormpageUrl().trim()+waid;
            	msg = msg.replace("{TinyUrl}",formPageUrl);
            }
            	
            
            detail.setMessage(msg);
			WAWatiReq waReq = WAWatiReq.builder()
					.filepath(detail.getFile_path())
					.msg(detail.getMessage())
					.waid(waid)
					.isTemplateMsg(isTemplateMsg)
					.build();

			String url = "";

			if (fileyn.equalsIgnoreCase("Y")) {

				url = commonurl + fileurl;

				if (StringUtils.isNotBlank(waReq.getFilepath())) {

					WAWatiReq waRes = callSendSessionFile(okhttp, url, auth, waReq);

					detail.setIssent("Y");
					detail.setRequest_time(reqtime);
					detail.setResponse_time(new Date());
					detail.setWa_messageid(waRes.getWamsgId());
					detail.setWa_response(waRes.getWaresponse());
					detail.setWa_filepath(waRes.getWafilepath());
					detail.setSessionid(waRes.getSessionid());
				}
			} else {
				url = commonurl + msgurl;

				if(StringUtils.isNotBlank(waReq.getMsg()) && "N".equals(isbuttonMsg) 
						&& "N".equals(isbuttonMsg) && "N".equalsIgnoreCase(isTemplateMsg)) {

					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

					detail.setIssent("Y");
					detail.setRequest_time(reqtime);
					detail.setResponse_time(new Date());
					detail.setWa_messageid(waRes.getWamsgId());
					detail.setWa_response(waRes.getWaresponse());
					detail.setWa_filepath(waRes.getWafilepath());
					detail.setSessionid(waRes.getSessionid());
					
				}else if (StringUtils.isNotBlank(waReq.getMsg()) && "Y".equals(isbuttonMsg)
						&& "N".equalsIgnoreCase(isTemplateMsg)) {
					
					fileurl=cs.getwebserviceurlProperty().getProperty("whatsapp.api.button");
					url = commonurl + fileurl;
					
					Long stageCode = detail.getReqDetPk().getCurrentstage();
					Long subStageCode = detail.getReqDetPk().getCurrentsubstage();
					
					WhatsappTemplateMaster tempM =getTempMasterStageContent(detail.getRemarks(), "90016",
							Long.valueOf(waid), stageCode, subStageCode);
					
					String language =contactDataRepo.getLanguage(waid);
					String button1 ="",button2="",button3="";
					if("Y".equalsIgnoreCase(isbuttonMsg)) {
						if("English".equalsIgnoreCase(language)) {
							button1=tempM.getButton1();
							button2=tempM.getButton2();
							button3=StringUtils.isBlank(tempM.getButton3())?"":tempM.getButton3();
						}else if("Swahili".equalsIgnoreCase(language)) {
							button1=tempM.getButtonSw1();
							button2=tempM.getButtonSw2();
							button3=StringUtils.isBlank(tempM.getButtonSw3())?"":tempM.getButtonSw3();
						}						
					}
					waReq.setMsgType(StringUtils.isBlank(tempM.getMsgType())?"":tempM.getMsgType());
					waReq.setIsButtonMsg(StringUtils.isBlank(tempM.getIsButtonMsg())?"N":tempM.getIsButtonMsg());
					waReq.setImageUrl(StringUtils.isBlank(tempM.getImageUrl())?"":tempM.getImageUrl());
					waReq.setImageName(StringUtils.isBlank(tempM.getImageName())?"":tempM.getImageName());
					waReq.setButton1(button1);
					waReq.setButton2(button2);
					waReq.setButton3(button3);

				
					
					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);
					
					detail.setIssent("Y");
					detail.setRequest_time(reqtime);
					detail.setResponse_time(new Date());
					detail.setWa_messageid(waRes.getWamsgId());
					detail.setWa_response(waRes.getWaresponse());
					detail.setWa_filepath(waRes.getWafilepath());
					detail.setSessionid(waRes.getSessionid());
					
				}else if("Y".equalsIgnoreCase(isTemplateMsg)) {
					
					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

					detail.setIssent("Y");
					detail.setRequest_time(reqtime);
					detail.setResponse_time(new Date());
					detail.setWa_messageid(waRes.getWamsgId());
					detail.setWa_response(waRes.getWaresponse());
					detail.setWa_filepath(waRes.getWafilepath());
					detail.setSessionid(waRes.getSessionid());
				}
			}

			if(apiCall.equalsIgnoreCase("Y") && StringUtils.isBlank(waReq.getMsg())) {
				detail.setIssent("Y");
				detail.setRequest_time(reqtime);
				detail.setResponse_time(new Date());
			}

			detailRepo.save(detail);

		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	private String  setEmojiResponse(String msg) {
		try {
		String response = msg;

				List<Map<String, Object>> list=detailRepo.getEmojiDetails();
				
				for(Map<String, Object> map :list) {
					
					String key = map.get("KEY_CODE")==null?"": "{"+map.get("KEY_CODE").toString()+"}";
					
						if (response.contains(key)) {
							
							String emoji=EmojiParser.parseToUnicode(map.get("REMARKS")==null?"":map.get("REMARKS").toString().trim());
							
							response = response.replace( key  , emoji);
									
									
						}
					
				}
				return response;
			} catch (Exception e) {
				log.error(e);
			}
			return null;
		}
	
	public String sendValidationMsg(OkHttpClient okhttp, RequestBody body, String commonurl, String msgurl,
			String fileurl, String auth, WhatsappRequestDetail detail, String waid, Date reqtime, WhatsappTemplateMaster tempM) {
		try {

			String apiCall = StringUtils.isBlank(detail.getIsapicall()) ? "N" : detail.getIsapicall();
			String isValidationApi = StringUtils.isBlank(detail.getIsvalidationapi()) ? "N" : detail.getIsvalidationapi();
			String isValid = "N";
			String isResSaveApi = StringUtils.isBlank(detail.getIsResSaveApi()) ? "N" : detail.getIsResSaveApi();
			String isResMsg = StringUtils.isBlank(detail.getIsResMsg()) ? "N" : detail.getIsResMsg();
			String isButtonMsg= StringUtils.isBlank(tempM.getIsButtonMsg())?"N":tempM.getIsButtonMsg();
			String msgType= StringUtils.isBlank(tempM.getMsgType())?"":tempM.getMsgType();
			String imageName = StringUtils.isBlank(tempM.getImageName())?"":tempM.getImageName();
			String imageUrl = StringUtils.isBlank(tempM.getImageUrl())?"":tempM.getImageUrl();
			String language =contactDataRepo.getLanguage(waid);
			String button1 ="",button2="",button3="";
			if("Y".equalsIgnoreCase(isButtonMsg)) {
				if("English".equalsIgnoreCase(language)) {
					button1=tempM.getButton1();
					button2=tempM.getButton2();
					button3=StringUtils.isBlank(tempM.getButton3())?"":tempM.getButton3();
				}else if("Swahili".equalsIgnoreCase(language)) {
					button1=tempM.getButtonSw1();
					button2=tempM.getButtonSw2();
					button3=StringUtils.isBlank(tempM.getButtonSw3())?"":tempM.getButtonSw3();
				}						
			}
			String isTemplateMsg =StringUtils.isBlank(detail.getIsTemplateMsg())?"N":detail.getIsTemplateMsg();
			String buttonUrl=cs.getwebserviceurlProperty().getProperty("whatsapp.api.button");
			msgurl ="Y".equalsIgnoreCase(isButtonMsg)?buttonUrl:msgurl;
			if("Y".equalsIgnoreCase(isResSaveApi) && "Y".equalsIgnoreCase(apiCall)) {
				String apiResp = "";
				apiResp = motSer.callMotorApi(detail, waid);
				@SuppressWarnings("unchecked")
				Map<String,Object> data =new ObjectMapper().readValue(apiResp, Map.class);
				String url = commonurl + msgurl;
				String error_desc =data.get("ErrorDesc")==null?"":data.get("ErrorDesc").toString();
				if(StringUtils.isNotBlank(error_desc)) {
					String errorResStr =StringUtils.isBlank(tempM.getErrorrespstring())?"":tempM.getErrorrespstring();
					for (Map.Entry<String, Object> entry : data.entrySet()) {
						if (errorResStr.contains(entry.getKey().toString())) {
							errorResStr = errorResStr.replace("{" + entry.getKey().toString() + "}",
									entry.getValue() == null ? "" : entry.getValue().toString());
						}
					}
					WAWatiReq waReq = WAWatiReq.builder()
							.filepath(detail.getFile_path())
							.msg(errorResStr)
							.waid(waid)
							.isButtonMsg(isButtonMsg)
							.msgType(msgType)
							.imageUrl(imageUrl)
							.imageName(imageName)
							.button1(button1)
							.button2(button2)
							.button3(button3)
							.isTemplateMsg(isTemplateMsg)
							.build();

					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

					detail.setSessionid(waRes.getSessionid());
					isValid="N";
					detailRepo.save(detail);
				}else {
					String responseText =new Gson().toJson(data);
					detail.setApiMessageText(responseText);
					isValid="Y";
					detailRepo.save(detail);

				}
				
				
			}
			else if (apiCall.equalsIgnoreCase("Y") && isValidationApi.equalsIgnoreCase("Y") && "N".equals(isResMsg)) {

				String apiResp = "";

				apiResp = motSer.callMotorApi(detail, waid);

				detail.setValidationmessage(apiResp.trim());

				String url = commonurl + msgurl;

				if (StringUtils.isNotBlank(apiResp)) {

					WAWatiReq waReq = WAWatiReq.builder()
							.filepath(detail.getFile_path())
							.msg(apiResp)
							.waid(waid)
							.isButtonMsg(isButtonMsg)
							.msgType(msgType)
							.imageUrl(imageUrl)
							.imageName(imageName)
							.button1(button1)
							.button2(button2)
							.button3(button3)
							.isTemplateMsg(isTemplateMsg)
							.build();

					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

					detail.setSessionid(waRes.getSessionid());
					
					isValid = "N";
				} else {
					isValid = "Y";
				}

				detailRepo.save(detail);
			}else {
				String apiResp = "";

				apiResp = motSer.callMotorApi(detail, waid);

				detail.setValidationmessage(apiResp.trim());

				String url = commonurl + msgurl;

				if (StringUtils.isNotBlank(apiResp)) {

					WAWatiReq waReq = WAWatiReq.builder()
							.filepath(detail.getFile_path())
							.msg(apiResp)
							.waid(waid)
							.isButtonMsg(isButtonMsg)
							.msgType(msgType)
							.imageUrl(imageUrl)
							.imageName(imageName)
							.button1(button1)
							.button2(button2)
							.button3(button3)
							.isTemplateMsg(isTemplateMsg)
							.build();

					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

					detail.setSessionid(waRes.getSessionid());
					
					isValid = "N";
				} else {
					isValid = "Y";
				}

				detailRepo.save(detail);
			}

			return isValid;
		} catch (Exception e) {
			log.error(e);
		}
		return "N";
	}

	public String sendDocValidationMsg(OkHttpClient okhttp, RequestBody body, String commonurl, String msgurl,
			String fileurl, String auth, WhatsappRequestDetail detail, String waid, Date reqtime, WhatsappTemplateMaster tempM) {
		try {

			String isButtonMsg= StringUtils.isBlank(tempM.getIsButtonMsg())?"N":tempM.getIsButtonMsg();
			String msgType= StringUtils.isBlank(tempM.getMsgType())?"":tempM.getMsgType();
			String imageName = StringUtils.isBlank(tempM.getImageName())?"":tempM.getImageName();
			String imageUrl = StringUtils.isBlank(tempM.getImageUrl())?"":tempM.getImageUrl();
			String language =contactDataRepo.getLanguage(waid);
			String button1 ="",button2="",button3="";
			if("Y".equalsIgnoreCase(isButtonMsg)) {
				if("English".equalsIgnoreCase(language)) {
					button1=tempM.getButton1();
					button2=tempM.getButton2();
					button3=StringUtils.isBlank(tempM.getButton3())?"":tempM.getButton3();
				}else if("Swahili".equalsIgnoreCase(language)) {
					button1=tempM.getButtonSw1();
					button2=tempM.getButtonSw2();
					button3=StringUtils.isBlank(tempM.getButtonSw3())?"":tempM.getButtonSw3();
				}						
			}
			String buttonUrl=cs.getwebserviceurlProperty().getProperty("whatsapp.api.button");

			String url ="Y".equalsIgnoreCase(isButtonMsg)? commonurl + buttonUrl:commonurl + msgurl;
			
			WAWatiReq waReq = WAWatiReq.builder()
					.filepath(detail.getFile_path())
					.msg(StringUtils.isBlank(detail.getValidationmessage())?"":detail.getValidationmessage())
					.waid(waid)
					.isButtonMsg(isButtonMsg)
					.msgType(msgType)
					.imageUrl(imageUrl)
					.imageName(imageName)
					.button1(button1)
					.button2(button2)
					.button3(button3)
					.build();

			WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	public WAWatiReq callSendSessionMsg(OkHttpClient okhttp, RequestBody body, String url, String auth, WAWatiReq req) {

		WAWatiReq detail = new WAWatiReq();

		try {
			String waid = req.getWaid();
			String msgs = req.getMsg();
			msgs=setEmojiResponse(msgs);
			msgs = URLEncoder.encode(msgs, StandardCharsets.UTF_8.toString());

			detail.setWaid(waid);
			detail.setMsg(req.getMsg());

			if("Y".equalsIgnoreCase(req.getIsButtonMsg()) && "N".equalsIgnoreCase(req.getIsTemplateMsg())) {
			
				String message =frameButtonMsg(detail.getMsg(),req);
			    url = url.replace("{whatsappNumber}", waid);
				log.info("Button req "+message);
				MediaType contentType =MediaType.parse("application/json");
				body =RequestBody.create(message,contentType);
		
			}else if("Y".equalsIgnoreCase(req.getIsTemplateMsg())) {
				MediaType contentType =MediaType.parse("application/json");
				body =RequestBody.create(req.getMsg(),contentType);
				url=cs.getwebserviceurlProperty().getProperty("whatsapp.template.msg");

			}else {
				
				url = url.replace("{whatsappNumber}", waid);
				url = url.replace("{pageSize}", "");
				url = url.replace("{pageNumber}", "");
				url = url.replace("{messageText}", msgs);
				url = url.trim();
			}
			
			Request request = new Request.Builder()
					.url(url)
					.addHeader("Authorization", auth)
					.post(body)
					.build();

			Response response = okhttp.newCall(request).execute();

			String responseString = response.body().string();

			log.info("callSendSessionMsg--> waid: " + waid + " response: " + responseString);

			SendSessionMsg apiRes = objectMapper.readValue(responseString, SendSessionMsg.class);

			String result = StringUtils.isBlank(apiRes.getResult())?"":apiRes.getResult();

			String msgid = "", sessionid = "";

			if (result.equalsIgnoreCase("success") || StringUtils.isEmpty(result)) {

				if (apiRes.getMessage() != null) {
					MessageSendRes msg = objectMapper.convertValue(apiRes.getMessage(),
							MessageSendRes.class);

					cs.reqPrint(msg);
					
					msgid = msg.getId();
					
					sessionid = msg.getTicketId();
				}

				detail.setIssent("Y");
				detail.setWamsgId(msgid);
				detail.setWaresponse(result);
				detail.setSessionid(sessionid);

			} else {
				String msgRes = apiRes.getMessage() == null ? "" : String.valueOf(apiRes.getMessage());
				String info = StringUtils.isBlank(apiRes.getInfo()) ? "" : apiRes.getInfo();

				detail.setIssent("N");
				detail.setWamsgId(msgid);
				detail.setWaresponse(StringUtils.isBlank(info) ? msgRes : info);
				detail.setSessionid(sessionid);
			}

		} catch (HttpStatusCodeException e) {
			log.error(e);
			cs.reqPrint(e.getResponseBodyAsString());

			detail.setIssent("N");
			detail.setWaresponse(e.getLocalizedMessage());

		} catch (Exception e) {
			log.error(e);

			detail.setIssent("N");
			detail.setWaresponse(e.getLocalizedMessage());
		}
		return detail;
	}

	private String frameButtonMsg(String msg, WAWatiReq req) {
		String buttonMessage="";
		String request="";
		try {
		
			if("Image".equalsIgnoreCase(req.getMsgType())) {
				ButtonMediaReq media =ButtonMediaReq.builder()
						.url(req.getImageUrl())
						.fileName(req.getImageName())
						.build();
				
				ButtonHeaderReq header =ButtonHeaderReq.builder()
						.type("Image")
						.text(" ")
						.media(media)
						.build();
				List<ButtonsNameReq> buttonList =new ArrayList<ButtonsNameReq>();
				if(StringUtils.isNotBlank(req.getButton1())) {
					buttonList.add(new ButtonsNameReq(req.getButton1()));
					
				}if(StringUtils.isNotBlank(req.getButton2())) {
					buttonList.add(new ButtonsNameReq(req.getButton2()));

				}if(StringUtils.isNotBlank(req.getButton3())) {
					buttonList.add(new ButtonsNameReq(req.getButton3()));

				}
				WhatsAppButtonReq buttonReq =WhatsAppButtonReq.builder()
						.header(header)
						.body(msg)
						.footer(" ")
						.buttons(buttonList)
						.build();
				request =cs.reqPrint(buttonReq);
				
			}else if("Text".equalsIgnoreCase(req.getMsgType())) {
				
				ButtonHeaderReq header =ButtonHeaderReq.builder()
						.type("Text")
						.text(" ")
						.media(null)
						.build();
				List<ButtonsNameReq> buttonList =new ArrayList<ButtonsNameReq>();
				if(StringUtils.isNotBlank(req.getButton1())) {
					buttonList.add(new ButtonsNameReq(req.getButton1()));
					
				}if(StringUtils.isNotBlank(req.getButton2())) {
					buttonList.add(new ButtonsNameReq(req.getButton2()));

				}if(StringUtils.isNotBlank(req.getButton3())) {
					buttonList.add(new ButtonsNameReq(req.getButton3()));

				}
				WhatsAppButtonReq buttonReq =WhatsAppButtonReq.builder()
						.header(header)
						.body(msg)
						.footer(" ")
						.buttons(buttonList)
						.build();
				request =cs.reqPrint(buttonReq);
			}
			

			String twoSlash=cs.getwebserviceurlProperty().getProperty("wa.hit.slash.two");
			String oneSlash=cs.getwebserviceurlProperty().getProperty("wa.hit.slash.one");
			
			buttonMessage =request.replace(twoSlash, oneSlash);

		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return buttonMessage;
		
	}

	public WAWatiReq callSendSessionFile(OkHttpClient okhttp, String url, String auth, WAWatiReq req) {

		WAWatiReq detail = new WAWatiReq();

		try {

			String filePath = req.getFilepath();

			String fileName = FilenameUtils.getName(filePath);

			String waid = req.getWaid();
			String msgs = req.getMsg();

			msgs = URLEncoder.encode(msgs, StandardCharsets.UTF_8.toString());

			url = url.replace("{whatsappNumber}", waid);
			url = url.replace("{pageSize}", "");
			url = url.replace("{pageNumber}", "");
			url = url.replace("{caption}", msgs);
			url = url.trim();

			detail.setWaid(waid);
			detail.setMsg(req.getMsg());
			detail.setFilepath(filePath);

			File file = new File(filePath);

			boolean exist = file.exists();

			log.info("callSendSessionFile--> exist: " + exist);

			if(exist) {
				String mimeType = Files.probeContentType(file.toPath());

				RequestBody body = new MultipartBody.Builder()
						.setType(MultipartBody.FORM)
						.addFormDataPart("file", fileName, RequestBody.create(file, MediaType.parse(mimeType)))
						.build();

				Request request = new Request.Builder()
						.url(url)
						.addHeader("Authorization", auth)
						.post(body)
						.build();

				Response response = okhttp.newCall(request).execute();

				String responseString = response.body().string();
				
				log.info("callSendSessionFile--> mobno: " + waid + " response: " + responseString);

				SendSessionFile apiRes = objectMapper.readValue(responseString, SendSessionFile.class);

				String result = apiRes.getResult();

				String msgid = "", sessionid = "";
				String waFilepath = "";

				if (result.equalsIgnoreCase("success")) {

					if (apiRes.getMessage() != null) {

						MessageFileRes msg = objectMapper.convertValue(apiRes.getMessage(),
								MessageFileRes.class);

						cs.reqPrint(msg);

						msgid = msg.getId();
						sessionid = msg.getTicketId();
						waFilepath = msg.getText();
					}

					detail.setIssent("Y");
					detail.setWamsgId(msgid);
					detail.setWaresponse(result);
					detail.setSessionid(sessionid);

				} else {
					String msgRes = apiRes.getMessage() == null ? "" : String.valueOf(apiRes.getMessage());
					String info = StringUtils.isBlank(apiRes.getInfo()) ? "" : apiRes.getInfo();

					detail.setIssent("N");
					detail.setWamsgId(msgid);
					detail.setWaresponse(StringUtils.isBlank(info) ? msgRes : info);
					detail.setWafilepath(waFilepath);
					detail.setSessionid(sessionid);
				}
			}

		} catch (HttpStatusCodeException e) {
			log.error(e);
			cs.reqPrint(e.getResponseBodyAsString());

			detail.setIssent("N");
			detail.setWaresponse(e.getLocalizedMessage());

		} catch (Exception e) {
			log.error(e);

			detail.setIssent("N");
			detail.setWaresponse(e.getLocalizedMessage());
		}
		return detail;
	}

	public String sendDocmentMsg(OkHttpClient okhttp, RequestBody body, String commonurl, String msgurl, String fileurl,
			String auth, WhatsappRequestDetail detail, String waid, Date date, WhatsappTemplateMaster tempM) {
			String isValid = "N";

		try {
			String apiResp =tempM.getMessage_content_en()+tempM.getMessage_regards_en();
			String apiCall = StringUtils.isBlank(detail.getIsapicall()) ? "N" : detail.getIsapicall();
			String isValidationApi = StringUtils.isBlank(detail.getIsvalidationapi()) ? "N" : detail.getIsvalidationapi();

				//apiResp = motSer.callMotorApi(detail, waid);

				detail.setValidationmessage(apiResp.trim());

				String url = commonurl + msgurl;

				if (StringUtils.isNotBlank(apiResp)) {

					WAWatiReq waReq = WAWatiReq.builder()
							.filepath(detail.getFile_path())
							.msg(apiResp)
							.waid(waid)
							.build();

					WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

					detail.setSessionid(waRes.getSessionid());
					
					isValid = "N";
				} else {
					isValid = "Y";
				}

				detailRepo.save(detail);
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
       
		return isValid;
	}



	public String sendIsResYnMsg(OkHttpClient okhttp, RequestBody body, String commonurl, String msgurl, String fileurl,
			String auth, WhatsappRequestDetail reqDet, String waid, Date date) {
		try {
			String apiResp = "";

			apiResp = motSer.callMotorApi(reqDet, waid);

			String url = commonurl + msgurl;
			
			url =url.replace("{whatsappNumber}", waid);
			
			if(StringUtils.isBlank(apiResp)) {
				
				apiResp="Please enter valid data";

				WAWatiReq waReq = WAWatiReq.builder()
						.filepath(reqDet.getFile_path())
						.msg(reqDet.getMessage())
						.waid(waid)
						.build();


				WAWatiReq waRes = callSendSessionMsg(okhttp, body, url, auth, waReq);

				waRes.setSessionid(waRes.getSessionid());
				
			}else {
				
				WhatsappTemplateMaster temp =getTempMasterStageContent(reqDet.getRemarks(), "90016",
						Long.valueOf(waid), reqDet.getReqDetPk().getCurrentstage(), reqDet.getReqDetPk().getCurrentsubstage());
				
				ButtonHeaderReq header=null;
				
				if("Image".equalsIgnoreCase(temp.getMsgType())) {
					
					ButtonMediaReq media =ButtonMediaReq.builder()
							.fileName(temp.getImageName())
							.url(temp.getImageUrl())
							.build();
					
					 header =ButtonHeaderReq.builder().type(temp.getMsgType()).
							text(temp.getButtonHeader())
							.media(media)
							.build();
				}else {
					
					 header =ButtonHeaderReq.builder().type(StringUtils.isBlank(temp.getMsgType())?"Text":temp.getMsgType()).
							 text(temp.getButtonHeader())
							.media(null)
							.build();
				}
				
				
				List<ButtonsNameReq>buttons =new ArrayList<ButtonsNameReq>(); 
				
				buttons.add(new ButtonsNameReq(StringUtils.isBlank(temp.getButton1())?"MainMenu":temp.getButton1()));
				if(StringUtils.isNotBlank(temp.getButton2())) {
					buttons.add(new ButtonsNameReq(temp.getButton2()));
				}
				if(StringUtils.isNotBlank(temp.getButton3())) {
					buttons.add(new ButtonsNameReq(temp.getButton3()));
				}
				WhatsAppButtonReq req = WhatsAppButtonReq.builder()
						.header(header)
						.body(apiResp)
						.footer(StringUtils.isBlank(temp.getButtonFooter())?"":temp.getButtonFooter())
						.buttons(buttons)
						.build();
				
				RequestBody requestBody =RequestBody.create(cs.reqPrint(req), MediaType.parse("application/json"));
				
				Request request = new Request.Builder()
						.url(url)
						.addHeader("Authorization", auth)
						.post(requestBody)
						.build();
	
				Response response = okhttp.newCall(request).execute();
	
				String responseString = response.body().string();
				
				log.info("Whatsapp button api response" +responseString);
				
				reqDet.setIsResponseYnSent("Y");
				
				detailRepo.save(reqDet);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "N";
	}
	
	private WAWatiReq sendButtonMsg(WAWatiReq waReq, WhatsappTemplateMaster temp) {
		WAWatiReq detail = new WAWatiReq();
		Gson jsonPrint =new Gson();
		try {
			String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
			String button_msg_url = cs.getwebserviceurlProperty().getProperty("whatsapp.api.button");
			String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

			String url = commonurl + button_msg_url;
			
			url =url.replace("{whatsappNumber}", waReq.getWaid());			
			
			ButtonHeaderReq header=null;
			
			String messageBody ="";
			
			String isApiCall =StringUtils.isBlank(temp.getIsapicall())?"N":temp.getIsapicall();
			
			String Isreplyyn =StringUtils.isBlank(temp.getIsreplyyn())?"N":temp.getIsreplyyn();
			
			if("Y".equals(isApiCall) || "N".equals(Isreplyyn)) 
				messageBody =StringUtils.isBlank(waReq.getMsg())?"Something went wrong":waReq.getMsg();
			else 
				messageBody =StringUtils.isBlank(temp.getButtonBody())?"Something went wrong":temp.getButtonBody();
			
			if("Image".equalsIgnoreCase(temp.getMsgType())) {
				
				ButtonMediaReq media =ButtonMediaReq.builder()
						.fileName(temp.getImageName())
						.url(temp.getImageUrl())
						.build();
				
				 header =ButtonHeaderReq.builder().type(temp.getMsgType()).
						text(temp.getButtonHeader())
						.media(media)
						.build();
				 
			}else if("Text".equalsIgnoreCase(temp.getMsgType())) {
				
				header =ButtonHeaderReq.builder().type(temp.getMsgType()).
						text(temp.getButtonHeader())
						.media(null)
						.build();
			}
			
			OkHttpClient okhttp = new OkHttpClient.Builder()
					.readTimeout(30, TimeUnit.SECONDS)
					.build();
			
			List<ButtonsNameReq>buttons =new ArrayList<ButtonsNameReq>(); 
			
			buttons.add(new ButtonsNameReq(StringUtils.isBlank(temp.getButton1())?"MainMenu":temp.getButton1()));
			if(StringUtils.isNotBlank(temp.getButton2())) {
				buttons.add(new ButtonsNameReq(temp.getButton2()));
			}
			if(StringUtils.isNotBlank(temp.getButton3())) {
				buttons.add(new ButtonsNameReq(temp.getButton3()));
			}

			WhatsAppButtonReq req = WhatsAppButtonReq.builder()
					.header(header)
					.body(messageBody)
					.footer(StringUtils.isBlank(temp.getButtonFooter())?"":temp.getButtonFooter())
					.buttons(buttons)
					.build();
			
			String obj =jsonPrint.toJson(req);
			
			String twoSlash=cs.getwebserviceurlProperty().getProperty("wa.hit.slash.two");
			String oneSlash=cs.getwebserviceurlProperty().getProperty("wa.hit.slash.one");
			
			String msgReq =obj.replace(twoSlash, oneSlash);

			System.out.println(msgReq);
			
			RequestBody requestBody =RequestBody.create(msgReq, MediaType.parse("application/json"));			
			
			Request request = new Request.Builder()
					.url(url)
					.addHeader("Authorization", auth)
					.post(requestBody)
					.build();

			Response response = okhttp.newCall(request).execute();

			String responseString = response.body().string();
			
			log.info("Whatsapp button api response" +responseString);
			
			log.info("callSendSessionMsg--> waid: " + waReq.getWaid() + " response: " + responseString);

			SendSessionMsg apiRes = objectMapper.readValue(responseString, SendSessionMsg.class);

			String result = apiRes.getResult();

			String msgid = "", sessionid = "";

			if (apiRes.getMessage()!=null) {

				if (apiRes.getMessage() != null) {
					MessageSendRes msg = objectMapper.convertValue(apiRes.getMessage(),
							MessageSendRes.class);

					cs.reqPrint(msg);
					
					msgid = msg.getId();
					
					sessionid = msg.getTicketId();
				}

				detail.setIssent("Y");
				detail.setWamsgId(msgid);
				detail.setWaresponse(result);
				detail.setSessionid(sessionid);

			} else {
				String msgRes = apiRes.getMessage() == null ? "" : String.valueOf(apiRes.getMessage());
				String info = StringUtils.isBlank(apiRes.getInfo()) ? "" : apiRes.getInfo();

				detail.setIssent("N");
				detail.setWamsgId(msgid);
				detail.setWaresponse(StringUtils.isBlank(info) ? msgRes : info);
				detail.setSessionid(sessionid);
			}

			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return detail;
	}
	
	public  WhatsappTemplateMaster getTempMasterStageContent(String msgid, String agencycode, Long waid,
			Long stageCode, Long subStageCode) {
		try {

			QWhatsappTemplateMaster qtempM = QWhatsappTemplateMaster.whatsappTemplateMaster;
			QWhatsappTemplateMasterPK qtempMPk = qtempM.tempMasterPk;

			WhatsappTemplateMaster tempM = jpa
					.selectFrom(qtempM)
					.where(qtempM.remarks.eq(msgid)
							.and(qtempM.status.equalsIgnoreCase("Y"))
							.and(qtempMPk.agencycode.eq(agencycode))
							.and(qtempM.ischatyn.equalsIgnoreCase("Y"))
							.and(qtempMPk.stagecode.eq(stageCode))
							.and(qtempMPk.stagesubcode.eq(subStageCode))
							)
					.orderBy(qtempM.stage_order.asc())
					.fetchFirst();

			return tempM;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}
	
	
	
}
