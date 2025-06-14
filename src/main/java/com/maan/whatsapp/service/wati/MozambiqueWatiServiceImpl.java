package com.maan.whatsapp.service.wati;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.whatsapp.entity.master.WAMessageMaster;
import com.maan.whatsapp.repository.whatsapp.WhatsappContactDataRepo;
import com.maan.whatsapp.repository.whatsapp.WhatsappRequestDataRepo;
import com.maan.whatsapp.repository.whatsapp.WhatsappRequestDetailRepo;
import com.maan.whatsapp.request.whatsapp.WAWatiReq;
import com.maan.whatsapp.service.common.CommonService;
import com.maan.whatsapp.service.motor.MotorService;
import com.maan.whatsapp.service.motor.MotorServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Service
public class MozambiqueWatiServiceImpl implements MozambiqueWatiService{

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendSessionMsg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String sendSessionMsg(Long waid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long checkSessionStatus(Long waid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String callSendSessionMsg(Long waid, String msgid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WAWatiReq sendSessMsg(WAMessageMaster wamsgM, Long waid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String storeWAFile(String wafile) {
		// TODO Auto-generated method stub
		return null;
	}

}
