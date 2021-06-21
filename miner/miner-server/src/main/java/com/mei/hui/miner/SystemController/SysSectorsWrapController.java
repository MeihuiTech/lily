package com.mei.hui.miner.SystemController;

import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.entity.SysSectorInfo;
import com.mei.hui.miner.entity.SysSectorsWrap;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.miner.service.ISysSectorInfoService;
import com.mei.hui.miner.service.ISysSectorsWrapService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.MyException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扇区信息聚合Controller
 * 
 * @author ruoyi
 * @date 2021-03-04
 */
@Api(value="扇区聚合信息", tags = "扇区聚合信息")
@RestController
@RequestMapping("/system/sectors")
public class SysSectorsWrapController
{
    @Autowired
    private ISysSectorsWrapService sysSectorsWrapService;

    @Autowired
    private ISysMinerInfoService sysMinerInfoService;

    @Autowired
    private ISysSectorInfoService sysSectorInfoService;

    /**
     * 查询扇区信息聚合列表
     */
    @ApiOperation(value = "扇区聚合列表",notes = "入参：\n" +
            "pageNum当前页码\n" +
            "pageSize每页大小\n" +
            "sectorNo扇区编号\n" +
            "minerId旷工id\n" +
            "beginTime开始时间，格式样例：2021-06-12\n" +
            "endTime开始时间，格式样例：2021-06-12\n" +
            "cloumName排序字段名称\n" +
            "asc:true 升序，false 降序")
    @GetMapping("/list")
    public Map<String,Object> list(SysSectorsWrap sysSectorsWrap)
    {
        if(StringUtils.isNotEmpty(sysSectorsWrap.getBeginTime())){
            sysSectorsWrap.setBeginTime(sysSectorsWrap.getBeginTime() + " 00:00:00");
        }
        if(StringUtils.isNotEmpty(sysSectorsWrap.getEndTime())){
            sysSectorsWrap.setEndTime(sysSectorsWrap.getEndTime() + " 23:59:59");
        }
        return sysSectorsWrapService.list(sysSectorsWrap);
    }


    @ApiOperation(value = "扇区详情")
    @GetMapping(value = "/{id}")
    public Map<String,Object> statusList(@PathVariable("id") Long id) {
        SysSectorsWrap wrap = sysSectorsWrapService.selectSysSectorsWrapById(id);
        if (wrap == null) {
            throw MyException.fail(MinerError.MYB_222222.getCode(),"资源不存在");
        }
        SysMinerInfo miner = sysMinerInfoService.selectSysMinerInfoByMinerId(wrap.getMinerId());
        Long userId = HttpRequestUtil.getUserId();

        if (userId != null && userId != 1L) {
            //扇区所属矿工信息丢失，无从判断扇区聚合信息所有权；或扇区聚合信息不属于当前登录者
            if (miner == null || !miner.getUserId().equals(userId)) {
                throw MyException.fail(MinerError.MYB_222222.getCode(),"没有权限");
            }
        }

        SysSectorInfo sectorInfo = new SysSectorInfo();
        sectorInfo.setSectorNo(wrap.getSectorNo());
        sectorInfo.setMinerId(wrap.getMinerId());
        List<SysSectorInfo> list = sysSectorInfoService.selectSysSectorInfoList(sectorInfo);
        for (SysSectorInfo dbSysSectorInfo:list) {
            // 显示GB，原来单位是B，结果肯定是整数，不会出来小数点
            dbSysSectorInfo.setSectorSize(dbSysSectorInfo.getSectorSize()/1024/1024/1024);
            if ("none".equals(dbSysSectorInfo.getHostname())) {
                dbSysSectorInfo.setHostname("");
            }
        }
        /**
         * 组装返回信息
         */
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg", ErrorCode.MYB_000000.getMsg());
        map.put("rows", list);
        map.put("total", list.size());
        return map;
    }

    /*
    *
    * @description 手动测试插入扇区信息聚合表
    * @author shangbin
    * @date 2021/5/12 15:54
    * @param []
    * @return int
    * @version v1.0.0
    */
//    @GetMapping(value = "/testInsert")
    public int testInsert() {
        SysSectorsWrap sysSectorsWrap = new SysSectorsWrap();
        sysSectorsWrap.setMinerId("f0693008");
        sysSectorsWrap.setHostname("none");
        sysSectorsWrap.setSectorNo(Long.valueOf("43626"));
        sysSectorsWrap.setSectorSize(Long.valueOf("68719476736"));
        sysSectorsWrap.setSectorStatus(8);
        sysSectorsWrap.setSectorDuration(Long.valueOf(0));
        return sysSectorsWrapService.testInsert(sysSectorsWrap);

    }
}
