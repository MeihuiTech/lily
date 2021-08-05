package com.mei.hui.miner.test;

import com.mei.hui.config.MailUtil;
import com.mei.hui.config.model.MailDO;
import com.mei.hui.miner.MinerApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MinerApplication.class)
@Slf4j
public class MailTest {



    @Test
    public void sendTitleTest(){
        MailDO mail = new MailDO();
        mail.setContent("发送了一段文本");
        mail.setEmail("baohongjian@meihuitech.com");
        mail.setTitle("你有一条新消息");
        MailUtil.sendTextMail(mail);
        log.info("发送完成");
    }

    @Test
    public void sendHtmlTest(){
        Map<String,Object> map = new HashMap<>();
        map.put("附件名","https://www.cnblogs.com/codhome/p/13621107.html");

        MailDO mail = new MailDO();
        mail.setContent("发了一个附件");
        mail.setEmail("baohongjian@meihuitech.com");
        mail.setTitle("你有一条新消息");
        mail.setAttachment(map);
        MailUtil.sendHtmlMail(mail,true);
        log.info("发送完成");
    }

    @Test
    public void sendTemplateTest(){
        Map<String,Object> map = new HashMap<>();
        map.put("username","我变大了");

        MailDO mail = new MailDO();
        mail.setContent("发送了一个模板邮件");
        mail.setEmail("baohongjian@meihuitech.com");
        mail.setTitle("你有一条新消息");
        mail.setAttachment(map);
        MailUtil.sendTemplateMail(mail);
        log.info("发送完成");
    }

}
