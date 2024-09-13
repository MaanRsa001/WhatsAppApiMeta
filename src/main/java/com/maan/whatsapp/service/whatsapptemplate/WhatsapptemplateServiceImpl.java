package com.maan.whatsapp.service.whatsapptemplate;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileSystemDirectory;
import com.maan.whatsapp.entity.whatsapptemplate.WhatsappChatrecipiantMessageMaster;
import com.maan.whatsapp.entity.whatsapptemplate.WhatsappChatrecipiantMessageMasterpk;
import com.maan.whatsapp.entity.whatsapptemplate.WhatsappMessageMaster;
import com.maan.whatsapp.entity.whatsapptemplate.Whatsapptemplate;
import com.maan.whatsapp.entity.whatsapptemplate.WhatsapptemplatePK;
import com.maan.whatsapp.repository.whatsapp.PreInspectionDataDetailRepo;
import com.maan.whatsapp.repository.whatsapptemplate.WhatsapptemplateRepository;
import com.maan.whatsapp.repository.whatsapptemplate.WhatschatreciptemplateRepository;
import com.maan.whatsapp.repository.whatsapptemplate.WhatschattemplateRepository;
import com.maan.whatsapp.request.motor.DocumentResponse;
import com.maan.whatsapp.request.motor.PreFileUploadReq;
import com.maan.whatsapp.request.motor.TinyURLResponse;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchatrecipiantReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsappchattempReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatsapptemplateReq;
import com.maan.whatsapp.request.whatsapptemplate.WhatschatdeleteRes;
import com.maan.whatsapp.response.error.Error;
import com.maan.whatsapp.response.motor.FileUploadRes;
import com.maan.whatsapp.response.motor.GetPreinspectionImageRes;
import com.maan.whatsapp.response.motor.PreinspectionDetailsRes;
import com.maan.whatsapp.response.motor.PreinspectionImageRes;
import com.maan.whatsapp.response.motor.WACommonRes;
import com.maan.whatsapp.service.common.CommonService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class WhatsapptemplateServiceImpl implements WhatsapptemplateService {
	
	private Logger log=LogManager.getLogger(WhatsapptemplateServiceImpl.class);	
	
	@Autowired
	private WhatsapptemplateRepository whatsrepo;
	
	@Autowired
	private WhatschattemplateRepository whatschatrepo;
	
	@Autowired
	private WhatschatreciptemplateRepository whatschatreciprepo;
	
	@Autowired
	private PreInspectionDataDetailRepo preInsImgRepo;
	
	@Autowired
	private WhatsapptemplateValidation whatsappvalidate;
	
	@Autowired
	private CommonService cs;
	
	

	@Value("${tinyurl.token}")
	private String tokenencrp;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public List<WhatsapptemplateReq> gettemplatelist(String agencycode, String productid){
		List<WhatsapptemplateReq> reslist = new ArrayList<WhatsapptemplateReq>();
		List<Whatsapptemplate> list = whatsrepo.gettemplatelist(agencycode,productid);
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				String stagecode = list.get(i).getWhatsapptemplatepk().getStage_code().toString(); 
				String stagedesc = list.get(i).getStage_desc();
				WhatsapptemplateReq response= WhatsapptemplateReq.builder()
						.stagecode(stagecode)
						.stagedesc(stagedesc)
						.build();
				reslist.add(response);
			}
		}
		return reslist;
	}
	
	public List<WhatsappchattempReq> chattemplateparentlist(String messageid){
		List<WhatsappchattempReq> reslist = new ArrayList<WhatsappchattempReq>();
		List<WhatsappMessageMaster> list = new ArrayList<>();
		if(messageid.equalsIgnoreCase("common")){
			list = whatschatrepo.chattemplatecommonlist();
		}else{
			 list = whatschatrepo.chattemplateparentlist(messageid);
		}
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				String msgid = list.get(i).getMessageid(); 
				String msgdescen = list.get(i).getMessagedescen();
				String msgdescar = StringUtils.isBlank(list.get(i).getMessagedescar()) ? "" : list.get(i).getMessagedescar();
				String status = StringUtils.isBlank(list.get(i).getStatus()) ? "N" : list.get(i).getStatus();
				String effectivedate = simpleDateFormat.format(list.get(i).getEffectivedate());
				String entrydate =simpleDateFormat.format(list.get(i).getEntrydate());
				String remarks = StringUtils.isBlank(list.get(i).getRemarks()) ? "" : list.get(i).getRemarks();
				String iscommon = StringUtils.isBlank(list.get(i).getIscommonmsg()) ? "" : list.get(i).getIscommonmsg();
				String commonid = StringUtils.isBlank(list.get(i).getCommonmsgid()) ? "" : list.get(i).getCommonmsgid();
				WhatsappchattempReq response= WhatsappchattempReq.builder()
						.messageid(msgid)
						.messagedescen(msgdescen)
						.messagedescar(msgdescar)
						.status(status)
						.effectivedate(effectivedate)
						.entrydate(entrydate)
						.remarks(remarks)
						.iscommonyn(iscommon)
						.commonid(commonid)
						//.isButtonMsgYn(StringUtils.isBlank(list.get(i).getIs_buttonmsg())?"":list.get(i).getIs_buttonmsg())
						//.headerType(StringUtils.isBlank(list.get(i).getMsg_type())?"":list.get(i).getMsg_type())
						//.button1(StringUtils.isBlank(list.get(i).getMsg_button1())?"":list.get(i).getMsg_button1())
						//.button2(StringUtils.isBlank(list.get(i).getMsg_button2())?"":list.get(i).getMsg_button2())
						//.button3(StringUtils.isBlank(list.get(i).getMsg_button3())?"":list.get(i).getMsg_button3())
						//.headerImageName(StringUtils.isBlank(list.get(i).getImage_name())?"":list.get(i).getImage_name())
						//.headerImageUrl(StringUtils.isBlank(list.get(i).getImage_url())?"":list.get(i).getImage_url())
						.build();
				reslist.add(response);
			}
		}
		return reslist;
	}
	
	public List<WhatsappchatrecipiantReq> chattemplatechildlist(String parentmsgid){
		List<WhatsappchatrecipiantReq> reslist = new ArrayList<WhatsappchatrecipiantReq>();
		List<WhatsappChatrecipiantMessageMaster> list = whatschatreciprepo.chattemplatechildlist(parentmsgid);
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				String msgid = list.get(i).getWhatschatpk().getMessageid(); 
				String parentmessageid = list.get(i).getWhatschatpk().getParentmessageid();
				String description = list.get(i).getDescription();
				String useropted = list.get(i).getUseroptted_messageid().toString();
				String validationapi = StringUtils.isBlank(list.get(i).getValidationapi()) ? "" : list.get(i).getValidationapi();
				String apiusername = StringUtils.isBlank(list.get(i).getApiusername()) ? "" : list.get(i).getApiusername() ;
				String apipassword = StringUtils.isBlank(list.get(i).getApipassword()) ? "" : list.get(i).getApipassword();
				String reqstr = StringUtils.isBlank(list.get(i).getRequeststring()) ? "" : list.get(i).getRequeststring();
				String status = StringUtils.isBlank(list.get(i).getStatus()) ? "N" : list.get(i).getStatus();
				String isjob = StringUtils.isBlank(list.get(i).getIsjobyn()) ? "N" : list.get(i).getIsjobyn();
				String effectivedate = simpleDateFormat.format(list.get(i).getEffectivedate());
				String entrydate =simpleDateFormat.format(list.get(i).getEntrydate());
				String remarks = StringUtils.isBlank(list.get(i).getRemarks()) ? "" : list.get(i).getRemarks();
				String inputyn = list.get(i).getIsinput();
				String inputkey = list.get(i).getRequest_key();
				String inputvalue = list.get(i).getInput_value();
				String iscommon = list.get(i).getIscommonmsg();
				String commonid = list.get(i).getCommonmsgid();
				WhatsappchatrecipiantReq response= WhatsappchatrecipiantReq.builder()
						.messageid(msgid)
						.parentmessageid(parentmessageid)
						.description(description)
						.useroptted_messageid(useropted)
						.validationapi(validationapi)
						.apiusername(apiusername)
						.apipassword(apipassword)
						.requeststring(reqstr)
						.isjobyn(isjob)
						.status(status)
						.effectivedate(effectivedate)
						.entrydate(entrydate)
						.remarks(remarks)
						.inputkey(inputkey)
						.inputvalue(inputvalue)
						.isinputyn(inputyn)
						.iscommonyn(iscommon)
						.commonid(commonid)
						.build();
				reslist.add(response);
			}
		}
		return reslist;
	}
	
	public WhatsappchatrecipiantReq chattemplatechildlistedit(String messageid, String parentmessageid){
		WhatsappchatrecipiantReq reslist = new WhatsappchatrecipiantReq();
		List<WhatsappChatrecipiantMessageMaster> list = whatschatreciprepo.chattemplatechildlistedit(messageid, parentmessageid);
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				String msgid = list.get(i).getWhatschatpk().getMessageid(); 
				String parentmessageid1 = list.get(i).getWhatschatpk().getParentmessageid();
				String description = list.get(i).getDescription();
				String useropted = list.get(i).getUseroptted_messageid().toString();
				String validationapi = StringUtils.isBlank(list.get(i).getValidationapi()) ? "" : list.get(i).getValidationapi();
				String apiusername = StringUtils.isBlank(list.get(i).getApiusername()) ? "" : list.get(i).getApiusername() ;
				String apipassword = StringUtils.isBlank(list.get(i).getApipassword()) ? "" : list.get(i).getApipassword();
				String reqstr = StringUtils.isBlank(list.get(i).getRequeststring()) ? "" : list.get(i).getRequeststring();
				String status = StringUtils.isBlank(list.get(i).getStatus()) ? "N" : list.get(i).getStatus();
				String isjob = StringUtils.isBlank(list.get(i).getIsjobyn()) ? "N" : list.get(i).getIsjobyn();
				String effectivedate = simpleDateFormat.format(list.get(i).getEffectivedate());
				String entrydate =simpleDateFormat.format(list.get(i).getEntrydate());
				String remarks = StringUtils.isBlank(list.get(i).getRemarks()) ? "" : list.get(i).getRemarks();
				String useropt = list.get(i).getUseroptted_messageid().toString();
				String inputyn = list.get(i).getIsinput();
				String inputkey = list.get(i).getRequest_key();
				String inputvalue = list.get(i).getInput_value();
				String iscommon = list.get(i).getIscommonmsg();
				String commonid = list.get(i).getCommonmsgid();
				WhatsappchatrecipiantReq response= WhatsappchatrecipiantReq.builder()
						.messageid(msgid)
						.parentmessageid(parentmessageid1)
						.description(description)
						.useroptted_messageid(useropted)
						.validationapi(validationapi)
						.apiusername(apiusername)
						.apipassword(apipassword)
						.requeststring(reqstr)
						.isjobyn(isjob)
						.status(status)
						.effectivedate(effectivedate)
						.entrydate(entrydate)
						.remarks(remarks)
						.useroptted_messageid(useropt)
						.inputkey(inputkey)
						.inputvalue(inputvalue)
						.isinputyn(inputyn)
						.iscommonyn(iscommon)
						.commonid(commonid)
						.build();
				reslist = response;
			}
		}
		return reslist;
	}

	
	public WhatsappchattempReq chattemplateparentsave(WhatsappchattempReq req){
		WhatsappchattempReq response = new WhatsappchattempReq();
		List<Error> list = whatsappvalidate.getchatparentsavevalidate(req);
		if(list.size() == 0){
			try{
				String msgid = StringUtils.isBlank(req.getMessageid()) ? req.getMessageidtype().trim() + whatschatrepo.getmaxmsgid(req.getMessageidtype(), new Long(req.getMessageidtype().trim().length() +3)) : req.getMessageid(); 
				String msgdescen = StringUtils.isBlank(req.getMessagedescen()) ? "" : req.getMessagedescen().replace("~", "\\");
				String msgdescar = StringUtils.isBlank(req.getMessagedescar()) ? "" : req.getMessagedescar().replace("~", "\\");
				String status = req.getStatus();
				Date effectivedate = simpleDateFormat.parse(req.getEffectivedate());
				Date entrydate =new Date();
				String remarks = req.getRemarks();
				String iscommon = req.getIscommonyn();
				String commonid = req.getCommonid();
				WhatsappMessageMaster data = WhatsappMessageMaster.builder()
						.messageid(msgid)
						.messagedescen(msgdescen)
						.messagedescar(msgdescar)
						.status(status)
						.effectivedate(effectivedate)
						.entrydate(entrydate)
						.remarks(remarks)
						.iscommonmsg(iscommon)
						.commonmsgid(commonid)
						//.is_buttonmsg(req.getIsButtonMsgYn()==null?"":req.getIsButtonMsgYn())
						//.msg_type(req.getHeaderType()==null?"":req.getHeaderType())
						//.msg_button1(req.getButton1()==null?"":req.getButton1())
						//.msg_button2(req.getButton2()==null?"":req.getButton2())
						//.msg_button3(req.getButton3()==null?"":req.getButton3())
						//.image_name(req.getHeaderImageName()==null?"":req.getHeaderImageName())
						//.image_url(req.getHeaderImageUrl()==null?"":req.getHeaderImageUrl())
						.build();
				whatschatrepo.save(data);
				response.setStatus("Success");
			}catch (Exception e) {
				response.setStatus("Failed");
				log.error(e);
			}
		}else{
			response.setStatus("Failed");
			response.setErrors(list);
		}
		return response;
	}
	
	
	public WhatsappchatrecipiantReq chattemplateanssave(WhatsappchatrecipiantReq req){
		WhatsappchatrecipiantReq response = new WhatsappchatrecipiantReq();
		List<Error> list = whatsappvalidate.getchattemplateparentsavevalidate(req);
		if(list.size() == 0){
			try{
				String backid = req.getBackid();
				String msgid = StringUtils.isBlank(req.getMessageid()) ? req.getMessageidtype().trim() + whatschatreciprepo.geansmaxmsgid(req.getMessageidtype(), new Long(req.getMessageidtype().trim().length() +3)) : req.getMessageid(); 
				String parentmessageid = req.getParentmessageid();
				String description = req.getDescription();
				Long useropted =StringUtils.isNotBlank(req.getUseroptted_messageid())? Long.parseLong(req.getUseroptted_messageid()) : whatschatreciprepo.getmaxuseropt(req.getParentmessageid());
				String validationapi = StringUtils.isBlank(req.getValidationapi()) ? "" : req.getValidationapi();
				String apiusername = StringUtils.isBlank(req.getApiusername()) ? "" : req.getApiusername() ;
				String apipassword = StringUtils.isBlank(req.getApipassword()) ? "" : req.getApipassword();
				String reqstr = StringUtils.isBlank(req.getRequeststring()) ? "" : req.getRequeststring();
				String status = StringUtils.isBlank(req.getStatus()) ? "" : req.getStatus();
				String isjob = StringUtils.isBlank(req.getIsjobyn()) ? "" : req.getIsjobyn();
				Date effectivedate = simpleDateFormat.parse(req.getEffectivedate());
				Date entrydate = new Date();
				String remarks = StringUtils.isBlank(req.getRemarks()) ? "" : req.getRemarks();
				String inputyn = StringUtils.isBlank(req.getIsinputyn()) ? "" : req.getIsinputyn();
				String inputkey = StringUtils.isBlank(req.getInputkey()) ? "" : req.getInputkey();
				String inputvalue = StringUtils.isBlank(req.getInputvalue()) ? "" : req.getInputvalue();
				String iscommon = StringUtils.isBlank(req.getIscommonyn()) ? "" : req.getIscommonyn() ;
				String commonid =StringUtils.isBlank( req.getCommonid()) ? "" : req.getCommonid() ;
				if( StringUtils.isNotBlank( backid) && !("undefined".equals( backid)) && whatschatreciprepo.getcountparentidbased(parentmessageid) == 0){
					WhatsappChatrecipiantMessageMasterpk datapk1 = WhatsappChatrecipiantMessageMasterpk.builder()
							.messageid(backid)
							.parentmessageid(parentmessageid)
							.build();

					WhatsappChatrecipiantMessageMaster data1 = WhatsappChatrecipiantMessageMaster.builder()
							.whatschatpk(datapk1)
							.description("Go Back")
							.useroptted_messageid(9L)
							.isjobyn("N")
							.status("Y")
							.effectivedate(effectivedate)
							.entrydate(entrydate)
							.isinput("N")
							.build();
					whatschatreciprepo.save(data1);
				}
				
				WhatsappChatrecipiantMessageMasterpk datapk = WhatsappChatrecipiantMessageMasterpk.builder()
						.messageid(msgid)
						.parentmessageid(parentmessageid)
						.build();

				WhatsappChatrecipiantMessageMaster data = WhatsappChatrecipiantMessageMaster.builder()
						.whatschatpk(datapk)
						.description(description)
						.useroptted_messageid(useropted)
						.validationapi(validationapi)
						.apiusername(apiusername)
						.apipassword(apipassword)
						.requeststring(reqstr)
						.isjobyn(isjob)
						.status(status)
						.effectivedate(effectivedate)
						.entrydate(entrydate)
						.remarks(remarks)
						.request_key(inputkey)
						.input_value(inputvalue)
						.isinput(inputyn)
						.iscommonmsg(iscommon)
						.commonmsgid(commonid)
						.build();
				whatschatreciprepo.save(data);
				response.setStatus("Success");
				
			}catch (Exception e) {
				response.setStatus("Failed");
				log.error(e);
			}
		}else{
			response.setStatus("Failed");
			response.setErrors(list);
		}
		return response;
	}
	
	public List<WhatsapptemplateReq> maintemplatechatlist(String remarksid){
		List<WhatsapptemplateReq> reslist = new ArrayList<WhatsapptemplateReq>();
		List<Whatsapptemplate> list = whatsrepo.maintemplatechatlist(remarksid);
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				String agencycode = list.get(i).getWhatsapptemplatepk().getAgency_code();
				String stagecode = list.get(i).getWhatsapptemplatepk().getStage_code().toString(); 
				String stagesubcode = list.get(i).getWhatsapptemplatepk().getStagesub_code().toString();
				String productid = list.get(i).getWhatsapptemplatepk().getProduct_id().toString();
				String stagedesc = list.get(i).getStage_desc();
				String stagesubdesc = list.get(i).getStagesub_desc();
				String messagecontenten = list.get(i).getMessage_content_en();
				String messacontentar = list.get(i).getMessage_content_ar();
				String regardsen = list.get(i).getMessage_regards_en();
				String regardsar = list.get(i).getMessage_regards_ar();
				String fileyn = list.get(i).getFile_yn();
				String filepath = list.get(i).getFile_path();
				String entrydate = list.get(i).getEntry_date().toString();
				String status = list.get(i).getStatus();
				String remarks = list.get(i).getRemarks();
				String stageorder = list.get(i).getStage_order() == null ? null : list.get(i).getStage_order().toString();
				String ischatyn = list.get(i).getIschatyn();
				String isreplyyn = list.get(i).getIsreplyyn();
				String isapicallyn = list.get(i).getIsapicall();
				String apiurl =list.get(i).getApiurl();
				String apiauth = list.get(i).getApiauth();
				String apimethod = list.get(i).getApimethod();
				String responsestr = list.get(i).getResponsestring();
				String errorrespstr = list.get(i).getErrorrespstring();
				String requestkey =list.get(i).getRequest_key();
				String reqstr = list.get(i).getRequest_string();
				String isskipyn = list.get(i).getIsskipyn();
				String isdocupyn = list.get(i).getIsdocuplyn();
				String isapivalidateyn = list.get(i).getIsvalidationapi();
				String isButtonMsg =list.get(i).getIsButtonMsg();
				String isApiResYn =list.get(i).getIsReponseYn();
				String footer ="";String body ="";String button1="";String button2="";
				String button3="";String headerType="";String headerTxt="";String imageUrl="";
				String imageName="";
				if("Y".equals(isButtonMsg)) {
					 footer =list.get(i).getButtonFooter();
					 body =list.get(i).getButtonBody();
					 button1=StringUtils.isBlank(list.get(i).getButton1())?"":list.get(i).getButton1();
					 button2=StringUtils.isBlank(list.get(i).getButton2())?"":list.get(i).getButton2();
					 button3=StringUtils.isBlank(list.get(i).getButton3())?"":list.get(i).getButton3();
					 headerType =list.get(i).getMsgType();
					 headerTxt =list.get(i).getButtonHeader();
					 imageUrl =list.get(i).getImageUrl();
					 imageName =list.get(i).getImageName();
				}
				WhatsapptemplateReq response= WhatsapptemplateReq.builder()
						.agencycode(agencycode)
						.stagecode(stagecode)
						.stagesubcode(stagesubcode)
						.productid(productid)
						.stagedesc(stagedesc)
						.stagesubdesc(stagesubdesc)
						.messagecontenten(messagecontenten)
						.messagecontentar(messacontentar)
						.messageregardsen(regardsen)
						.messageregardsar(regardsar)
						.fileyn(fileyn)
						.filepath(filepath)
						.entrydate(entrydate)
						.status(status)
						.remarks(remarks)
						.stageorder(stageorder)
						.ischatyn(ischatyn)
						.isreplyyn(isreplyyn)
						.isapicall(isapicallyn)
						.apiurl(apiurl)
						.apiauth(apiauth)
						.apimethod(apimethod)
						.responsestring(responsestr)
						.errorrespstring(errorrespstr)
						.requestkey(requestkey)
						.requeststring(reqstr)
						.isskipyn(isskipyn)
						.apivalidationyn(isapivalidateyn)
						.docuploadyn(isdocupyn)
						.button1(button1)
						.button2(button2)
						.button3(button3)
						.headerImageName(imageName)
						.headerImageUrl(imageUrl)
						.msgBody(body)
						.msgFooter(footer)
						.headerText(headerTxt)
						.headerType(headerType)
						.isApiResYn(isApiResYn)
						.isButtonMsgYn(isButtonMsg)
						.build();
				reslist.add(response);
			}
		}
		return reslist;
	}

	public WhatsapptemplateReq getmaintemplateedit(String productid1, String agencycode1, String stagecode1, String substagecode){
		List<WhatsapptemplateReq> reslist = new ArrayList<WhatsapptemplateReq>();
		List<Whatsapptemplate> list = whatsrepo.getmaintemplateedit(productid1, agencycode1, stagecode1, substagecode);
		if(list.size() > 0){
			for(int i=0;i<list.size();i++){
				String agencycode = list.get(i).getWhatsapptemplatepk().getAgency_code();
				String stagecode = list.get(i).getWhatsapptemplatepk().getStage_code().toString(); 
				String stagesubcode = list.get(i).getWhatsapptemplatepk().getStagesub_code().toString();
				String productid = list.get(i).getWhatsapptemplatepk().getProduct_id().toString();
				String stagedesc = list.get(i).getStage_desc();
				String stagesubdesc = list.get(i).getStagesub_desc();
				String messagecontenten = list.get(i).getMessage_content_en();
				String messacontentar = list.get(i).getMessage_content_ar();
				String regardsen = list.get(i).getMessage_regards_en();
				String regardsar = list.get(i).getMessage_regards_ar();
				String fileyn = list.get(i).getFile_yn();
				String filepath = list.get(i).getFile_path();
				String entrydate = list.get(i).getEntry_date().toString();
				String status = list.get(i).getStatus();
				String remarks = list.get(i).getRemarks();
				String stageorder = list.get(i).getStage_order() == null ? null : list.get(i).getStage_order().toString();
				String ischatyn = list.get(i).getIschatyn();
				String isreplyyn = list.get(i).getIsreplyyn();
				String isapicallyn = list.get(i).getIsapicall();
				String apiurl =list.get(i).getApiurl();
				String apiauth = list.get(i).getApiauth();
				String apimethod = list.get(i).getApimethod();
				String responsestr = list.get(i).getResponsestring();
				String errorrespstr = list.get(i).getErrorrespstring();
				String requestkey =list.get(i).getRequest_key();
				String reqstr = list.get(i).getRequest_string();
				String isskipyn = list.get(i).getIsskipyn();
				String isdocupyn = list.get(i).getIsdocuplyn();
				String isapivalidateyn = list.get(i).getIsvalidationapi();
				String responsestrar = list.get(i).getResponsestring_ar();
				String isButtonMsg =list.get(i).getIsButtonMsg();
				String isApiResYn =list.get(i).getIsReponseYn();
				String footer ="";String body ="";String button1="";String button2="";
				String button3="";String headerType="";String headerTxt="";String imageUrl="";
				String imageName="";
				if("Y".equals(isButtonMsg) || "Y".equalsIgnoreCase(isApiResYn)) {
					 footer =list.get(i).getButtonFooter();
					 body =list.get(i).getButtonBody();
					 button1=StringUtils.isBlank(list.get(i).getButton1())?"":list.get(i).getButton1();
					 button2=StringUtils.isBlank(list.get(i).getButton2())?"":list.get(i).getButton2();
					 button3=StringUtils.isBlank(list.get(i).getButton3())?"":list.get(i).getButton3();
					 headerType =list.get(i).getMsgType();
					 headerTxt =list.get(i).getButtonHeader();
					 imageUrl =list.get(i).getImageUrl();
					 imageName =list.get(i).getImageName();
				}
				WhatsapptemplateReq response= WhatsapptemplateReq.builder()
						.agencycode(agencycode)
						.stagecode(stagecode)
						.stagesubcode(stagesubcode)
						.productid(productid)
						.stagedesc(stagedesc)
						.stagesubdesc(stagesubdesc)
						.messagecontenten(messagecontenten)
						.messagecontentar(messacontentar)
						.messageregardsen(regardsen)
						.messageregardsar(regardsar)
						.fileyn(fileyn)
						.filepath(filepath)
						.entrydate(entrydate)
						.status(status)
						.remarks(remarks)
						.stageorder(stageorder)
						.ischatyn(ischatyn)
						.isreplyyn(isreplyyn)
						.isapicall(isapicallyn)
						.apiurl(apiurl)
						.apiauth(apiauth)
						.apimethod(apimethod)
						.responsestring(responsestr)
						.errorrespstring(errorrespstr)
						.requestkey(requestkey)
						.requeststring(reqstr)
						.isskipyn(isskipyn)
						.apivalidationyn(isapivalidateyn)
						.docuploadyn(isdocupyn)
						.responsestringar(responsestrar)
						.button1(button1)
						.button2(button2)
						.button3(button3)
						.headerImageName(imageName)
						.headerImageUrl(imageUrl)
						.msgBody(body)
						.msgFooter(footer)
						.headerText(headerTxt)
						.headerType(headerType)
						.isApiResYn(isApiResYn)
						.isButtonMsgYn(isButtonMsg)
						.build();
				reslist.add(response);
			}
		}
		return reslist.get(0);
	}
	
	public WhatsapptemplateReq maintemplatesave(WhatsapptemplateReq req){
		WhatsapptemplateReq response = new WhatsapptemplateReq();
		List<Error> list = whatsappvalidate.getmainmplatesavevalidate(req);
		if(list.size() == 0){
			try{
				String remarks = req.getRemarks();
				Long productid = Long.parseLong(req.getProductid()) ;
				String agencycode = req.getAgencycode();
				Long stagecode = StringUtils.isBlank(req.getStagecode()) ? null :  Long.parseLong(req.getStagecode());
				Long checkstagecode = null;
				if(stagecode == null)
				checkstagecode = whatsrepo.getcheckstagecode(productid,remarks);
				if(checkstagecode !=  null)
					stagecode = checkstagecode;
				if(stagecode == null && checkstagecode == null)
					stagecode = whatsrepo.getmaxstagecode(productid, agencycode);
				Long stagesubcode = StringUtils.isBlank(req.getStagesubcode())? whatsrepo.getmaxstagesubcode(productid, agencycode ,stagecode) : Long.parseLong(req.getStagesubcode());
				String stagedesc = StringUtils.isBlank(req.getStagedesc()) ? "" : req.getStagedesc().replace("~", "\\");
				String stagesubdesc = StringUtils.isBlank(req.getStagesubdesc()) ? "" : req.getStagesubdesc().replace("~", "\\");
				Long stageorder = StringUtils.isBlank(req.getStageorder()) ? null : Long.parseLong(req.getStageorder());
				String file = req.getFileyn();
				String filepath = StringUtils.isBlank(req.getFilepath()) ? "" : req.getFilepath().replace("~", "\\");
				String status = req.getStatus();
				String ischat = req.getIschatyn();
				String isreply = req.getIsreplyyn();
				String apicall = req.getIsapicall();
				String apiurl = StringUtils.isBlank(req.getApiurl()) ? "" : req.getApiurl().replace("~", "\\");
				String apiauth = StringUtils.isBlank(req.getApiauth()) ? "" : req.getApiauth().replace("~", "\\");
				String apimethod = req.getApimethod();
				String responsestr = StringUtils.isBlank(req.getResponsestring()) ? "" : req.getResponsestring().replace("~", "\\");
				String errorresponsestr = StringUtils.isBlank(req.getErrorrespstring()) ? "" : req.getErrorrespstring().replace("~", "\\");
				String requestkey = req.getRequestkey();
				String msgcontenten = StringUtils.isBlank(req.getMessagecontenten()) ? "" : req.getMessagecontenten().replace("~", "\\");
				String msgcontentar = StringUtils.isBlank(req.getMessagecontentar()) ? "" : req.getMessagecontentar().replace("~", "\\");
				String msgregardsen = StringUtils.isBlank(req.getMessageregardsen()) ? "" : req.getMessageregardsen().replace("~", "\\");
				String msgregardsar = StringUtils.isBlank(req.getMessageregardsar()) ? "" : req.getMessageregardsar().replace("~", "\\");
				String reqstr = StringUtils.isBlank(req.getRequeststring()) ? "" : req.getRequeststring().replace("~", "\"") ;
				String isskipyn = req.getIsskipyn();
				String isdocupyn = req.getDocuploadyn();
				String isapivalidateyn = req.getApivalidationyn();
				String responsestrar = StringUtils.isBlank(req.getResponsestringar()) ? "" : req.getResponsestringar().replace("~", "\\");
				String isApiResYn =StringUtils.isBlank(req.getIsApiResYn())?"N": req.getIsApiResYn();

				String isButtonMsg =StringUtils.isBlank(req.getIsButtonMsgYn())?"N": req.getIsButtonMsgYn();
				String headerType ="";String headerText="";String imageUrl ="";String imageName="";
				String msgBody ="";String msgfooter =""; String button1=""; String button2=""; String button3="";
				if("Y".equals(isButtonMsg) || "Y".equals(isApiResYn)) {
					headerType =req.getHeaderType();
					headerText =req.getHeaderText();
					imageUrl=req.getHeaderImageUrl();
					imageName=StringUtils.isBlank(req.getHeaderImageName())?"":req.getHeaderImageName();
					msgBody=StringUtils.isBlank(req.getMsgBody())?"":req.getMsgBody();
					msgfooter=StringUtils.isBlank(req.getMsgFooter())?"":req.getMsgFooter();
					button1 =StringUtils.isBlank(req.getButton1())?"":req.getButton1();
					button2 =StringUtils.isBlank(req.getButton2())?"":req.getButton2();
					button3 =StringUtils.isBlank(req.getButton3())?"":req.getButton3();
				}
				
				
				WhatsapptemplatePK datapk = WhatsapptemplatePK.builder()
						.product_id(productid)
						.agency_code(agencycode)
						.stage_code(stagecode)
						.stagesub_code(stagesubcode)
						.build();

				Whatsapptemplate data = Whatsapptemplate.builder()
						.Whatsapptemplatepk(datapk)
						.stage_desc(stagedesc)
						.stagesub_desc(stagesubdesc)
						.remarks(remarks)
						.stage_order(stageorder)
						.file_yn(file)
						.file_path(filepath)
						.status(status)
						.ischatyn(ischat)
						.isreplyyn(isreply)
						.isapicall(apicall)
						.apiurl(apiurl)
						.apiauth(apiauth)
						.apimethod(apimethod)
						.responsestring(responsestr)
						.errorrespstring(errorresponsestr)
						.message_content_en(msgcontenten)
						.message_content_ar(msgcontentar)
						.message_regards_en(msgregardsen)
						.message_regards_ar(msgregardsar)
						.request_key(requestkey)
						.request_string(reqstr)
						.isskipyn(isskipyn)
						.isvalidationapi(isapivalidateyn)
						.isdocuplyn(isdocupyn)
						.entry_date(new Date())
						.responsestring_ar(responsestrar)
						.isreplyyn(isreply)
						.button1(button1)
						.button2(button2)
						.button3(button3)
						.buttonBody(msgBody)
						.buttonFooter(msgfooter)
						.buttonHeader(headerText)
						.isButtonMsg(isButtonMsg)
						.imageUrl(imageUrl)
						.imageName(imageName)
						.msgType(headerType)
						.isReponseYn(isApiResYn)
						.build();
				whatsrepo.save(data);
				response.setStatus("Success");
			}catch (Exception e) {
				response.setStatus("Failed");
				log.error(e);
			}
		}else{
			response.setStatus("Failed");
			response.setErrors(list);
		}
		return response;
	}
	
	public List<WhatschatdeleteRes> getchatdeletepreview(String id){
		List<WhatschatdeleteRes> resist = new ArrayList<>();
		
		Map<String,Integer> dellist = new HashMap<>();
		List<List<String>> queslist = new ArrayList<>();
		List<WhatsappMessageMaster> parentlist = questiondeletelist(id);
		List<WhatsappChatrecipiantMessageMaster> childlist = optiondeletelist(id);
		if(parentlist.size() > 0){
		dellist.put(parentlist.get(0).getMessageid(),optiondeleteidlist(childlist).size());
		queslist.add(optiondeleteidlist(childlist));
		dellist.put(id,optiondeleteidlist(childlist).size());
		}
		
		for(int i=0;i<queslist.size();i++){
			for(int j=0;j<queslist.get(i).size();j++){
				childlist = optiondeletelist(queslist.get(i).get(j));
				dellist.put(queslist.get(i).get(j),optiondeleteidlist(childlist).size());
				queslist.add(optiondeleteidlist(childlist));
			}
		}
		
		System.out.println(dellist+"================="+queslist);
		System.out.println();
		for(Map.Entry<String, Integer> entry : dellist.entrySet()){
			if(entry.getValue() != 0){
			List<WhatsappMessageMaster> list = whatschatrepo.chattemplateparentlist(entry.getKey());
				if(list != null && list.size() != 0){
					WhatschatdeleteRes delres = WhatschatdeleteRes.builder()
							.messageid(list.get(0).getMessageid())
							.description(list.get(0).getMessagedescen())
							.optioncount(entry.getValue().toString())
							.build();
					
							 resist.add(delres);
				}
			}
			
		}
		return resist;
	}
	
	public List<WhatsappMessageMaster> questiondeletelist(String id){
		return whatschatrepo.getparentdeletepreview(id);
	}
	
	public List<WhatsappChatrecipiantMessageMaster> optiondeletelist(String id){
		return whatschatreciprepo.getchilddeletepreview(id);
	}
	
	public  List<String> optiondeleteidlist(List<WhatsappChatrecipiantMessageMaster> childlist){
		List<String> list =new ArrayList<>();
		for(int i = 0; i < childlist.size() ; i++ )
			if(childlist.get(i).getUseroptted_messageid() != 0 && childlist.get(i).getUseroptted_messageid() != 9)
			list.add(childlist.get(i).getWhatschatpk().getMessageid());
		return list;
		
    }

	public WhatschatdeleteRes chatdeleterec(String id){
		WhatschatdeleteRes res = new WhatschatdeleteRes();
		List<WhatschatdeleteRes> delreclist = getchatdeletepreview(id);
		try{
			if(delreclist != null && delreclist.size() != 0){
				whatschatreciprepo.getchildmappedoptiondel(id);
				for(int i=0; i<delreclist.size();i++){
					String delid = delreclist.get(i).getMessageid();
					 whatschatrepo.getparentdeleterec(delid);
					 whatschatreciprepo.getchilddeleterec(delid);
					 res.setDescription("Successfully Deleted");
				}
			}
			
		}catch (Exception e) {
			res.setDescription("Failed to delete");
			log.error(e);
		}
		return res;
	}

	@Override
	public WACommonRes getPreinspectionImageByDate() {
		WACommonRes response = new WACommonRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<GetPreinspectionImageRes> rlist =new ArrayList<GetPreinspectionImageRes>();
		try {
			List<Map<String,Object>> list =preInsImgRepo.getPreInspectionImage();
			if(!CollectionUtils.isEmpty(list)) {
				for(Map<String,Object> p:list){
					GetPreinspectionImageRes imageRes = GetPreinspectionImageRes.builder()
							.totalCount(p.get("TOTAL_COUNT")==null?"":p.get("TOTAL_COUNT").toString())
							.entryDate(p.get("ENTRY_DATE")==null?"":sdf.format(p.get("ENTRY_DATE")))
							.build();
					rlist.add(imageRes);
					
				}
				response.setResponse(rlist);
				response.setMessage("SUCCESS");
			}else {
				response.setResponse(rlist);
				response.setMessage("FAILED");
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public WACommonRes getPreinspectionImagesByDate(String entry_date) {
		WACommonRes response = new WACommonRes();
		List<PreinspectionDetailsRes> rlist =new ArrayList<PreinspectionDetailsRes>();
		try {
			List<Map<String,Object>> list =preInsImgRepo.getPreInspectionImageByDate(entry_date);
			if(!CollectionUtils.isEmpty(list)) {
				Map<String,List<Map<String,Object>>> d= list.stream()
						.collect(Collectors.groupingBy(p->p.get("TRANID").toString(),Collectors.toList()));
				for(Map.Entry<String,List<Map<String,Object>>> entry:d.entrySet()) {
					List<Map<String,Object>> value =entry.getValue();
					List<PreinspectionImageRes> image =new ArrayList<PreinspectionImageRes>();
					for(Map<String,Object> p : value) {
						PreinspectionImageRes imageRes = PreinspectionImageRes.builder()
								.imageName(p.get("IMAGENAME")==null?"":p.get("IMAGENAME").toString().trim())
								.imagePath(p.get("IMAGEFILEPATH")==null || p.get("IMAGEFILEPATH").equals("99")?"":p.get("IMAGEFILEPATH").toString().replace("\\", "//"))
								.originalFileName(p.get("ORIGINAL_FILE_NAME")==null?"":p.get("ORIGINAL_FILE_NAME").toString().trim())
								.build();
						image.add(imageRes);
					}
					PreinspectionDetailsRes pid = PreinspectionDetailsRes.builder()
							.chassisNo(value.get(0).get("CHASSISNO")==null?"":value.get(0).get("CHASSISNO").toString())
							.mobileNo(value.get(0).get("MOBILENO")==null?"":value.get(0).get("MOBILENO").toString())
							.registrationNo(value.get(0).get("REGISTRAIONNO")==null?"":value.get(0).get("REGISTRAIONNO").toString())
							.transactionId(value.get(0).get("TRANID")==null?"":value.get(0).get("TRANID").toString())
							.customerName(value.get(0).get("SENDERNAME")==null?"":value.get(0).get("SENDERNAME").toString())
							.image(image)
							.build();
					rlist.add(pid);
				}
						
				response.setResponse(rlist);
				response.setMessage("SUCCESS");
			}else {
				response.setResponse(rlist);
				response.setMessage("FAILED");
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public WACommonRes getPreinspectionImagesByTranId(String tranId) {
		WACommonRes response = new WACommonRes();
		List<PreinspectionImageRes> rlist =new ArrayList<PreinspectionImageRes>();
		try {
			List<Map<String,Object>> list =preInsImgRepo.getPreInspectionImageByTranId(tranId);
			if(!CollectionUtils.isEmpty(list)) {
				for(Map<String,Object> p:list){
					PreinspectionImageRes imageRes = PreinspectionImageRes.builder()
							.imageName(p.get("IMAGENAME")==null?"":p.get("IMAGENAME").toString().trim())
							.imagePath(p.get("IMAGEFILEPATH")==null || p.get("IMAGEFILEPATH").equals("99") || p.get("IMAGEFILEPATH").toString().equalsIgnoreCase("skip") ?"":p.get("IMAGEFILEPATH").toString().replace("\\", "//"))
							.originalFileName(p.get("ORIGINAL_FILE_NAME")==null?"":p.get("ORIGINAL_FILE_NAME").toString().trim())
							.build();
					rlist.add(imageRes);
					
				}
				response.setResponse(rlist);
				response.setMessage("SUCCESS");
			}else {
				response.setResponse(rlist);
				response.setMessage("FAILED");
			}
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public FileUploadRes uploadFile(PreFileUploadReq req) {
		FileUploadRes response = new FileUploadRes();
		String isCamera ="N";
		try {
			log.info("upload file request ::: "+cs.reqPrint(req));
			final String constant_path=new CommonService().getwebserviceurlProperty().get("wa.preins.image.path").toString().trim();
				String exif_status ="";
				Date exifDate =null;
				String base64Image = req.getBase64().split(",")[1];
				byte [] image  =Base64.getDecoder().decode(base64Image);
				String [] array =req.getOriginalFileName().split("[.]");
				String file_path =constant_path+array[0]+"_"+System.currentTimeMillis()+"."+array[1];
				Path path =Paths.get(file_path);
				Files.write(path, image);
				File file =new File(file_path);		
				
				Metadata metadata1 = ImageMetadataReader.readMetadata(file);			          			              
				if("N".equals(isCamera)) {
				    ExifSubIFDDirectory directory = metadata1.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
				    log.info("/metadata response :"+cs.reqPrint(metadata1));
				    log.info("/ExifSubIFDDirectory response :"+cs.reqPrint(directory));
				    if(directory!=null) {
					    	exifDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);					   
					    	log.info("Extracted exifDate from Image ==>"+ exifDate);
					    	if(exifDate!=null) {
						    	LocalDate systemDate =LocalDate.now();			            		 
							    LocalDate date = exifDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
							    if(date.isBefore(systemDate) || date.isAfter(systemDate)) {
							    	//exif_status="INVALID";
							    	exif_status="VALID";
							    	log.info("Image date invalid ==>"+ date);
							    }else {
							    	//log.info("Image date valid ==>"+ date);
							    	exif_status="VALID";
							    }
					    	}else {
					    		//exif_status="INVALID";
					    		exif_status="VALID";
					    	}
					    	
					 }else {
						 
						 //exif_status="INVALID";
						 exif_status="VALID";
						 
					 }
				}else if ("Y".equals(isCamera)) {
					exif_status ="VALID";
					FileSystemDirectory fsd = metadata1.getFirstDirectoryOfType(FileSystemDirectory.class);					   
					exifDate=fsd.getDate(3);
					
				}
			preInsImgRepo.insertImageDetails(req.getTranId(),req.getFileName(),file_path,exifDate,exif_status,array[1],isCamera);			
			response.setResponse("SUCCESS");
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			response.setResponse("FAILED");
		}
		return response;
	}
	public String getTinyUrl(String encryptedURL) {
		String resp ="";
		try {
			final String tinyurl="http://api.tinyurl.com/create?api_token="+tokenencrp;
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
			headers.setContentType(MediaType.APPLICATION_JSON);
			String request="{ \"url\": \""+ encryptedURL+"\"}";

			HttpEntity<Object> entityReq = new HttpEntity<>(request, headers);
			log.info("tiny url request--> : " + request);
			log.info("tiny url url--> : " + tinyurl);
			ResponseEntity<TinyURLResponse> response = restTemplate.postForEntity(tinyurl, entityReq, TinyURLResponse.class);
			resp = response.getBody().getData().getTiny_url();

		}catch (Exception e) {
			e.printStackTrace();
		}
		return resp;
			
		}

	@Override
	public String sendWhatsappMessage(String tranId) {
		try {
			List<Map<String,Object>> list =preInsImgRepo.getGroupOfRecordByTranId(tranId);
			List<Map<String,Object>> list_2=preInsImgRepo.getMobileNoByTranId(tranId);
			
			String mobile_number=list_2.get(0).get("WHATSAPPID")==null?"919566362141":list_2.get(0).get("WHATSAPPID").toString();
			String customer_name=list_2.get(0).get("SENDERNAME")==null?"Alliance":list_2.get(0).get("SENDERNAME").toString();
			Long valid_records=list.stream()
					.filter(l ->(l.get("EXIF_IMAGE_STATUS").equals("VALID") && !l.get("EXIF_IMAGE_STATUS").toString().isEmpty()))
					.count();
			
			Long invalid_records=list.stream()
					.filter(l ->(l.get("EXIF_IMAGE_STATUS").equals("INVALID") &&!l.get("EXIF_IMAGE_STATUS").toString().isEmpty()))
					.count();
			
			Long total_records=valid_records+invalid_records;
			
			String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
			String msgurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.sendSessionMessage");			
			String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

			OkHttpClient okhttp = new OkHttpClient.Builder()
					.readTimeout(30, TimeUnit.SECONDS)
					.build();
			
			String url =commonurl+msgurl;
			
			String msgs ="Dear {NAME},\n\nWe have received your documents"
					+ ".\nYour total dcuments : {TOTAL}.\nVaild douments : {VALID}"
					+ "\nInvalid documents : {INVALID}.\n*Your Reference number is "+tranId+"* ";
			
			msgs =msgs.replace("{TOTAL}", String.valueOf(total_records));
			msgs =msgs.replace("{VALID}", String.valueOf(valid_records));
			msgs =msgs.replace("{INVALID}", String.valueOf(invalid_records));
			msgs =msgs.replace("{NAME}", customer_name);

			msgs = URLEncoder.encode(msgs, StandardCharsets.UTF_8.toString());

			url = url.replace("{whatsappNumber}", mobile_number);
			url = url.replace("{messageText}", msgs);
			url = url.trim();

			RequestBody body = RequestBody.create(new byte[0], null);

			Request request = new Request.Builder()
					.url(url)
					.addHeader("Authorization", auth)
					.post(body)
					.build();

			Response response = okhttp.newCall(request).execute();

			String responseString = response.body().string();

			log.info("callSendSessionMsg--> waid: " + mobile_number + " response: " + responseString);
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return "Failed";
	}

	@Override
	public List<DocumentResponse> getMasterDocuments() {
		List<DocumentResponse> list =new ArrayList<DocumentResponse>();
		try {
			String documents =preInsImgRepo.getPreinsDocuments();
			String split[]=documents.split(",");
			for (String document :split) {
				DocumentResponse res = new DocumentResponse();
				res.setDocument(document);
				list.add(res);
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return list;
	}

	/*private void getImageExifDate() {
		try {
			String exif_status="";
			Date exifDate =null;
			String data ="data:image/jpeg;base64,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";
			String base64Image = data.split(",")[1];
			byte [] image  =Base64.getDecoder().decode(base64Image);
			String file_path ="C:\\Users\\MAANSAROVAR04\\Desktop\\CommonPath\\test.jpeg";
			Path path =Paths.get(file_path);
			Files.write(path, image);
			
			File file = new File(file_path);
			Metadata metadata1 = ImageMetadataReader.readMetadata(file);	
			FileSystemDirectory fsd = metadata1.getFirstDirectoryOfType(FileSystemDirectory.class);					   
			System.out.println(fsd.getDate(3));
			
			
		    ExifSubIFDDirectory directory = metadata1.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		    log.info("/metadata response :"+cs.reqPrint(metadata1));
		    log.info("/ExifSubIFDDirectory response :"+cs.reqPrint(directory));
		    if(directory!=null) {
			    	exifDate = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);					   
				    LocalDate systemDate =LocalDate.now();			            		 
				    LocalDate date = exifDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				    if(date.isBefore(systemDate) || date.isAfter(systemDate)) {
				    	exif_status="INVALID";
				    	log.info("Image date invalid ==>"+ date);
				    }else {
				    	log.info("Image date valid ==>"+ date);
				    	exif_status="VALID";
				    }
			 }else {
				 
				 exif_status="INVALID";
				 
			 }
 		}catch (Exception e) {
			
		}
	}
	
*/

}
