package com.mei.hui.config.smsConfig;

import com.mei.hui.config.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Slf4j
@Component
public class SmsUtil {

    private static SmsConfig smsConfig;

    @Autowired
    public static void setSmsConfig(SmsConfig smsConfig) {
        SmsUtil.smsConfig = smsConfig;
    }

    public static  boolean send(String phone, String sms){
        long timestamp = System.currentTimeMillis();
        log.debug("发送时间"+timestamp);
        String url = smsConfig.getUrl();
        String username = smsConfig.getUsername();
        String password = smsConfig.getPassword();
        String token = smsConfig.getToken();
        String templateId = smsConfig.getTemplateId();
        String beforeSign = "action=sendtemplate&username="+username+"&password="+getMD5String(password)+"&token="+token+"&timestamp="+timestamp;
        String postData = "action=sendtemplate&username="+username+"&password="+getMD5String(password)+"&token="+token+"&templateid="+templateId+"&param="+phone+"|"+sms+"&rece=json&timestamp="+timestamp+"&sign="+getMD5String(beforeSign);
        String result = HttpUtil.doGet(url,postData);
        log.debug("发送短信结果：" + result);
        return !result.equals("");
    }

    private static String getMD5String(String rawString){    //用来计算MD5的函数
        String[] hexArray = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(rawString.getBytes());
            byte[] rawBit = md.digest();
            String outputMD5 = " ";
            for(int i = 0; i<16; i++){
                outputMD5 = outputMD5+hexArray[rawBit[i]>>>4& 0x0f];
                outputMD5 = outputMD5+hexArray[rawBit[i]& 0x0f];
            }
            return outputMD5.trim();
        }catch(Exception e){
            log.error("计算MD5值发生错误");
            e.printStackTrace();
        }
        return null;
    }
}
