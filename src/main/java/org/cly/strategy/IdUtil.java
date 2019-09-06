package org.cly.strategy;

import cn.hutool.core.util.NumberUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.math.BigDecimal;

public class IdUtil implements InitializingBean {

    private static final String REDIS_KEY = "alpha:notice:noticev2:id";

    @Autowired
    private RedisTemplate redisTemplate;


    static RedisAtomicLong entityIdCounter=null;

    /**
     * 获取id
     *
     * @return id
     */
    public static Integer getId() {
        long andIncrement = entityIdCounter.getAndIncrement();
        BigDecimal add = NumberUtil.add(60000000L + andIncrement);
        return add.intValue();
    }


    @Override
    public void afterPropertiesSet() {
        entityIdCounter = new RedisAtomicLong(REDIS_KEY, redisTemplate.getConnectionFactory());
    }
}
