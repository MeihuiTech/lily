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
        double f1 = n.setScale(4,BigDecimal.ROUND_HALF_UP).doubleValue();
        return new BigDecimal(f1);
    }
}
