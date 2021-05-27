package com.mei.hui.miner.common.enums;

public enum TransferRecordStatusEnum {

    DRAWING(0,"提取中"),
    FINISH(1,"已提取"),
    FAIL(2,"提取失败");

    private int status;

    private String des;

    TransferRecordStatusEnum(int status,String des){
        this.status = status;
        this.des = des;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }
}
