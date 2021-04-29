package com.mei.hui.util;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MyException extends RuntimeException {

	private String code;
	private String msg;

	public static MyException fail(String code,String msg){
		return new MyException(code,msg);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
