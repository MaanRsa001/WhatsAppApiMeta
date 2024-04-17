package com.maan.whatsapp.service.wati;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.whatsapp.entity.master.WAMessageMaster;
import com.maan.whatsapp.entity.whatsapp.QWhatsappContactData;
import com.maan.whatsapp.entity.whatsapp.QWhatsappRequestData;
import com.maan.whatsapp.entity.whatsapp.QWhatsappRequestDataPK;
import com.maan.whatsapp.entity.whatsapp.QWhatsappRequestDetail;
import com.maan.whatsapp.entity.whatsapp.QWhatsappRequestDetailPK;
import com.maan.whatsapp.entity.whatsapp.WhatsappContactData;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestData;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDataPK;
import com.maan.whatsapp.entity.whatsapp.WhatsappRequestDetail;
import com.maan.whatsapp.repository.whatsapp.WhatsappContactDataRepo;
import com.maan.whatsapp.repository.whatsapp.WhatsappRequestDataRepo;
import com.maan.whatsapp.repository.whatsapp.WhatsappRequestDetailRepo;
import com.maan.whatsapp.request.motor.ClaimDocumentReq;
import com.maan.whatsapp.request.motor.DocumentListReq;
import com.maan.whatsapp.request.whatsapp.WAWatiReq;
import com.maan.whatsapp.response.wati.getmsg.GetMessageRes;
import com.maan.whatsapp.response.wati.getmsg.MessageItems;
import com.maan.whatsapp.response.wati.getmsg.MessageRes;
import com.maan.whatsapp.service.common.CommonService;
import com.maan.whatsapp.service.motor.MotorService;
import com.maan.whatsapp.service.motor.MotorServiceImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import com.vdurmont.emoji.EmojiParser;
import com.vdurmont.emoji.EmojiParser.EmojiTransformer;
import com.vdurmont.emoji.EmojiParser.UnicodeCandidate;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Service
public class WatiServiceimpl implements WatiService {

	@Autowired
	private WhatsappRequestDataRepo dataRepo;
	@Autowired
	private WhatsappRequestDetailRepo detailRepo;
	@Autowired
	private WhatsappContactDataRepo contactRepo;

	@Autowired
	private CommonService cs;
	@Autowired
	private WatiApiCall watiApiCall;

	@Autowired
	private JPAQueryFactory jpa;

	@Autowired
	private MotorService motSer;
	
	@Autowired
	private MotorServiceImpl motorImpl;
	@Autowired
	private ObjectMapper objectMapper;

	private Logger log = LogManager.getLogger(getClass());

	@Override
	public String updateMsgStatus() {
		try {

			QWhatsappRequestDetail qDet = QWhatsappRequestDetail.whatsappRequestDetail;

			JPAQuery<WhatsappRequestDetail> jpa_reqData = jpa.selectFrom(qDet);

			List<Long> whatsappnos = jpa_reqData.select(qDet.whatsappid)
					.distinct()
					.where(qDet.issent.equalsIgnoreCase("N")
							.and(qDet.status.equalsIgnoreCase("Y"))
							.and(qDet.wa_response.isNotNull()))
					.fetch();

			log.info("updateMsgStatus--> whatsappnos: " + whatsappnos.size());

			if(whatsappnos.size() > 0) {

				String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
				String msgurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.getMessages");

				String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

				String url = commonurl + msgurl;

				OkHttpClient okhttp = new OkHttpClient.Builder()
						.readTimeout(30, TimeUnit.SECONDS)
						.build();

				Flux.fromIterable(whatsappnos)
					.map(i -> callgetMessage(i, url, auth, okhttp))
					.subscribeOn(Schedulers.boundedElastic())
					.subscribe();
			}

			return "Updated";
		} catch (Exception e) {
			log.error(e);
		}
		return "Not Updated";
	}

