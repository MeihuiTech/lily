package com.mei.hui.user.SystemController;

import com.mei.hui.user.model.ApiTokenVO;
import com.mei.hui.user.model.GetTokenBO;
import com.mei.hui.user.service.ApiUserService;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "nbPool-java-sdk相关接口")
@RestController
@RequestMapping("/k2Pool")
@Slf4j
public class OutSideApi {

    @Autowired
    private ApiUserService apiUserService;

    @ApiOperation("获取token")
    @PostMapping("/getToken")
    public Result<ApiTokenVO> getToken(@RequestBody GetTokenBO getTokenBO){
        return apiUserService.getToken(getTokenBO);
    }


}
