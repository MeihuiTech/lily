package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysNotice;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.service.ISysNoticeService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 公告 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController{
    @Autowired
    private ISysNoticeService noticeService;

    @Autowired
    private ISysUserService sysUserService;

    /**
     * 获取通知公告列表
     */
    @GetMapping("/list")
    public Map<String,Object> list(SysNotice notice){
        return noticeService.selectNoticeList(notice);
    }

    /**
     * 根据通知公告编号获取详细信息
     */
    @GetMapping(value = "/{noticeId}")
    public Result getInfo(@PathVariable Long noticeId){
        return Result.success(noticeService.selectNoticeById(noticeId));
    }

    /**
     * 新增通知公告
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysNotice notice)
    {
        SysUser user = sysUserService.getLoginUser();
        notice.setCreateBy(user.getUserName());
        int rows = noticeService.insertNotice(notice);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改通知公告
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysNotice notice)
    {
        SysUser user = sysUserService.getLoginUser();
        notice.setUpdateBy(user.getUserName());
        int rows = noticeService.updateNotice(notice);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除通知公告
     */
    @DeleteMapping("/{noticeIds}")
    public Result remove(@PathVariable Long[] noticeIds)
    {
        int rows = noticeService.deleteNoticeByIds(noticeIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }
}
