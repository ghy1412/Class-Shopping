package com.dreamone.service.impl;

import com.dreamone.service.CacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    private Cache<String,Object> cache = null;

    @PostConstruct
    public void init(){
        cache = CacheBuilder.newBuilder().initialCapacity(10)
                .maximumSize(50)
                .expireAfterWrite(60,TimeUnit.SECONDS).build();

    }
    @Override
    public void setCommonCache(String key, Object value) {
        cache.put(key,value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return cache.getIfPresent(key);
    }
}
