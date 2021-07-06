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

}
