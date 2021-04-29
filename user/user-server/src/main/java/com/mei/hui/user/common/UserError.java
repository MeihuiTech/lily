package com.mei.hui.user.common;

public enum UserError {

    MYB_333333("333333", "错误");

    private String code;
    private String msg;

    UserError(String code, String msg) {
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
