package com.maan.whatsapp.response.wati.getmsg;

import java.util.List;

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
public class MessageRes {

	private List<MessageItems> items;

	private String pageNumber;

	private String pageSize;

	private String convCount;

	private String total;

	private String grandTotal;

	private String orderBy;

	private String lastId;

	private String sortBy;

	private String filters;

	private String allowFilters;

	private String search;

}
