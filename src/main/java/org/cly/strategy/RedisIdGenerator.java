package org.cly.strategy;

import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;


import java.util.Properties;


public class RedisIdGenerator implements ShardingKeyGenerator {

    private Properties properties = new Properties();

    @Override
    public Comparable<?> generateKey() {
        Integer id = IdUtil.getId();
        return id;
    }

    @Override
    public String getType() {
        return "redis";
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties=properties;
    }
}

