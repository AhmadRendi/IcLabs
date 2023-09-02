package com.example.iclabs.config;

import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;

import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
@EnableCaching
public class CacheConfig {

    private  CaffeineCacheManager cacheManager;

    public CacheManager cacheManager(){
        cacheManager.setCaffeine(Caffeine.newBuilder());
        cacheManager.setAllowNullValues(false);
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(0)
                .maximumSize(150)
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .weakKeys()
                .removalListener(new CustomRemovalListener())
                .recordStats();
    }

    private static class  CustomRemovalListener implements RemovalListener<Object, Object>{
        @Override
        public void onRemoval(@Nullable Object key, @Nullable Object value, RemovalCause removalCause) {
            System.out.format("removal listener called with key[%s], cause [%s], evicted [%s] \n",
                    key,removalCause.toString(), removalCause.wasEvicted());
        }
    }
}
