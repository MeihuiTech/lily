package com.mei.hui.user.service;

import com.mei.hui.user.model.SmsSendBO;
import com.mei.hui.util.Result;

public interface SmsService {

    Result send(SmsSendBO smsSendBO);
}
