package com.altas.cache.redis;

import com.altas.exception.JedisConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

public class JedisTemplate {

    private static JedisTemplate jedisTemplateInstance = new JedisTemplate();
    public static JedisTemplate instance(){
        if(null == jedisTemplateInstance)
            jedisTemplateInstance = new JedisTemplate();

        return jedisTemplateInstance;
    }

    private JedisConfig jedisConfig = new JedisConfig();
    public void loadJedisConfig(JedisConfig jedisConfig) throws JedisConfigException {
        this.jedisConfig = jedisConfig;
        loadJedisCluster();
    }

    private JedisCluster jedisCluster;
    private void loadJedisCluster() throws JedisConfigException {
        if(null == jedisConfig)
            throw new JedisConfigException();

        String[] serverArray = jedisConfig.getNodes().split(",");
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }

        String password = jedisConfig.getPassword();
        if(null == password || password.trim().isEmpty())
            jedisCluster = new JedisCluster(nodes,
                    jedisConfig.getConnectionTimeout(),
                    jedisConfig.getSoTimeout(),
                    jedisConfig.getMaxAttempts(),
                    new GenericObjectPoolConfig());
        else {
            jedisCluster = new JedisCluster(nodes,
                    jedisConfig.getConnectionTimeout(),
                    jedisConfig.getSoTimeout(),
                    jedisConfig.getMaxAttempts(),
                    jedisConfig.getPassword(), new GenericObjectPoolConfig());
        }
    }


    /**
     * 设置缓存
     * @param prefix 缓存前缀（用于区分缓存，防止缓存键值重复）
     * @param key    缓存key
     * @param value  缓存value
     */
    public void set(String prefix, String key, String value) {
        if(null == jedisCluster)
            return;

        if(StringUtils.isBlank(prefix))
            throw new IllegalArgumentException("prefix must not null!");
        if(StringUtils.isBlank(key))
            throw new IllegalArgumentException("key must not null!");

        jedisCluster.set(prefix + JedisConfig.KEY_SPLIT + key, value);

    }

    /**
     * 设置缓存，并且自己指定过期时间
     * @param prefix
     * @param key
     * @param value
     * @param expireTime 过期时间
     */
    public void setWithExpireTime(String prefix, String key, String value, int expireTime) {
        if(null == jedisCluster)
            return;

        if(StringUtils.isBlank(prefix))
            throw new IllegalArgumentException("prefix must not null!");
        if(StringUtils.isBlank(key))
            throw new IllegalArgumentException("key must not null!");

        jedisCluster.setex(prefix + JedisConfig.KEY_SPLIT + key, expireTime, value);

    }

    /**
     * 设置缓存，并且由配置文件指定过期时间
     * @param prefix
     * @param key
     * @param value
     */
    public void setWithExpireTime(String prefix, String key, String value) {
        if(null == jedisCluster)
            return;

        if(StringUtils.isBlank(prefix))
            throw new IllegalArgumentException("prefix must not null!");
        if(StringUtils.isBlank(key))
            throw new IllegalArgumentException("key must not null!");

        jedisCluster.setex(prefix + JedisConfig.KEY_SPLIT + key, jedisConfig.getExpireSeconds(), value);
    }

    /**
     * 获取指定key的缓存
     * @param prefix
     * @param key
     */
    public String get(String prefix, String key) {
        if(null == jedisCluster)
            return "";

        if(StringUtils.isBlank(prefix))
            throw new IllegalArgumentException("prefix must not null!");
        if(StringUtils.isBlank(key))
            throw new IllegalArgumentException("key must not null!");

        String value = jedisCluster.get(prefix + JedisConfig.KEY_SPLIT + key);

        return value;
    }

    /**
     * 删除指定key的缓存
     * @param prefix
     * @param key
     */
    public void deleteWithPrefix(String prefix, String key) {

        if(null == jedisCluster)
            return;

        if(StringUtils.isBlank(prefix))
            throw new IllegalArgumentException("prefix must not null!");
        if(StringUtils.isBlank(key))
            throw new IllegalArgumentException("key must not null!");

        jedisCluster.del(prefix + JedisConfig.KEY_SPLIT + key);
    }

    public void delete(String key) {
        if(StringUtils.isBlank(key))
            throw new IllegalArgumentException("key must not null!");

        jedisCluster.del(key);

    }
}
