package com.mei.hui.miner.entity;

import lombok.Data;

@Data
public class SysTransferRecordUserName extends SysTransferRecord {

    /** 用户名 */
    private String userName;
    private String remark;

}
