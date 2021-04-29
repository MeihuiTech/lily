package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysLogininfor;
import com.mei.hui.user.service.ISysLogininforService;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统访问记录
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/monitor/logininfor")
public class SysLogininforController{
    @Autowired
    private ISysLogininforService logininforService;

    @GetMapping("/list")
    public Map<String,Object> list(SysLogininfor logininfor)
    {
        return logininforService.selectLogininforList(logininfor);
    }

    @DeleteMapping("/{infoIds}")
    public Result remove(@PathVariable Long[] infoIds)
    {
        int rows = logininforService.deleteLogininforByIds(infoIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    @DeleteMapping("/clean")
    public Result clean()
    {
        logininforService.cleanLogininfor();
        return Result.OK;
    }
}
