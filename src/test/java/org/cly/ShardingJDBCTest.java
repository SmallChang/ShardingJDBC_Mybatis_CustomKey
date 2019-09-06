package org.cly;

import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
//import org.apache.shardingsphere.shardingjdbc.jdbc.core.resultset.GeneratedKeysResultSet;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.cly.dao.UserMapper;
import org.cly.entity.User;
//import org.cly.strategy.TestJavaShardingJDBC;
import org.cly.dao.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
@Slf4j
public class ShardingJDBCTest {

    public final static String TABLENAME = "notice_v2";

    @Autowired
    UserMapper userMapper;

//    @Resource(name="test")
//    private DataSource myDataSource;


    @Test
    public void insertData() {
        System.out.println("---------------------------- Insert Data ----------------------------");
        List<String> result = new ArrayList<>(10);
        for (int i = 1; i <= 10; i++) {
            User notice = new User();
            notice.setObjectId(String.valueOf(i));
            notice.setType(String.valueOf(i));
            notice.setContent(i+"新增了XXX");
            notice.setNotifier("ED34H4534563Fadd"+ "232343"+i);
            System.out.println("notifier :"+notice.getNotifier().hashCode());
            userMapper.insertSelective(notice);
            result.add(notice.getNotifier());
        }
        System.out.println(result);
    }


    @Test
    public void printData() {
        System.out.println("---------------------------- Print Order Data -----------------------");
        List<User> users = userMapper.selectByPrimaryKey();
        for(User user:users){
            System.out.println(user);
        }
    }

//    @Test
//    public void insertData2() {
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(myDataSource);
//        List<?> resultList = jdbcTemplate.queryForList("select * from notice_v2");
//        System.out.println("===>>>>>>>>>>>" + resultList);
//    }
    @Test
    public void insertData2() {
        Jedis jedis = new Jedis("192.168.255.2",6379);
        jedis.set("id","0");
        jedis.close();
    }


    private ExecutorService divideTable = Executors.newFixedThreadPool(10);

    @Test
    /**
     * 迁移数据
     */
    public void data(){

        //十万
        List<String> Ids = userMapper.selectUsers();

        Map<Integer, List<String>> result = Ids.stream().collect(Collectors.groupingBy(i -> Math.abs(i.hashCode()%30)));

        TimeInterval timeInterval  = new TimeInterval();
        AtomicInteger all =new AtomicInteger(result.keySet().size());
        for(Integer key:result.keySet()){
            List<String> notifierIds = result.get(key);

            divideTable.execute(()->{
                try{
                    Map<Object, List<String>> objectObjectMap = groupList(notifierIds);

                    Collection<List<String>> values = objectObjectMap.values();
                    for (List<String> e : values) {
                        String tableName = TABLENAME+"_"+key;
                        userMapper.insertSelect(e,tableName);
                    }
                }catch(Exception | Error e){
                    log.error("notice transfer data error",e);
                }finally {
                    int i = all.decrementAndGet();//30--
                    long interval = timeInterval.interval();
                    long minute = timeInterval.intervalMinute();
                    log.info(" user spilt size:{},分钟:{},毫秒:{}",i,minute,interval);
                    if(i==0||i==1){
                        log.info("end user spilt size:{},分钟:{},毫秒:{}",i,minute,interval);
                    }
                }
            });
        }
    }

    private static <K,V> Map<K,V> groupList(List list) {
        int listSize = list.size();
        int toIndex = 3;
        //用map存起来新的分组后数据
        Map map = new HashMap();
        int keyToken = 0;
        for (int i = 0; i < list.size(); i += 3) {
            //作用为toIndex最后没有100条数据则剩余几条newList中就装几条
            if (i + 3 > listSize) {
                toIndex = listSize - i;
            }
            List newList = list.subList(i, i + toIndex);
            map.put("keyName" + keyToken, newList);
            keyToken++;
        }
        return map;
    }


    @Test
    /**
     * 查该用户在哪张表中
     */
    public void data2(){

       List<String> Ids  = Lists.newArrayList("EAA65D553FED11E998137CD30AEB153E");
       List<Map<Integer,Object>> maps =  Ids.stream().map(id->{
           Map<Integer,Object> map = new HashMap<>();
           Integer h = id.hashCode();
           Integer h1 = id.hashCode()%30;
           Integer h2 = Math.abs(id.hashCode()%30);
           System.out.println(id);
           System.out.println(h);
           System.out.println(h1);
           //最终该用户在哪张表
           System.out.println(h2);
           map.put(h2,id);
           return map;
        }).collect(Collectors.toList());
    }


}
