package com.maan.whatsapp.config.scheduler;

import java.net.InetAddress;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.maan.whatsapp.repository.master.WhatsappTemplateMasterRepo;
import com.maan.whatsapp.service.wati.WatiService;
import com.maan.whatsapp.service.whatsapp.WhatsAppService;
import com.maan.whatsapp.thread.wati.Thread_Wati;

@Service
public class ScheduleTasks {

	@Autowired
	private WhatsappTemplateMasterRepo wtmRepo;

	@Autowired
	private WatiService watiSer;
	@Autowired
	private WhatsAppService whatsAppSer;

	private static Logger log = LogManager.getLogger(ScheduleTasks.class);

	static String ipaddress;
	static {
		try {
			ipaddress = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			log.error(e);
		}
	}

	//@Scheduled(cron = "${whatsapp.update.msg.status}")
	public void start_UpdateMsgStatus() {
		try {

			log.info("start_UpdateMsgStatus--> ipaddress: " + ipaddress);

			String status = wtmRepo.getStatus("210", "4", ipaddress);
			status = StringUtils.isBlank(status) ? "" : status;

			log.info("start_UpdateMsgStatus--> status: " + status);

			if (status.equalsIgnoreCase("Y")) {

				boolean thread_status = isProcessExist("UPDATE_MSG_STATUS");

				log.info("start_UpdateMsgStatus--> thread_status: " + thread_status);

				if (!thread_status) {

					Thread_Wati thread_Job = new Thread_Wati("UPDATE_MSG_STATUS", watiSer);

					Thread thread = new Thread(thread_Job);
					thread.setName("UPDATE_MSG_STATUS");
					thread.setDaemon(false);
					thread.start();
				}
			}

		} catch (Exception e) {
			log.error(e);
		}
	}

	//@Scheduled(cron = "${whatsapp.send.sessExp.msg}")
	public void start_sendSessExpMsg() {
		try {

			log.info("start_sendSessExpMsg--> ipaddress: " + ipaddress);

			String status = wtmRepo.getStatus("210", "5", ipaddress);
			status = StringUtils.isBlank(status) ? "" : status;

			log.info("start_sendSessExpMsg--> status: " + status);

			if (status.equalsIgnoreCase("Y")) {

				boolean thread_status = isProcessExist("SEND_SESSEXP_MSG");

				log.info("start_sendSessExpMsg--> thread_status: " + thread_status);

				if (!thread_status) {

					Thread_Wati thread_Job = new Thread_Wati("SEND_SESSEXP_MSG", whatsAppSer);

					Thread thread = new Thread(thread_Job);
					thread.setName("SEND_SESSEXP_MSG");
					thread.setDaemon(false);
					thread.start();
				}
			}

		} catch (Exception e) {
			log.error(e);
		}
	}

	public boolean isProcessExist(String processName) {
		try {
			Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
			Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
			if (threadArray.length > 0) {
				for (Thread th : threadArray) {
					if (th.getName().contains(processName) && th.isAlive()) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		return false;
	}

}
