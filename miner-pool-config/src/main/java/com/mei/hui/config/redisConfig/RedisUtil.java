package com.mei.hui.config.redisConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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
        return String.valueOf(redisTemplate.opsForValue().get(key));
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
     * 将value加入到 Set 集合中
     * @param key
     * @param value
     * @return
     */
    public long sadd(String key,String... value){
        return redisTemplate.opsForSet().add(key,value);
    }

    /**
     * 返回 Set 集合中所有成员
     * @param key
     * @return
     */
    public Set<String> smembers(String key){
        return redisTemplate.opsForSet().members(key);
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


}