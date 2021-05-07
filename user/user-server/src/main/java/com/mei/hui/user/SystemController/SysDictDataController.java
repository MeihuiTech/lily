package com.mei.hui.user.SystemController;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysDictData;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.service.ISysDictDataService;
import com.mei.hui.user.service.ISysDictTypeService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.ErrorCode;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据字典信息
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController{
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping("/list")
    public Map<String,Object> list(SysDictData dictData)
    {
        PageHelper.startPage(Integer.valueOf(dictData.getPageNum()+""),Integer.valueOf(dictData.getPageSize()+""));
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        Map<String,Object> map = new HashMap<>();
        map.put("code", ErrorCode.MYB_000000.getCode());
        map.put("msg",ErrorCode.MYB_000000.getMsg());
        map.put("rows",list);
        map.put("total",new PageInfo(list).getTotal());
        return map;
    }

    /**
     * 查询字典数据详细
     */
    @GetMapping(value = "/{dictCode}")
    public Result getInfo(@PathVariable Long dictCode)
    {
        return Result.success(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public Result dictType(@PathVariable String dictType)
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (data == null || data.size() == 0)
        {
            data = new ArrayList<SysDictData>();
        }
        return Result.success(data);
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysDictData dict){
        SysUser user = sysUserService.getLoginUser();
        dict.setCreateBy(user.getUserName());
        int rows = dictDataService.insertDictData(dict);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改保存字典类型
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysDictData dict)
    {
        SysUser user = sysUserService.getLoginUser();
        dict.setUpdateBy(user.getUserName());
        int rows = dictDataService.updateDictData(dict);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{dictCodes}")
    public Result remove(@PathVariable Long[] dictCodes)
    {
        int rows = dictDataService.deleteDictDataByIds(dictCodes);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }
}
