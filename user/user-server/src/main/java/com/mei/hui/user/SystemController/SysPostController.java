package com.mei.hui.user.SystemController;

import com.mei.hui.user.common.Constants;
import com.mei.hui.user.common.UserError;
import com.mei.hui.user.entity.SysPost;
import com.mei.hui.user.entity.SysUser;
import com.mei.hui.user.service.ISysPostService;
import com.mei.hui.user.service.ISysUserService;
import com.mei.hui.util.MyException;
import com.mei.hui.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 岗位信息操作处理
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/post")
public class SysPostController
{
    @Autowired
    private ISysPostService postService;
    @Autowired
    private ISysUserService sysUserService;

    /**
     * 获取岗位列表
     */
    @GetMapping("/list")
    public Map<String,Object> list(SysPost post)
    {
        return postService.selectPostList(post);
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @GetMapping(value = "/{postId}")
    public Result getInfo(@PathVariable Long postId)
    {
        return Result.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @PostMapping
    public Result add(@Validated @RequestBody SysPost post)
    {
        if (Constants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"岗位名称已存在");
        }
        else if (Constants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"岗位编码已存在");
        }
        SysUser user = sysUserService.getSysUser();
        post.setCreateBy(user.getUserName());
        int rows = postService.insertPost(post);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 修改岗位
     */
    @PutMapping
    public Result edit(@Validated @RequestBody SysPost post)
    {
        if (Constants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"岗位名称已存在");
        }
        else if (Constants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            throw MyException.fail(UserError.MYB_333333.getCode(),"岗位编码已存在");
        }
        SysUser user = sysUserService.getSysUser();
        post.setUpdateBy(user.getUserName());
        int rows = postService.updatePost(post);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{postIds}")
    public Result remove(@PathVariable Long[] postIds)
    {
        int rows = postService.deletePostByIds(postIds);
        return rows > 0 ? Result.OK : Result.fail(UserError.MYB_333333.getCode(),"失败");
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public Result optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return Result.success(posts);
    }
}
