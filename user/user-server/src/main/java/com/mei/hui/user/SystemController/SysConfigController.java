package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysConfig;
import com.mei.hui.user.service.ISysConfigService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 参数配置 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController
{
    @Autowired
    private ISysConfigService configService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 获取参数配置列表
     */
    @GetMapping("/list")
    public Map<String,Object> list(SysConfig config)
    {
        return configService.selectConfigList(config);
    }


    /**
     * 根据参数编号获取详细信息
     */
    @GetMapping(value = "/{configId}")
    public Result getInfo(@PathVariable Long configId)
    {
        return Result.success(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public Result getConfigKey(@PathVariable String configKey)
    {
        return Result.success(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PostMapping("/add")
    public Result add(@Validated @RequestBody SysConfig config)
    {
        if ("1".equals(configService.checkConfigKeyUnique(config)))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"参数键名已存在");
        }
        config.setCreateBy(sysUserService.getSysUser().getUserName());
        int rows = configService.insertConfig(config);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改参数配置
     */
    @PostMapping("/edit")
    public Result edit(@Validated @RequestBody SysConfig config)
    {
        if ("1".equals(configService.checkConfigKeyUnique(config)))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"参数键名已存在");
        }
        config.setUpdateBy(sysUserService.getSysUser().getUserName());
        int rows = configService.updateConfig(config);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除参数配置
     */
    @PostMapping("/{configIds}")
    public Result remove(@PathVariable Long[] configIds)
    {
        int rows = configService.deleteConfigByIds(configIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 清空缓存
     */
    @PostMapping("/clearCache")
    public Result clearCache()
    {
        configService.clearCache();
        return Result.OK;
    }
}
