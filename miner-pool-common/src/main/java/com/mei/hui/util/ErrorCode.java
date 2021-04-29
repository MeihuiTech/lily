package com.mei.hui.util;

public enum ErrorCode {

	MYB_000000("000000", "成功"),
	MYB_111111("111111", "系统错误,请联系管理员");


	private String code;
	private String msg;

	ErrorCode(String code, String msg) {
		this.code = code;
		this.msg = msg;
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
