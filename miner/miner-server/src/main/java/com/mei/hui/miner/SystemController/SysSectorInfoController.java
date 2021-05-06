package com.mei.hui.miner.SystemController;

import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.service.ISysSectorInfoService;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
 * 扇区信息Controller
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@RestController
@RequestMapping("/system/sector")
public class SysSectorInfoController
{
    @Autowired
    private ISysSectorInfoService sysSectorInfoService;

    /**
     * 查询扇区信息列表
     */
    @GetMapping("/list")
    public Map<String,Object> list(SysSectorInfo sysSectorInfo)
    {
        return sysSectorInfoService.list(sysSectorInfo);
    }

    /**
     * 获取扇区信息详细信息
     */
    @GetMapping(value = "/{id}")
    public Result getInfo(@PathVariable("id") Long id)
    {
        SysSectorInfo sectorInfo = sysSectorInfoService.selectSysSectorInfoById(id);
        return Result.success(sectorInfo);
    }
}
