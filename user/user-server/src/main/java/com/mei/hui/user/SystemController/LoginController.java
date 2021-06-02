package com.mei.hui.user.SystemController;

import com.google.code.kaptcha.Producer;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Base64;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.model.ChangeCurrencyBO;
import com.mei.hui.user.model.ChangeCurrencyVO;
import com.mei.hui.user.model.LoginBody;
import com.mei.hui.user.service.LoginService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.*;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "用户模块【鲍红建】")
@RestController
@RequestMapping("/system")
@Slf4j
public class LoginController {
    @Autowired
    private ISysUserService sysUserService;
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;
    @Autowired
    private RedisUtil redisCache;
    @Value("${ruoyi.captchaType}")
    private String captchaType;
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private LoginService loginService;
    @Autowired
    private RuoYiConfig ruoYiConfig;
    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public Map<String,Object> login(@RequestBody LoginBody loginBody){

        if(StringUtils.isEmpty(loginBody.getUsername())){
            throw new MyException(ErrorCode.MYB_111111.getMsg(),"用户姓名不能为空");
        }
        if(StringUtils.isEmpty(loginBody.getPassword())){
            throw new MyException(ErrorCode.MYB_111111.getMsg(),"用户密码不能为空");
        }
        if(StringUtils.isEmpty(loginBody.getCode())){
            throw new MyException(ErrorCode.MYB_111111.getMsg(),"请输入验证码");
        }
        return sysUserService.getSysUserByNameAndPass(loginBody);
    }

    @ApiOperation(value = "切换币种")
    @PostMapping("/changeCurrency")
    public Result<ChangeCurrencyVO> changeCurrency(@RequestBody ChangeCurrencyBO changeCurrencyBO){
        return sysUserService.changeCurrency(changeCurrencyBO);
    }
    /**
     * 生成验证码
     */
    @GetMapping("/captchaImage")
    public  Map<String,Object> getCode() throws IOException
    {
        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String capStr = null;
        String code = null;
        BufferedImage image = null;
        // 生成验证码
        if ("math".equals(captchaType)){
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }else if ("char".equals(captchaType)){
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }
        //有效期设置为2分钟
        redisCache.set(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        result.put("uuid", uuid);
        result.put("img", Base64.encode(os.toByteArray()));
        return result;
    }

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @RequestMapping("/getInfo")
    public Map<String,Object> getInfo(){
        return loginService.getInfo();
    }

    /**
     * 获取路由信息
     * @return 路由信息
     */
    @GetMapping("/getRouters")
    public Result getRouters(){
       return loginService.getRouters();
    }

    @RequestMapping("/logout")
    public Result logout(){
        HttpServletRequest httpServletRequest = CommonUtil.getHttpServletRequest();
        String token = httpServletRequest.getHeader(SystemConstants.TOKEN);
        if(StringUtils.isNotEmpty(token)){
            redisCache.delete(token);
        }
        return Result.OK;
    }

    @PostMapping("/user/authority")
    public Result authority(@RequestBody String token){
        //是否主动退出，如果redis无值，则是主动退出
        if(!redisCache.exists(token)){
            throw MyException.fail(ErrorCode.MYB_111003.getCode(),ErrorCode.MYB_111003.getMsg());
        }
        //token 验签，校验是否过期
        Claims claims = JwtUtil.parseToken(token);
        Integer userId = (Integer)claims.get("userId");
        /**
         * 校验是否已经被下线ht
         */
        String offline = String.format(Constants.OfflineUser, userId);
        if (redisUtils.exists(offline)){
            redisUtils.delete(offline);
            throw MyException.fail(ErrorCode.MYB_111003.getCode(),ErrorCode.MYB_111003.getMsg());
        }
        redisUtils.set(token,null,ruoYiConfig.getJwtMinutes(),TimeUnit.MINUTES);
        return Result.OK;
    }




}
