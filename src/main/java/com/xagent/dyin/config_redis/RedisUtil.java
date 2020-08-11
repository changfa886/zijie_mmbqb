package com.xagent.dyin.config_redis;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Created by jonty on 2019/8/11.
 *
 * @Component是一个通用的Spring容器管理的单例bean组件。 而@Repository, @Service, @Controller就是针对不同的使用场景所采取的特定功能化的注解组件。
 * 因此，当你的一个类被@Component所注解，那么就意味着同样可以用@Repository, @Service, @Controller来替代它
 * 同时这些注解会具备有更多的功能，而且功能各异。
 * 最后，如果你不知道要在项目的业务层采用@Service还是@Component注解。那么，@Service是一个更好的选择。
 */
@Component
public class RedisUtil
{
    private static Logger logger = LoggerFactory.getLogger(RedisUtil.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 使用spring自带的redisTemplate
//    @Autowired
//    private RedisTemplate redisTemplate;

    // =============================common============================

    /**
     * scan 实现
     *
     * @param pattern  表达式
     * @param consumer 对迭代到的key进行操作
     */
    public void scan(String pattern, Consumer<byte[]> consumer)
    {
        // Long.MAX_VALUE 太大，会超时
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(4000).match(pattern).build()))
            {
                cursor.forEachRemaining(consumer);
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    // 只是简单遍历key
    public void scan(String pattern)
    {
//        long yyy = Long.MAX_VALUE;
//        int xxx = Integer.MAX_VALUE;
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(4000).match(pattern).build()))
            {
                while (cursor.hasNext())
                {
                    cursor.next();
                    // logger.info(new String(cursor.next(), StandardCharsets.UTF_8));
                    // todo ...
                }
                return null;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    // 遍历key，返回keys
    public List<String> keys(String pattern)
    {
        List<String> keys = new ArrayList<>();
        try
        {
            this.scan(pattern, item -> {
                //符合条件的key
                String key = new String(item, StandardCharsets.UTF_8);
                keys.add(key);
            });
        }
        catch (Exception e)
        {}
        return keys;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    public boolean expire(String key, long time)
    {
        try
        {
            if (time > 0)
            {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    // 发布消息队列
    public void convertAndSend(String channel, Object message)
    {
        redisTemplate.convertAndSend(channel, message);
    }

    public RedisSerializer<String> getStringSerializer()
    {
        return redisTemplate.getStringSerializer();
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key)
    {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key)
    {
        try
        {
            return redisTemplate.hasKey(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key)
    {
        if (key != null && key.length > 0)
        {
            if (key.length == 1)
            {
                redisTemplate.delete(key[0]);
            }
            else
            {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }
    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key)
    {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value)
    {
        try
        {
            redisTemplate.opsForValue().set(key, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time)
    {
        try
        {
            if (time > 0)
            {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            }
            else
            {
                set(key, value);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return 返回键 key 在执行incr操作之后的值
     */
    public long incr(String key, long delta)
    {
        if (delta < 0)
        {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return 返回键 key 在执行decr操作之后的值
     */
    public long decr(String key, long delta)
    {
        if (delta < 0)
        {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget(String key, String item)
    {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key)
    {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * Cursor<Map.Entry<Object, Object>> scan = hscan(key)
     * while (scan.hasNext()){
     * Map.Entry<Object, Object> next = scan.next();
     * }
     * scan.close();
     */
    public Cursor<Map.Entry<Object, Object>> hscan(String key)
    {
        return redisTemplate.opsForHash().scan(key, ScanOptions.NONE);
    }

    // count是每次扫描的key个数，并不是结果集个数。
    // count要根据扫描数据量大小而定，Scan虽然无锁，但是也不能保证在超过百万数据量级别搜索效率；
    // count不能太小，网络交互会变多，count要尽可能的大。
    // 在搜索结果集1万以内，建议直接设置为与所搜集大小相同
    public Cursor<Map.Entry<Object, Object>> hscan(String key, Long icount)
    {
        return redisTemplate.opsForHash().scan(key, ScanOptions.scanOptions().count(icount).build());
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map)
    {
        try
        {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time)
    {
        try
        {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0)
            {
                expire(key, time);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项 field建议使用String
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value)
    {
        try
        {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key, String item, Object value, long time)
    {
        if (TextUtils.isEmpty(item))
        {
            return true;
        }
        try
        {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0)
            {
                expire(key, time);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item)
    {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey(String key, String item)
    {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    /**
     * hash中key的元素数量
     *
     * @param key
     * @return
     */
    public long hsizeOfKey(String key)
    {
        Long size = redisTemplate.opsForHash().size(key);

        return size == null ? 0 : size;
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return
     */
    public long hincr(String key, String item, long by)
    {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return
     */
    public long hdecr(String key, String item, long by)
    {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
    // ============================set=============================

    public long hdecr(String key, String item, String dispatched, long by)
    {
        try
        {
            redisTemplate.opsForHash().put(key, dispatched, System.currentTimeMillis());
        }
        catch (Exception e)
        {
        }
        return redisTemplate.opsForHash().increment(key, item, -by);
    }

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGet(String key)
    {
        try
        {
            return redisTemplate.opsForSet().members(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key, Object value)
    {
        try
        {
            return redisTemplate.opsForSet().isMember(key, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, Object... values)
    {
        try
        {
            return redisTemplate.opsForSet().add(key, values);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key, long time, Object... values)
    {
        try
        {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0)
            {
                expire(key, time);
            }
            return count;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key)
    {
        try
        {
            return redisTemplate.opsForSet().size(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values)
    {
        try
        {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }
    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public List<Object> lGet(String key, long start, long end)
    {
        try
        {
            return redisTemplate.opsForList().range(key, start, end);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    public long lGetListSize(String key)
    {
        try
        {
            return redisTemplate.opsForList().size(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetIndex(String key, long index)
    {
        try
        {
            return redisTemplate.opsForList().index(key, index);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, Object value)
    {
        try
        {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, Object value, long time)
    {
        try
        {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0)
            {
                expire(key, time);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<Object> value)
    {
        try
        {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public boolean lSet(String key, List<Object> value, long time)
    {
        try
        {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0)
            {
                expire(key, time);
            }
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index, Object value)
    {
        try
        {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key, long count, Object value)
    {
        try
        {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }

    // 从左边删除一个,并返回该元素
    public Object lRemove(String key)
    {
        try
        {
            Object v = redisTemplate.opsForList().leftPop(key);
            return v;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    // 从左边删除一个,并返回该元素 阻塞x秒
    public Object leftPop(String key, long xseconds)
    {
        try
        {
            Object v = redisTemplate.opsForList().leftPop(key, xseconds, TimeUnit.SECONDS);
            return v;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    // 左边删除count个
    public long lRemove(String key, long count)
    {
        try
        {
            for (int i = 0; i < count; i++)
            {
                Object v = redisTemplate.opsForList().leftPop(key);
            }
            return 0;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 0;
        }
    }
}
