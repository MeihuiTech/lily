package com.mei.hui.miner.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 矿工存储服务配置
 * </p>
 *
 * @author 鲍红建
 * @since 2021-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("qiniu_store_config")
public class QiniuStoreConfig implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 矿工ID
     */
    private String minerId;

    /**
     * 七牛bucket名称
     */
    private String bucket;

    /**
     * 七牛prometheus 服务地址
     */
    private String prometheusDomain;

    /**
     * 七牛ECloud服务地址
     */
    private String ecloudDomain;

    /**
     * 用户名,AES加密后数据
     */
    private String userName;

    /**
     * 密码,AES加密数据
     */
    private String passWord;

    /**
     * 集群名称
     */
    private String clusterName;

    /**
     *
     */
    private String idcname;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QiniuStoreConfig config = (QiniuStoreConfig) o;
        return Objects.equals(prometheusDomain, config.prometheusDomain) &&
                Objects.equals(ecloudDomain, config.ecloudDomain) &&
                Objects.equals(userName, config.userName) &&
                Objects.equals(passWord, config.passWord) &&
                Objects.equals(clusterName, config.clusterName) &&
                Objects.equals(idcname, config.idcname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prometheusDomain, ecloudDomain, userName, passWord, clusterName, idcname);
    }
}
