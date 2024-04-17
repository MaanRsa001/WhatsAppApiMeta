package com.maan.whatsapp.request.motor;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TinyURLResponse {
	
	public int code;
    public Data data;
    public List<Object> errors;

}
