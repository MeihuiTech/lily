package com.mei.hui.config.jwtConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 * 
 * @author ruoyi
 */
@Component
@ConfigurationProperties(prefix = "ruoyi")
public class RuoYiConfig
{
    /** 上传路径 */
    private static String profile;
    /**
     * jwt 秘钥
     */
    private String jwtSecret;
    /**
     * token 过去时间
     */
    private long jwtMinutes;

    /**
     * aes 秘钥
     */
    private String aesSecret;

    private String logUrl;

    //sdk 私钥
    private String privateKey;

    private Long visitorUserRoleId;
    private String visitorName;
    private String visitorEmail;
    private String visitorMobile;
    private String visitorAddress;

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public String getVisitorMobile() {
        return visitorMobile;
    }

    public void setVisitorMobile(String visitorMobile) {
        this.visitorMobile = visitorMobile;
    }

    public String getVisitorAddress() {
        return visitorAddress;
    }

    public void setVisitorAddress(String visitorAddress) {
        this.visitorAddress = visitorAddress;
    }

    public Long getVisitorUserRoleId() {
        return visitorUserRoleId;
    }

    public void setVisitorUserRoleId(Long visitorUserRoleId) {
        this.visitorUserRoleId = visitorUserRoleId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getLogUrl() {
        return logUrl;
    }

    public void setLogUrl(String logUrl) {
        this.logUrl = logUrl;
    }

    public static String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        RuoYiConfig.profile = profile;
    }
    /**
     * 获取头像上传路径
     */
    public static String getAvatarPath()
    {
        return getProfile() + "/avatar";
    }
    /**
     * 获取下载路径
     */
    public static String getDownloadPath()
    {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath()
    {
        return getProfile() + "/upload";
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getJwtMinutes() {
        return jwtMinutes;
    }

    public void setJwtMinutes(long jwtMinutes) {
        this.jwtMinutes = jwtMinutes;
    }

    public String getAesSecret() {
        return aesSecret;
    }

    public void setAesSecret(String aesSecret) {
        this.aesSecret = aesSecret;
    }
}
