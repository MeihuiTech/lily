package com.mei.hui.miner.common.enums;

/**
 * 货币状态
 */
public enum  EnumCurrencyStatus {


    //不可用
    disable(0),

    //可用
    available(1);

    private Integer status;
    EnumCurrencyStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
