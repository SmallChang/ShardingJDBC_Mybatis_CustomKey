package org.cly.strategy;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shardingsphere.api.config.sharding.KeyGeneratorConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.cache.interceptor.DefaultKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.jdbc.Sql;


import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@ComponentScan
@Configuration
public class TestJavaShardingJDBC {

    @Bean(name="test")
    public DataSource getShardingDataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        shardingRuleConfig.setDefaultTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("NOTIFIER","notice_v2_$->{Math.abs(NOTIFIER.hashCode() % 3)}"));
        return ShardingDataSourceFactory.createDataSource(createDataSourceMap(), shardingRuleConfig,new Properties());
    }

    private static KeyGeneratorConfiguration getKeyGeneratorConfiguration() {
        //主键规则可以用雪花算法，也可以自定义Redis计数器作为主键，具体见RedisIdGenerator
        KeyGeneratorConfiguration result = new KeyGeneratorConfiguration("SNOWFLAKE","id");
        return result;
    }

    TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("notice_v2","ds0.notice_v2_${0..2}");
        result.setKeyGeneratorConfig(getKeyGeneratorConfiguration());
        return result;
    }

    Map<String, DataSource> createDataSourceMap(){
        Map<String, DataSource> result = new HashMap<>();
        result.put("ds0",createDataSource("root","123456","jdbc:mysql://localhost:3306/test_ShardingJDBC?characterEncoding=utf-8"));
        return result;
    }


    public DataSource createDataSource(String username,String password,String url) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setUrl(url);
        return dataSource;
    }

}
