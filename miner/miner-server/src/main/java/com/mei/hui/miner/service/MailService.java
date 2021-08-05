package com.mei.hui.miner.service;

import com.mei.hui.miner.model.MailDO;

public interface MailService {

    void sendTextMail(MailDO mailDO);

    void sendHtmlMail(MailDO mailDO,boolean isShowHtml);

    void sendTemplateMail(MailDO mailDO);
}
