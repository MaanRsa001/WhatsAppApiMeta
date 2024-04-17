package com.maan.whatsapp.request.motor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Data {
	
	public String url;
    public String domain;
    public String alias;
    public List<Object> tags;
    public String tiny_url;

}
