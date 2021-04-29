package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysOperLog;
import com.mei.hui.user.service.ISysOperLogService;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

/**
 * 操作日志记录
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController
{
    @Autowired
    private ISysOperLogService operLogService;

    @GetMapping("/list")
    public Map<String,Object> list(SysOperLog operLog)
    {
        return operLogService.selectOperLogList(operLog);
    }

    @DeleteMapping("/{operIds}")
    public Result remove(@PathVariable Long[] operIds)
    {
        int rows = operLogService.deleteOperLogByIds(operIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    @DeleteMapping("/clean")
    public Result clean()
    {
        operLogService.cleanOperLog();
        return Result.OK;
    }
}
