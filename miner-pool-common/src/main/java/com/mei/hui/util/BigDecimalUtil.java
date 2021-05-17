package com.mei.hui.util;

import java.math.BigDecimal;

public class BigDecimalUtil {

    /**
     * 保留小数点后4位
     * @param n
     * @return
     */
    public static BigDecimal formatFour(BigDecimal n){
        if(n == null){
            return null;
        }
        return n.setScale(4,BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 保留两位小数
     * @param n
     * @return
     */
    public static BigDecimal formatTwo(BigDecimal n){
        if(n == null){
            return null;
        }
        return n.setScale(2,BigDecimal.ROUND_HALF_UP);
    }
}