	private String callgetMessage(Long whatsappid, String url, String auth, OkHttpClient okhttp) {
		try {

			String waid = String.valueOf(whatsappid);

			url = url.replace("{whatsappNumber}", waid);
			url = url.replace("{pageSize}", "");
			url = url.replace("{pageNumber}", "");
			url = url.trim();

			Request request = new Request.Builder()
					.url(url)
					.addHeader("Authorization", auth)
					.get()
					.build();

			Response response = okhttp.newCall(request)
					.execute();

			String responseString = response.body().string();

			log.info("callgetMessage--> waid: " + waid + " response: " + responseString);

			GetMessageRes apiRes = objectMapper.readValue(responseString, GetMessageRes.class);

			String result = StringUtils.isBlank(apiRes.getResult()) ? "" : apiRes.getResult();

			if (result.equalsIgnoreCase("success")) {

				MessageRes messages = apiRes.getMessages();

				List<MessageItems> items = messages.getItems();

				if (items.size() > 0) {

					QWhatsappRequestDetail qDet = QWhatsappRequestDetail.whatsappRequestDetail;

					JPAQuery<WhatsappRequestDetail> jpa_reqData = jpa.selectFrom(qDet);

					List<WhatsappRequestDetail> details = jpa_reqData.select(qDet)
							.where(qDet.issent.equalsIgnoreCase("N")
									.and(qDet.status.equalsIgnoreCase("Y"))
									.and(qDet.whatsappid.eq(whatsappid))
									.and(qDet.wa_response.isNotNull()))
							.fetch();

					for (WhatsappRequestDetail det : details) {

						String message = StringUtils.isBlank(det.getMessage()) ? ""
								: det.getMessage().replaceAll("\\s", "").trim();

						Predicate<MessageItems> pred = i -> true;

						Optional<MessageItems> op_item = items.stream()
								.filter(pred
										.and(i -> (StringUtils.isBlank(i.getOwner()) ? false
												: i.getOwner().equalsIgnoreCase("true")))
										.and(i -> !NumberUtils.isCreatable(i.getType()))
										.and(i -> i.getEventType().equalsIgnoreCase("message"))
										.and(i -> (StringUtils.isBlank(i.getStatusString()) ? false
												: !i.getStatusString().equalsIgnoreCase("FAILED")))
										.and(i -> (StringUtils.isBlank(i.getText()) ? false
												: i.getText().replaceAll("\\s", "").trim().equalsIgnoreCase(message))))
								.findFirst();

						if (op_item.isPresent()) {
							MessageItems item = op_item.get();

							det.setIssent("Y");
							det.setWa_messageid(item.getId());
							det.setWa_filepath(item.getData());
							det.setWa_response(item.getStatusString());

							detailRepo.save(det);
						}
					}
				}
			}

		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	private long execute(QWhatsappRequestData qdata,QWhatsappRequestDataPK qdataPk, Long mobileno, Long whatsappcode) {
		try {

			JPAUpdateClause update = jpa.update(qdata);

			long execute = update.set(qdata.issessionactive, "Y")
					.set(qdata.request_time, new Date())
					.set(qdata.response_time, new Date())
					.where(qdataPk.mobileno.eq(mobileno)
							.and(qdataPk.whatsappcode.eq(whatsappcode))
							.and(qdata.status.equalsIgnoreCase("Y")))
					.execute();

			log.info("execute--> execute: " + execute);

			return execute;
		} catch (Exception e) {
			log.error(e);
		}
		return 0;
	}

	@Override
	public String sendSessionMsg() {
		try {

			QWhatsappRequestData qdata = QWhatsappRequestData.whatsappRequestData;
			QWhatsappRequestDataPK qdataPk = qdata.reqDataPk;

			JPAQuery<WhatsappRequestData> jpa_reqData = jpa.selectFrom(qdata);

			List<WhatsappRequestData> datas = jpa_reqData.select(qdata)
					.where((qdata.isprocesscompleted.notEqualsIgnoreCase("Y")
							.or(qdata.isprocesscompleted.isNull()))
							.and(qdata.issessionactive.equalsIgnoreCase("Y"))
							.and(qdata.status.equalsIgnoreCase("Y")))
					.orderBy(qdata.entry_date.asc(), qdataPk.currentstage.asc())
					.fetch();

			log.info("sendSessionMsg--> messageNotSendedCount: " + datas.size());

			callsendSessionMsg(datas);

			return "";
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	private String callsendSessionMsg(List<WhatsappRequestData> datas) {
		try {

			if (datas.size() > 0) {

				String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
				String msgurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.sendSessionMessage");
				String fileurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.sendSessionFile");

				String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

				OkHttpClient okhttp = new OkHttpClient.Builder()
						.readTimeout(30, TimeUnit.SECONDS)
						.build();

				RequestBody body = RequestBody.create(new byte[0], null);

				for (WhatsappRequestData data : datas) {

					Date reqtime = new Date();

					WhatsappRequestDataPK dataPk = data.getReqDataPk();

					Long quoteno = dataPk.getQuoteno();
					Long currentStage = dataPk.getCurrentstage();
					Long productid = dataPk.getProductid();
					Long mobileno = dataPk.getMobileno();
					Long whatsappcode = dataPk.getWhatsappcode();

					String mobno = String.valueOf(whatsappcode) + String.valueOf(mobileno);

					QWhatsappRequestDetail qDet = QWhatsappRequestDetail.whatsappRequestDetail;
					QWhatsappRequestDetailPK qDetPk = qDet.reqDetPk;

					BooleanExpression bool_det = qDetPk.currentstage.eq(currentStage)
								.and(qDetPk.quoteno.eq(quoteno))
								.and(qDetPk.productid.eq(productid))
								.and(qDet.status.equalsIgnoreCase("Y"))
								.and(qDet.issent.notEqualsIgnoreCase("Y")
										.or(qDet.issent.isNull()));

					List<WhatsappRequestDetail> detail_list = (List<WhatsappRequestDetail>) detailRepo.findAll(bool_det,
							qDet.stage_order.asc());

					log.info("callsendSessionMsg--> list: " + detail_list.size());

					List<CompletableFuture<String>> callList = new ArrayList<>();

					for (WhatsappRequestDetail detail : detail_list) {

						CompletableFuture<String> call;

						call = watiApiCall.sendMsg(okhttp, body, commonurl, msgurl, fileurl, auth, detail, mobno,
								reqtime);

						callList.add(call);
					}

					CompletableFuture.allOf(callList.toArray(new CompletableFuture[callList.size()])).join();

					if(detail_list.size() > 0) {
						BooleanExpression bool_detIssent = qDetPk.currentstage.eq(currentStage)
								.and(qDetPk.quoteno.eq(quoteno))
								.and(qDetPk.productid.eq(productid))
								.and(qDet.status.equalsIgnoreCase("Y"))
								.and(qDet.issent.notEqualsIgnoreCase("Y")
										.or(qDet.issent.isNull()));

						long count = detailRepo.count(bool_detIssent);

						if (count > 0) {
							data.setIsprocesscompleted("N");
							data.setRequest_time(reqtime);
							data.setResponse_time(new Date());
						} else {
							data.setIsprocesscompleted("Y");
							data.setRequest_time(reqtime);
							data.setResponse_time(new Date());
						}

						dataRepo.save(data);
					}
				}				
			}

			return "";
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	@Override
	public String sendSessionMsg(Long waid) {
		try {

			long count = checkSessionStatus(waid);

			log.info("sendSessionMsg--> waid: " + waid + " count: " + count);

			if (count > 0) {
				callSendSessionMsg(waid, "");
			}

		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	@Override
	public long checkSessionStatus(Long waid) {
		try {

			QWhatsappContactData qContactData = QWhatsappContactData.whatsappContactData;

			JPAQuery<WhatsappContactData> jpa_contactData = jpa.selectFrom(qContactData);

			DateTimeExpression<Date> currentTimestamp = Expressions.currentTimestamp();

			long count = jpa_contactData.where(
					qContactData.status.equalsIgnoreCase("Y")
					.and(qContactData.whatsappid.eq(waid))
					.and(currentTimestamp.between(qContactData.session_start_time, qContactData.session_end_time)))
					.fetchCount();

			return count;
		} catch (Exception e) {
			log.error(e);
		}
		return 0;
	}

	@Override
	public String callSendSessionMsg(Long waid, String msgid) {
		try {

			QWhatsappRequestDetail qDet = QWhatsappRequestDetail.whatsappRequestDetail;

			BooleanExpression bool_det = qDet.whatsappid.eq(waid)
					.and(qDet.status.equalsIgnoreCase("Y"))
					.and(qDet.isskipped.equalsIgnoreCase("N"))
					.and((qDet.isreplyyn.equalsIgnoreCase("Y").and(
							qDet.isprocesscompleted.notEqualsIgnoreCase("Y").or(qDet.issent.notEqualsIgnoreCase("Y"))))
									.or(qDet.isreplyyn.equalsIgnoreCase("N")
											.and(qDet.issent.notEqualsIgnoreCase("Y"))))
					.and(StringUtils.isBlank(msgid) ? qDet.remarks.isNotNull() : qDet.remarks.equalsIgnoreCase(msgid));

			List<WhatsappRequestDetail> detail_list = (List<WhatsappRequestDetail>) detailRepo.findAll(bool_det,
					qDet.entry_date.asc(), qDet.stage_order.asc());

			log.info("callsendSessionMsg--> list: " + detail_list.size());

			if(detail_list.size() > 0) {
				
				String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
				String msgurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.sendSessionMessage");
				String fileurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.sendSessionFile");

				String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

				OkHttpClient okhttp = new OkHttpClient.Builder()
						.readTimeout(30, TimeUnit.SECONDS)
						.build();

				RequestBody body = RequestBody.create(new byte[0], null);

				List<CompletableFuture<String>> callList = new ArrayList<>();

				String isReplyyn = "N";

				for (WhatsappRequestDetail detail : detail_list) {
					if(StringUtils.isNotEmpty(detail.getIsread())) {
						detail.setIsread("");
						detailRepo.save(detail);
					}
					if (isReplyyn.equalsIgnoreCase("N")) {
						CompletableFuture<String> call;

						call = watiApiCall.sendMsg(okhttp, body, commonurl, msgurl, fileurl, auth, detail,
								String.valueOf(waid), new Date());

						callList.add(call);

						isReplyyn = StringUtils.isBlank(detail.getIsreplyyn()) ? "N" : detail.getIsreplyyn();
	  				}

					if (isReplyyn.equalsIgnoreCase("Y"))
						break;
				}

				CompletableFuture.allOf(callList.toArray(new CompletableFuture[callList.size()])).join();
			}

			return "";
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	@Override
	public WAWatiReq sendSessMsg(WAMessageMaster wamsgM, Long waid) {
		try {

			String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
			String msgurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.sendSessionMessage");
			String buttonMsgUrl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.button");

			String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

			OkHttpClient okhttp = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build();

			RequestBody body = RequestBody.create(new byte[0], null);
			
			String msgEn = StringUtils.isBlank(wamsgM.getMessagedescen()) ? "" : wamsgM.getMessagedescen();
			String msgAr = StringUtils.isBlank(wamsgM.getMessagedescar()) ? "" : wamsgM.getMessagedescar();
			String isButtonMsg=StringUtils.isBlank(wamsgM.getIsButtonMsg())?"N":wamsgM.getIsButtonMsg();
			
			
			String language =contactRepo.getLanguage(waid.toString());
			String msg ="";
			if("English".equalsIgnoreCase(language))
				msg =msgEn;
			else if("Swahili".equalsIgnoreCase(language))
				msg =msgAr;
			
			String button1 ="",button2="",button3="";
			
			if("Y".equalsIgnoreCase(isButtonMsg)) {
				if("English".equalsIgnoreCase(language)) {
					button1=wamsgM.getMsgButton1();
					button2=StringUtils.isBlank(wamsgM.getMsgButton2())?"":wamsgM.getMsgButton2();
					button3=StringUtils.isBlank(wamsgM.getMsgButton3())?"":wamsgM.getMsgButton3();
				}else if("Swahili".equalsIgnoreCase(language)) {
					button1=wamsgM.getMsgButtonSW1();
					button2=StringUtils.isBlank(wamsgM.getMsgButtonSw2())?"":wamsgM.getMsgButtonSw2();
					button3=StringUtils.isBlank(wamsgM.getMsgButtonSw3())?"":wamsgM.getMsgButtonSw3();
				}						
			}
			WAWatiReq waReq = WAWatiReq.builder()
					.filepath("")
					.msg(msg)
					.waid(String.valueOf(waid))
					.isButtonMsg(isButtonMsg)
					.imageUrl(StringUtils.isBlank(wamsgM.getImageUrl())?"":wamsgM.getImageUrl())
					.imageName(StringUtils.isBlank(wamsgM.getImageName())?"":wamsgM.getImageName())
					.button1(button1) 
					.button2(button2) 
					.button3(button3) 
					.msgType(StringUtils.isBlank(wamsgM.getMsgType())?"":wamsgM.getMsgType())
					.isTemplateMsg("N")
					.build();

			String url="";
			if("Y".equalsIgnoreCase(isButtonMsg))
				url = commonurl + buttonMsgUrl;
			else
				url = commonurl + msgurl;
			
			WAWatiReq response = watiApiCall.callSendSessionMsg(okhttp, body, url, auth, waReq);

			return response;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

	@Override
	public String storeWAFile(String wafile) {
		try {
			log.info("storeWAFile--> wafile: " + wafile);

			String commonurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api");
			String fileurl = cs.getwebserviceurlProperty().getProperty("whatsapp.api.getMedia");

			String auth = cs.getwebserviceurlProperty().getProperty("whatsapp.auth");

			String url = commonurl + fileurl;

			url = url.replace("{data}", wafile);
			url = url.trim();

			OkHttpClient okhttp = new OkHttpClient.Builder()
					.readTimeout(30, TimeUnit.SECONDS)
					.build();

			Request request = new Request.Builder()
					.url(url)
					.addHeader("Authorization", auth)
					.get()
					.build();

			Response response = okhttp.newCall(request)
					.execute();

			InputStream is = response.body().byteStream();
			
			String path = cs.getwebserviceurlProperty().getProperty("wa.preins.image.path");

			String date = cs.formatdatewithtime3(new Date());
			String fileName = FilenameUtils.getBaseName(wafile);
			String extension = FilenameUtils.getExtension(wafile);
			
			String name = fileName + "_" + date + "." + extension;
			path = path + name;

			log.info("storeWAFile--> path: " + path);

			File toFile = new File(path);

			FileUtils.copyInputStreamToFile(is, toFile);

			log.info("storeWAFile--> wafile: " + wafile + " path: " + path);

			return path;
		} catch (Exception e) {
			log.error(e);
		}
		return "";
	}

	
}
