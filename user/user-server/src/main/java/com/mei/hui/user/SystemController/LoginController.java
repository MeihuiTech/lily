package com.mei.hui.user.SystemController;

import com.google.code.kaptcha.Producer;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.JwtUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Base64;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.feign.vo.SignBO;
import com.mei.hui.user.model.LoginBody;
import com.mei.hui.user.service.LoginService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.*;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
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
    private LoginService loginService;
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
        Long userId = HttpRequestUtil.getUserId();
        redisCache.delete(Constants.USERID+userId);
        return Result.OK;
    }

    @PostMapping("/sign ")
    public Result sign(@RequestBody SignBO signBO){
        //验签
        Claims claims = JwtUtil.parseToken(signBO.getToken());
        Integer userId = (Integer) claims.get(SystemConstants.USERID);
        if(!redisCache.exists("user:"+userId)){
            throw MyException.fail(ErrorCode.MYB_111111.getCode(),"token 失效");
        }
        return Result.OK;
    }




}
