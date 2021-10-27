package com.mei.hui.browser.common;

public enum BrowserError {

    MYB_000000("000000", "成功"),
    MYB_222222("333333", "错误");

    private String code;
    private String msg;

    BrowserError(String code, String msg) {
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
