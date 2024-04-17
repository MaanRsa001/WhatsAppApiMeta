package com.maan.whatsapp.response.motor;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WAQuoteRes {

	@JsonProperty("QuestionMsg")
	private String questionMsg;

	@JsonProperty("AnswerMsg")
	private String answerMsg;

	@JsonProperty("ValidationMessage")
	private String validationMessage;

	@JsonProperty("IsJobYN")
	private String isJobYN;

	@JsonProperty("FileYN")
	private String fileYN;

	@JsonProperty("SendedFile")
	private String sendedFile;

	@JsonProperty("IsDocUplYN")
	private String isDocUplYN;

	@JsonProperty("ReceivedFile")
	private String receivedFile;

	@JsonProperty("IsReplyYn")
	private String isReplyYn;

	@JsonProperty("EntryDate")
	private String entryDate;

	@JsonProperty("IsChatYN")
	private String isChatYN;

}
