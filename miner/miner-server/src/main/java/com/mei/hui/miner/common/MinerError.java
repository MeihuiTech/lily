package com.mei.hui.miner.common;

public enum  MinerError {

    MYB_000000("000000", "成功"),
    MYB_222222("222222", "错误");

    private String code;
    private String msg;

    MinerError(String code, String msg) {
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
