package com.mei.hui.user.SystemController;

import com.alibaba.excel.EasyExcel;
import com.mei.hui.config.jwtConfig.RuoYiConfig;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysLogininfor;
import com.mei.hui.user.model.ExportLoginin;
import com.mei.hui.user.service.ISysLogininforService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.NotAop;
import com.mei.hui.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统访问记录
 * 
 * @author ruoyi
 */
@Slf4j
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

    @GetMapping("/export")
/*    @NotAop*/
    public Map<String,Object> export(SysLogininfor logininfor) throws IOException {
        List<SysLogininfor> list = logininforService.findLogin(logininfor);
        List<ExportLoginin> rows = list.stream().map(v -> {
            ExportLoginin login = new ExportLoginin();
            BeanUtils.copyProperties(v, login);
            return login;
        }).collect(Collectors.toList());
        String filename = UUID.randomUUID().toString() + "_" + DateUtils.getDate() + ".xlsx";
        String path = getAbsoluteFile(filename);
        log.info("下载文件路径:{}",path);
        FileOutputStream out = new FileOutputStream(path);
        EasyExcel.write(out, ExportLoginin.class).sheet("模板").doWrite(rows);
        Map<String,Object> map = new HashMap<>();
        map.put("code", 200);
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("data", path);
       // Constants.RESOURCE_PREFIX + "/"+
        return map;
    }
    /**
     * 获取下载路径
     * @param filename 文件名称
     */
    public String getAbsoluteFile(String filename)
    {
        String downloadPath = RuoYiConfig.getProfile() + File.separator+filename;
        java.io.File desc = new java.io.File(downloadPath);
        if (!desc.getParentFile().exists())
        {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }
}
