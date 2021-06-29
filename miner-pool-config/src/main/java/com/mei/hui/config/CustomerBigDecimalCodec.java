package com.mei.hui.config;

import com.alibaba.fastjson.serializer.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CustomerBigDecimalCodec extends BigDecimalCodec {

    public final static CustomerBigDecimalCodec instance = new CustomerBigDecimalCodec();

    /**
     * 当BigDecimal类型的属性上有@JsonFiled注解，且该注解中的format有值时，使用该方法进行序列化，否则使用fastjson的
     * BigDecimalCodec中的write方法进行序列化
     */

    public void write(JSONSerializer serializer, Object object, BeanContext context) {
        SerializeWriter out = serializer.out;
        if (object == null) {
            out.writeNull(SerializerFeature.WriteNullNumberAsZero);
        } else {
            BigDecimal val = (BigDecimal) object;
            String outText;
            if (out.isEnabled(SerializerFeature.WriteBigDecimalAsPlain)) {
                outText = val.toPlainString();
            } else {
                outText = val.toString();
            }
            out.writeString(outText);
        }

    }
}