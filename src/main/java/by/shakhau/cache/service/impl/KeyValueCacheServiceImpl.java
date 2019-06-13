package by.shakhau.cache.service.impl;

import by.shakhau.cache.service.KeyValueCacheService;
import by.shakhau.cache.service.dto.CacheKeyValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class KeyValueCacheServiceImpl implements KeyValueCacheService {

    private static final Lock LOCK = new ReentrantLock();

    @Value("${cache.max.size}")
    private Long maxSize;

    private AtomicLong minTimestampAdded = new AtomicLong(System.currentTimeMillis());
    private Map<String, CacheKeyValue> keyValues = new ConcurrentHashMap<>();

    @Override
    public CacheKeyValue find(String key) {
        return keyValues.get(key);
    }

    @Override
    public void add(CacheKeyValue cacheKeyValue) {
        keyValues.put(cacheKeyValue.getKey(), cacheKeyValue);

        if (keyValues.size() > maxSize) {
            try {
                LOCK.lock();
                if (keyValues.size() > maxSize) {
                    clearOldRecords();
                }
            } finally {
                LOCK.unlock();
            }
        }
    }

    @Override
    public void delete(String key) {
        keyValues.remove(key);
    }

    private void clearOldRecords() {
        long minTimestamp = minTimestampAdded.get();
        long maxTimestamp = System.currentTimeMillis();
        long timestampToDate = (minTimestamp + maxTimestamp) / 2;
        keyValues.forEach((k, v) -> {
            if (timestampToDate > v.getUpdateTimestamp()) {
                keyValues.remove(k);
            }
        });
        minTimestampAdded.set(timestampToDate);
    }
}
