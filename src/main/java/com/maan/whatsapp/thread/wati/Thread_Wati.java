package com.maan.whatsapp.thread.wati;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.maan.whatsapp.service.wati.WatiService;
import com.maan.whatsapp.service.whatsapp.WhatsAppService;

public class Thread_Wati implements Runnable {

	private Logger log = LogManager.getLogger(getClass());

	private String type;

	private WatiService watiSer;

	private WhatsAppService whatsAppSer;

	public Thread_Wati(String type, WatiService watiSer) {
		this.type = type;
		this.watiSer = watiSer;
	}

	public Thread_Wati(String type, WhatsAppService whatsAppSer) {
		this.type = type;
		this.whatsAppSer = whatsAppSer;
	}

	@Override
	public void run() {
		try {

			type = StringUtils.isBlank(type) ? "" : type;

			log.info("Thread_Wati--> type: " + type);

			if (type.equalsIgnoreCase("UPDATE_MSG_STATUS")) {

				watiSer.updateMsgStatus();

			} else if (type.equalsIgnoreCase("CALL_SESSION_APIS")) {

				watiSer.sendSessionMsg();

			} else if (type.equalsIgnoreCase("SEND_SESSEXP_MSG")) {

				whatsAppSer.sendSessExpMsg();

			}

		} catch (Exception e) {
			log.error(e);
		}
	}

}
