package com.mei.hui.config.redisConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置key-value
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置带生存时间的key-value
     * @param key 键
     * @param value 值
     * @param timeout 生存时间
     * @param unit 时间单位
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置指定数据的生存时间。
     * @param key 键
     * @param time 生存时间（秒）
     */
    public void expire(String key, long time) {
        redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 根据key，获取值
     * @param key 键
     * @return 获取到的值
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? null :String.valueOf(value);
    }

    /**
     * 删除指定信息。
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 获取指定的 key 集合
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    /**
     * Set集合：将value加入到 Set 集合中
     * @param key
     * @param value
     * @param time 设置key值删除时间，单位秒
     * @return
     */
    public long sadd(String key,String value,long time){
        Long result = redisTemplate.opsForSet().add(key, value);
        expire(key,time);
        return result;
    }

    /**
     * Set集合：移除集合中的指定 key 的一个或多个随机元素，移除后会返回移除的元素
     * @param key
     * @return
     */
    public String spop(String key){
        Object value = redisTemplate.opsForSet().pop(key);
        return value == null ? null :String.valueOf(value);
    }

    /**
     * 将 key对应的value中储存的数字值增一，然后返回。
     * @param key
     * @return
     */
    public long incr(String key){
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 将 key 对应的value中储存的数字值减一，然后返回。
     * @param key
     * @return
     */
    public long decr(String key){
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 判断 key 是否存在
     * @param key
     * @return
     */
    public boolean exists(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * Hash结构:hmset 命令设置hash值
     * @param key
     * @param field
     * @param value
     * @param time 设置key值删除时间，单位秒
     */
    public void hmset(String key,String field,String value,long time){
        redisTemplate.opsForHash().put(key,field,value);
        expire(key,time);
    }

    /**
     * Hash结构:hget命令,获取存储在哈希表中指定字段的值
     * @param key
     * @param field
     * @return
     */
    public String hget(String key,String field){
        Object value = redisTemplate.opsForHash().get(key, field);
        return value == null ? null :String.valueOf(value);
    }

    /**
     * Hash结构:hget命令,获取key对应的所有键值对
     * @param key
     * @return
     */
    public Map<String,String> hgetall(String key){
        Map<String,String> value = redisTemplate.opsForHash().entries(key);
        return value;
    }

    /**
     * Hash结构:hget命令,获取key对应的所有键值对
     * @param key
     * @param map
     * @param time 设置key值删除时间，单位秒
     */
    public void putall(String key,Map<String, String> map,long time){
        redisTemplate.opsForHash().putAll(key,map);
        expire(key,time);
    }


}