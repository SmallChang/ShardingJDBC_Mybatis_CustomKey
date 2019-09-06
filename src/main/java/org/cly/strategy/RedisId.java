package org.cly.strategy;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class RedisId{

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public String getId() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        Date date=new Date();
        String formatDate=sdf.format(date);
        String key=formatDate;
        Long incr = getIncr(key, getCurrent2TodayEndMillisTime());
        if(incr==0) {
            incr = getIncr(key, getCurrent2TodayEndMillisTime());//从001开始
        }
        DecimalFormat df=new DecimalFormat("000");//三位序列号
        return formatDate+df.format(incr);
    }

    public Long getIncr(String key, long liveTime) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();

        if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.MILLISECONDS);//单位毫秒
        }
        return increment;
    }

    //现在到今天结束的毫秒数
    public Long getCurrent2TodayEndMillisTime() {
        Calendar todayEnd = Calendar.getInstance();
        // Calendar.HOUR 12小时制
        // HOUR_OF_DAY 24小时制
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTimeInMillis() - new Date().getTime();
    }
}
