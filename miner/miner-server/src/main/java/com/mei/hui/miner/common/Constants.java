package com.mei.hui.miner.common;

import com.mei.hui.util.SystemConstants;
import io.swagger.annotations.ApiModelProperty;

/**
 * 常量类
 */
public interface Constants extends SystemConstants {


    /**
     * 机器类型:Miner
     */
    String MACHINETYPEMINER = "Miner";

    /**
     * 机器类型:post
     */
    String MACHINETYPEPOST = "Post";

    /**
     * 机器类型:ctwo
     */
    String MACHINETYPECTWO = "C2 worker";

    /**
     * 机器类型:seal
     */
    String MACHINETYPESEAL = "Seal worker";

    /**
     * 在线状态：0 离线
     */
    Integer MACHINEONLINEZERO = 0;

    /**
     * 在线状态：1在线
     */
    Integer MACHINEONLINEONE = 1;

    //格式:qi_niu_集群名称_token
    String qi_niu_token = "qi_niu_%s_token";

    /**
     * 存储类型,qiniu-七牛
     */
    String STORETYPEQINIU = "qiniu";

    /**
     * 存储类型,gpfs-GPFS
     */
    String STORETYPEGPFS = "gpfs";

    /**
     * 扇区封装过程：开始封装start
     */
    String ACTIONSTART = "start";

    /**
     * 扇区封装过程：结束封装stop
     */
    String ACTIONSTOP = "stop";

    /**
     * 封装状态：0进行中
     */
    Integer SECTORSTATUSZERO = 0;

    /**
     * 封装状态：1已完成
     */
    Integer SECTORSTATUSONE = 1;

    /**
     * 类型：0Node Fee存储提供者手续费
     */
    String TYPENODEFEE = "Node Fee";

    /**
     * 类型：1Burn Fee销毁手续费
     */
    String TYPEBURNFEE = "Burn Fee";

    /**
     * 类型：2Transfer转账
     */
    String TYPETRANSFER = "Transfer";

}
