package com.mei.hui.miner.SystemController;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mei.hui.miner.common.MinerError;
import com.mei.hui.miner.entity.SysMinerInfo;
import com.mei.hui.miner.feign.vo.BillTotalVO;
import com.mei.hui.miner.feign.vo.FilBillMethodBO;
import com.mei.hui.miner.feign.vo.FilBillSubAccountVO;
import com.mei.hui.miner.feign.vo.FilBillVO;
import com.mei.hui.miner.service.FilBillService;
import com.mei.hui.miner.service.ISysMinerInfoService;
import com.mei.hui.util.DateUtils;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "fil币账单相关")
@RestController
@RequestMapping("/bill")
public class FilBillController {

    @Autowired
    private FilBillService filBillService;
    @Autowired
    private ISysMinerInfoService sysMinerInfoService;


    @ApiOperation("账单方法下拉列表")
    @PostMapping("/methodList")
    public Result<List<String>> selectFilBillMethodList(@RequestBody FilBillMethodBO filBillMethodBO){
        if (filBillMethodBO == null || StringUtils.isEmpty(filBillMethodBO.getMinerId()) || StringUtils.isEmpty(filBillMethodBO.getSubAccount()) || StringUtils.isEmpty(filBillMethodBO.getMonthDate())){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"入参为空");
        }
        List<String> billMethodList = filBillService.selectFilBillMethodList(filBillMethodBO);
        return Result.success(billMethodList);
    }

    @ApiOperation("矿工子账户下拉列表")
    @PostMapping("/subAccountList")
    public Result<List<FilBillSubAccountVO>> selectFilBillSubAccountList(@RequestBody FilBillMethodBO filBillMethodBO){
        if (filBillMethodBO == null || StringUtils.isEmpty(filBillMethodBO.getMinerId())){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"矿工id为空");
        }
        List<FilBillSubAccountVO> filBillSubAccountVOList = filBillService.selectFilBillSubAccountList(filBillMethodBO);
        return Result.success(filBillSubAccountVOList);
    }

    @ApiOperation("分页查询账单消息列表")
    @PostMapping("/page")
    public Result<IPage<FilBillVO>> selectFilBillPage(@RequestBody FilBillMethodBO filBillMethodBO){
        if (StringUtils.isEmpty(filBillMethodBO.getMinerId()) && StringUtils.isEmpty(filBillMethodBO.getSubAccount()) && StringUtils.isEmpty(filBillMethodBO.getMonthDate())){
            filBillMethodBO = filBillMethodBOIsNull(filBillMethodBO);
        }
        IPage<FilBillVO> filBillVOIPage = filBillService.selectFilBillPage(filBillMethodBO);
        return Result.success(filBillVOIPage);
    }

    @ApiModelProperty("查询账单汇总信息")
    @PostMapping("/total")
    public Result<BillTotalVO> selectFilBillTotal(@RequestBody FilBillMethodBO filBillMethodBO){
        if (StringUtils.isEmpty(filBillMethodBO.getMinerId()) && StringUtils.isEmpty(filBillMethodBO.getSubAccount()) && StringUtils.isEmpty(filBillMethodBO.getMonthDate())){
            filBillMethodBO = filBillMethodBOIsNull(filBillMethodBO);
        }
        BillTotalVO billTotalVO = filBillService.selectFilBillTotal(filBillMethodBO);
        return Result.success(billTotalVO);
    }

    /**
     * filBillMethodBO为空时设置默认值
     * @param filBillMethodBO
     */
    public FilBillMethodBO filBillMethodBOIsNull(FilBillMethodBO filBillMethodBO){
        List<SysMinerInfo> sysMinerInfoList = sysMinerInfoService.selectSysMinerInfoList(new SysMinerInfo());
        log.info("查询矿工信息列表：【{}】",JSON.toJSON(sysMinerInfoList));
        if (sysMinerInfoList == null || sysMinerInfoList.size() < 1){
            throw  MyException.fail(MinerError.MYB_222222.getCode(),"该用户没有矿工");
        }
        filBillMethodBO.setMinerId(sysMinerInfoList.get(0).getMinerId());
        filBillMethodBO.setSubAccount(sysMinerInfoList.get(0).getMinerId());
        filBillMethodBO.setMonthDate(DateUtils.getDate().substring(0,7));
        return filBillMethodBO;
    }

}

