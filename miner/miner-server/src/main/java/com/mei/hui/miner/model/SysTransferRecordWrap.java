package com.mei.hui.miner.model;

import com.mei.hui.miner.entity.SysTransferRecord;

public class SysTransferRecordWrap extends SysTransferRecord
{
    /** 验证码 */
    private String verifyCode;

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
