package com.shujiai.ka.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis 工具类
 */
@Component
public class RedisUtil {
	private static final Logger LOG = LoggerFactory.getLogger(RedisUtil.class);

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	/**
	 * 指定缓存失效时间
	 * @param key  键
	 * @param time 时间(秒)
	 * @return true or false
	 */
	public boolean expire(String key, long time) {
		try {
			if (ObjectUtils.isEmpty(key)) {
				throw new Exception("key is null.");
			}
			if (time > 0) {
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil expire]key:{}, time:{}, exception:{}", key, time, e);
			return false;
		}
	}

	/**
	 * 根据key 获取过期时间
	 * @param key 键 不能为null
	 * @return 时间(秒) 返回0代表为永久有效（返回-1为操作失败）
	 */
	public long getExpire(String key) {
		try {
			if (ObjectUtils.isEmpty(key)) {
				throw new Exception("key is null.");
			}
			Long expireTime = redisTemplate.getExpire(key, TimeUnit.SECONDS);
			if (ObjectUtils.isEmpty(expireTime)) {
				throw new Exception("expire time is null.");
			}
			return expireTime;
		} catch (Exception e) {
			LOG.error("[RedisUtil getExpire]key:{}, exception:{}", key, e);
			return -1;
		}
	}

	/**
	 * 判断key是否存在
	 * @param key 键
	 * @return true 存在 false不存在
	 */
	public boolean hasKey(String key) {
		try {
			if (ObjectUtils.isEmpty(key)) {
				throw new Exception("key is null.");
			}
			Boolean isExist = redisTemplate.hasKey(key);
			if (ObjectUtils.isEmpty(isExist)) {
				throw new Exception("key is not exist.");
			}
			return isExist;
		} catch (Exception e) {
			LOG.error("[RedisUtil hasKey]key:{}, exception:{}", key, e);
			return false;
		}
	}

