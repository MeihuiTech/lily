package com.mei.hui.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mei.hui.config.CommonUtil;
import com.mei.hui.config.HttpRequestUtil;
import com.mei.hui.config.redisConfig.RedisUtil;
import com.mei.hui.user.common.Constants;
import com.mei.hui.user.entity.WhiteUrl;
import com.mei.hui.user.mapper.WhiteUrlMapper;
import com.mei.hui.user.service.ISysMenuService;
import com.mei.hui.user.service.WhiteUrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 白名单 服务实现类
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-30
 */
@Service
@Slf4j
public class WhiteUrlServiceImpl extends ServiceImpl<WhiteUrlMapper, WhiteUrl> implements WhiteUrlService {
    @Autowired
    private RedisUtil redisUtils;
    @Autowired
    private ISysMenuService menuService;

    /**
     * 校验规则：
     * 1：先校验是否是白名单url,如果是则返回true;否则，校验是否拥有当前请求url的权限
     * @return
     */
    public boolean checkAutoUrl(String url, Integer userId){
        log.info("判断请求地址是否在白名单中");
        if(isWhiteUrl(url,findWhiteUrls())){
            return true;
        }

        log.info("请求地址不在白名单,进行权限校验");
        List<String> urls = menuService.findAutoUrlByUserId(Long.valueOf(userId+""));
        if(urls == null || urls.size() == 0){
            log.info("用户还没有配置权限");
            return false;
        }
        if(isWhiteUrl(url,urls)){
            return true;
        }
        return false;
    }

    /**
     *校验字符串是否匹配正则表达式集合中的至少一项
     * @param url 字符串
     * @param regex 正则表达式集合
     * @return
     */
    public boolean isWhiteUrl(String url,List<String> regex){
        boolean flag = false;
        for (String rg : regex){
            PathMatcher matcher = new AntPathMatcher();
            flag = matcher.match(rg, url);
            if(flag){
                log.info("匹配白名单地址:{}",rg);
                break;
            }
        }
        return flag;
    }

    /**
     * 获取白名单url
     * 先从redis获取，如果缓存没有则从mysql获取,然后缓存到redis,缓存时间8小时
     * @return
     */
    public List<String> findWhiteUrls(){
        Set<String> set = redisUtils.smembers(Constants.WhiteUrl);
        if(set != null && set.size() > 0){
            return new ArrayList(set);
        }
        List<WhiteUrl> list = this.list();
        List<String> lt = list.stream().map(v -> {
            redisUtils.sadd(Constants.WhiteUrl, v.getUrl(), 3600 * 8);
            return v.getUrl();
        }).collect(Collectors.toList());
        return lt;
    }

}
