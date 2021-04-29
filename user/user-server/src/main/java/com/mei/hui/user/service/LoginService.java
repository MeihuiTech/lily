package com.mei.hui.user.service;

import com.mei.hui.user.model.RouterVo;
import com.mei.hui.util.Result;

import java.util.List;
import java.util.Map;

public interface LoginService {

    Map<String,Object> getInfo();

    Result<List<RouterVo>> getRouters();
}