	/**
	 * 删除缓存
	 * @param key 可以传一个值 或多个
	 */
	public void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				redisTemplate.delete(key[0]);
			} else {
				redisTemplate.delete(Arrays.asList(key));
			}
		}
	}

	// ============================String=============================
	/**
	 * 普通缓存获取
	 * @param key 键
	 * @return 值
	 */
	public Object get(String key) {
		return key == null ? null : redisTemplate.opsForValue().get(key);
	}

	/**
	 * 普通缓存放入
	 * @param key   键
	 * @param value 值
	 * @return true成功 false失败
	 */
	public boolean set(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil set]key:{}, exception:{}", key, e);
			return false;
		}

	}

	/**
	 * 普通缓存放入并设置时间
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
	 * @return true成功 false 失败
	 */
	public boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil set]key:{}, value:{}, time:{}, exception:{}", key, value, time, e);
			return false;
		}
	}

	/**
	 * 递增
	 * @param key 键
	 * @param delta  要增加几(大于0)
	 * @return 递增后的值
	 */
	public long incr(String key, long delta) throws Exception {
		if (delta < 0) {
			throw new RuntimeException("递增因子必须大于0");
		}
		Long curValue = redisTemplate.opsForValue().increment(key, delta);
		if (ObjectUtils.isEmpty(curValue)) {
			throw new Exception("incr fail.");
		}
		return curValue;
	}

	/**
	 * 递减
	 * @param key 键
	 * @param delta  要减少几(小于0)
	 * @return 递减后的值
	 */
	public long decr(String key, long delta) throws Exception {
		if (delta < 0) {
			throw new RuntimeException("递减因子必须大于0");
		}
		Long curValue = redisTemplate.opsForValue().increment(key, -delta);
		if (ObjectUtils.isEmpty(curValue)) {
			throw new Exception("decr fail.");
		}
		return curValue;
	}

	// ================================Map=================================
	/**
	 * HashGet
	 *
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return 值
	 */
	public Object hget(String key, String item) {
		return redisTemplate.opsForHash().get(key, item);
	}

	/**
	 * 获取hashKey对应的所有键值
	 *
	 * @param key 键
	 * @return 对应的多个键值
	 */
	public Map<Object, Object> hmget(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * HashSet
	 *
	 * @param key 键
	 * @param map 对应多个键值
	 * @return true 成功 false 失败
	 */
	public boolean hmset(String key, Map<String, Object> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil hmset]key:{}, exception:{}", key, e);
			return false;
		}
	}

	/**
	 * HashSet 并设置时间
	 * @param key  键
	 * @param map  对应多个键值
	 * @param time 时间(秒)
	 * @return true成功 false失败
	 */
	public boolean hmset(String key, Map<String, Object> map, long time) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil hmset expire]key:{}, exception:{}", key, e);
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @return true 成功 false失败
	 */
	public boolean hset(String key, String item, Object value) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil hset]key:{}, item:{}, value:{}, exception:{}", key, item, value, e);
			return false;
		}
	}

	/**
	 * 向一张hash表中放入数据,如果不存在将创建
	 * @param key   键
	 * @param item  项
	 * @param value 值
	 * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
	 * @return true 成功 false失败
	 */
	public boolean hset(String key, String item, Object value, long time) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil hset]key:{}, item:{}, value:{}, time:{}, exception:{}",
					key, item, value, time, e);
			return false;
		}
	}

	/**
	 * 删除hash表中的值
	 * @param key  键 不能为null
	 * @param item 项 可以使多个 不能为null
	 */
	public void hdel(String key, Object... item) {
		redisTemplate.opsForHash().delete(key, item);
	}

	/**
	 * 判断hash表中是否有该项的值
	 * @param key  键 不能为null
	 * @param item 项 不能为null
	 * @return true 存在 false不存在
	 */
	public boolean hHasKey(String key, String item) {
		return redisTemplate.opsForHash().hasKey(key, item);
	}

	/**
	 * hash递增 如果不存在,就会创建一个 并把新增后的值返回
	 * @param key  键
	 * @param item 项
	 * @param by   要增加几(大于0)
	 * @return 增加后的值
	 */
	public double hincr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, by);
	}

	/**
	 * hash递减
	 * @param key  键
	 * @param item 项
	 * @param by   要减少记(小于0)
	 * @return 递减后的值
	 */
	public double hdecr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, -by);
	}

	// ============================set=============================
	/**
	 * 根据key获取Set中的所有值
	 * @param key 键
	 * @return  返回set
	 */
	public Set<Object> sGet(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			LOG.error("[RedisUtil sGet]key:{}, exception:{}", key, e);
			return null;
		}
	}

	/**
	 * 根据value从一个set中查询,是否存在
	 * @param key   键
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public boolean sHasKey(String key, Object value) {
		try {
			Boolean isExist = redisTemplate.opsForSet().isMember(key, value);
			if (ObjectUtils.isEmpty(isExist)) {
				throw new Exception("key is not exist.");
			}
			return isExist;
		} catch (Exception e) {
			LOG.error("[RedisUtil sHasKey]key:{}, exception:{}", key, e);
			return false;
		}
	}

	/**
	 * 将数据放入set缓存
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 成功个数(-1为操作失败)
	 */
	public long sSet(String key, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().add(key, values);
			if (ObjectUtils.isEmpty(count)) {
				throw new Exception("operate fail.");
			}
			return count;
		} catch (Exception e) {
			LOG.error("[RedisUtil sSet]key:{}, exception:{}", key, e);
			return -1;
		}
	}

	/**
	 * 将set数据放入缓存
	 * @param key    键
	 * @param time   时间(秒)
	 * @param values 值 可以是多个
	 * @return 成功个数(-1为操作失败)
	 */
	public long sSetAndTime(String key, long time, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().add(key, values);
			if (ObjectUtils.isEmpty(count)) {
				throw new Exception("operate fail.");
			}
			if (time > 0) {
				expire(key, time);
			}
			return count;
		} catch (Exception e) {
			LOG.error("[RedisUtil sSetAndTime]key:{}, time:{}, exception:{}", key, time, e);
			return -1;
		}
	}

	/**
	 * 获取set缓存的长度
	 * @param key 键
	 * @return 返回长度
	 */
	public long sGetSetSize(String key) {
		try {
			Long size = redisTemplate.opsForSet().size(key);
			if (ObjectUtils.isEmpty(size)) {
				throw new Exception("operate fail.");
			}
			return size;
		} catch (Exception e) {
			LOG.error("[RedisUtil sGetSetSize]key:{}, exception:{}", key, e);
			return 0;
		}
	}

	/**
	 * 移除值为value的
	 * @param key    键
	 * @param values 值 可以是多个
	 * @return 移除的个数(-1为操作失败)
	 */
	public long setRemove(String key, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().remove(key, values);
			if (ObjectUtils.isEmpty(count)) {
				throw new Exception("remove fail.");
			}
			return count;
		} catch (Exception e) {
			LOG.error("[RedisUtil setRemove]key:{}, exception:{}", key, e);
			return -1;
		}
	}
	// ===============================list=================================

	/**
	 * 获取list缓存的内容
	 * @param key   键
	 * @param start 开始
	 * @param end   结束 0 到 -1代表所有值
	 * @return  返回List
	 */
	public List<Object> lGet(String key, long start, long end) {
		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			LOG.error("[RedisUtil lGet]key:{}, start:{}, end:{}, exception:{}", key, start, end, e);
			return null;
		}
	}

	/**
	 * 获取list缓存的长度
	 * @param key 键
	 * @return 返回长度 (返回-1为操作失败)
	 */
	public long lGetListSize(String key) {
		try {
			Long size = redisTemplate.opsForList().size(key);
			if (ObjectUtils.isEmpty(size)) {
				throw new Exception("operate fail.");
			}
			return size;
		} catch (Exception e) {
			LOG.error("[RedisUtil lGetListSize]key:{}, exception:{}", key, e);
			return -1;
		}
	}

	/**
	 * 通过索引 获取list中的值
	 * @param key   键
	 * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
	 * @return 返回索引对应的值
	 */
	public Object lGetIndex(String key, long index) {
		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			LOG.error("[RedisUtil lGetIndex]key:{}, index:{}, exception:{}", key, index, e);
			return null;
		}
	}

	/**
	 * 将list放入缓存
	 * @param key   键
	 * @param value 值
	 * @return 返回true or false
	 */
	public boolean lSet(String key, Object value) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil lSet]key:{}, value:{}, exception:{}", key, value, e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return 返回true or false
	 */
	public boolean lSet(String key, Object value, long time) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil lSet]key:{}, value:{}, time:{}, exception:{}", key, value, time, e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * @param key   键
	 * @param value 值
	 * @return  返回true or false
	 */
	public boolean lSet(String key, List<Object> value) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil lSet]key:{}, exception:{}", key, e);
			return false;
		}
	}

	/**
	 * 将list放入缓存
	 * @param key   键
	 * @param value 值
	 * @param time  时间(秒)
	 * @return 返回true or false
	 */
	public boolean lSet(String key, List<Object> value, long time) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil lSet]key:{}, time:{}, exception:{}", key, time, e);
			return false;
		}
	}

	/**
	 * 根据索引修改list中的某条数据
	 *
	 * @param key   键
	 * @param index 索引
	 * @param value 值
	 * @return  返回更新的数量
	 */
	public boolean lUpdateIndex(String key, long index, Object value) {
		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			LOG.error("[RedisUtil lUpdateIndex]key:{}, index:{}, value:{}, exception:{}", key, index, value, e);
			return false;
		}
	}

	/**
	 * 移除N个值为value
	 *
	 * @param key   键
	 * @param count 移除多少个
	 * @param value 值
	 * @return 移除的个数(-1为操作失败)
	 */
	public long lRemove(String key, long count, Object value) {
		try {
			Long removedCount = redisTemplate.opsForList().remove(key, count, value);
			if (ObjectUtils.isEmpty(removedCount)) {
				throw new Exception("return remove count is null.");
			}
			return removedCount;
		} catch (Exception e) {
			LOG.error("[RedisUtil lRemove]key:{}, count:{}, value:{}, exception:{}", key, count, value, e);
			return -1;
		}
	}

	/**
	 *  获取指定前缀的一系列key
	 *  使用scan命令代替keys, Redis是单线程处理，keys命令在KEY数量较多时，
	 *  操作效率极低【时间复杂度为O(N)】，该命令一旦执行会严重阻塞线上其它命令的正常请求
	 * @param keyPrefix  key前缀
	 * @return  匹配前缀的key
	 */
	private Set<String> keys(String keyPrefix) {
		String realKey = keyPrefix + "*";
		try {
			return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
				Set<String> binaryKeys = new HashSet<>();
				Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match(realKey).count(Integer.MAX_VALUE).build());
				while (cursor.hasNext()) {
					binaryKeys.add(new String(cursor.next()));
				}
				return binaryKeys;
			});
		} catch (Exception e) {
			LOG.error("[RedisUtil keys]keyPrefix:{}, exception:{}", keyPrefix, e);
		}

		return null;
	}

	/**
	 *  删除指定前缀的一系列key
	 * @param keyPrefix 移除的key前缀
	 */
	public void removeAll(String keyPrefix) {
		try {
			Set<String> keys = keys(keyPrefix);
			if (ObjectUtils.isEmpty(keys)) {
				throw new Exception("key is not exist.");
			}
			Long count = redisTemplate.delete(keys);
			if (ObjectUtils.isEmpty(count)) {
				throw new Exception("removeAll fail.");
			}
		} catch (Throwable e) {
			LOG.error("[RedisUtil removeAll]keyPrefix:{}, exception:{}", keyPrefix, e);
		}
	}
}
