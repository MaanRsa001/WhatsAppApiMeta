package com.maan.whatsapp.entity.whatsapptemplate;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
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
@Embeddable
public class WhatsappChatrecipiantMessageMasterpk implements Serializable {
	private String messageid;
	private String parentmessageid;
}
