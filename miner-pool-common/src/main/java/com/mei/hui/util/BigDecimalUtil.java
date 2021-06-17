package com.mei.hui.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

    /**
     * 保留小数点后4位,直接截取
     * @param n
     * @return
     */
    public static BigDecimal formatFour(BigDecimal n){
        if(n == null){
            return null;
        }
        return n.setScale(4,BigDecimal.ROUND_DOWN);
    }

    /**
     * 保留两位小数，四舍五入
     * @param n
     * @return
     */
    public static BigDecimal formatTwo(BigDecimal n){
        if(n == null){
            return null;
        }
        return n.setScale(2,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 保留小数点后8位,直接截取
     * @param n
     * @return
     */
    public static BigDecimal formatEight(BigDecimal n){
        if(n == null){
            return null;
        }
        return n.setScale(8,BigDecimal.ROUND_DOWN);
    }

}
