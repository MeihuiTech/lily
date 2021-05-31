package com.mei.hui.user.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.common.file.FileUploadUtils;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.mapper.SysUserMapper;
import com.mei.hui.user.model.SysUserBO;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.NotAop;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController{
    @Autowired
    private ISysUserService userService;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Value("${server.port}")
    private String serverPort;
    /**
     * 查询当前登录人的个人详细信息
     */
    @GetMapping
    public Map<String,Object> profile(){
        Long userId = HttpRequestUtil.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        user.setPassword(null);
        user.setAvatar("/user-server"+user.getAvatar());
        Map<String,Object> result = new HashMap<>();
        result.put("code", ErrorCode.MYB_000000.getCode());
        result.put("msg",ErrorCode.MYB_000000.getMsg());
        result.put("roleGroup", userService.selectUserRoleGroup(user.getUserName()));
        result.put("postGroup", null);
        result.put("data",user);
        return result;
    }

    /**
     * 头像上传
     */
    @PostMapping("/avatar")
    @NotAop
    public Map<String,Object> avatar(@RequestParam("avatarfile") MultipartFile file) throws IOException
    {
        Map<String,Object> map = new HashMap<>();
        if (!file.isEmpty())
        {
            SysUser loginUser = userService.getLoginUser();

            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file);
            if (userService.updateUserAvatar(loginUser.getUserId(), avatar))
            {
                map.put("code",ErrorCode.MYB_000000.getCode());
                map.put("msg",ErrorCode.MYB_000000.getMsg());
                map.put("imgUrl","/user-server/"+avatar);
                return map;
            }
        }
        map.put("code",UserError.MYB_333333.getCode());
        map.put("msg",UserError.MYB_333333.getMsg());
        return map;
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Map<String,Object> updateProfile(@RequestBody SysUserBO user){
       return userService.updateProfile(user);
    }

    /**
     * 重置密码
     */
    @PutMapping("/updatePwd")
    public Result updatePwd(String oldPassword, String newPassword)
    {
        return userService.updatePwd(oldPassword,newPassword);
    }




}
