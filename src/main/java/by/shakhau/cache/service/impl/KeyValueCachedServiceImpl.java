package by.shakhau.cache.service.impl;

import by.shakhau.cache.service.KeyValueService;
import by.shakhau.cache.service.dto.CacheKeyValue;
import by.shakhau.cache.service.dto.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service("KeyValueCachedServiceImpl")
public class KeyValueCachedServiceImpl implements KeyValueService {

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    @Value("${cache.max.size}")
    private Long maxSize;

    private AtomicLong minTimestampAdded = new AtomicLong(System.currentTimeMillis());
    private Map<String, CacheKeyValue> keyValues = new ConcurrentHashMap<>();

    @Qualifier("KeyValueServiceImpl")
    @Autowired
    private KeyValueService keyValueService;

    @Override
    public KeyValue findByKey(String key) {
        try {
            LOCK.readLock().lock();
            CacheKeyValue cacheKeyValue = keyValues.get(key);

            if (cacheKeyValue != null) {
                return new KeyValue(key, cacheKeyValue.getValue());
            }

            KeyValue keyValue = keyValueService.findByKey(key);
            keyValues.put(key, new CacheKeyValue(key, keyValue.getValue(), System.currentTimeMillis()));
            return keyValue;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    @Override
    public void add(String key, String value) {
        try {
            LOCK.writeLock().lock();
            keyValueService.add(key, value);

            if (!keyValues.containsKey(key)) {
                keyValues.put(key, new CacheKeyValue(key, value, System.currentTimeMillis()));

                if (keyValues.size() > maxSize) {
                    clearOldRecords();
                }
            }
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public void markToDelete(String key) {
        try {
            LOCK.writeLock().lock();
            keyValues.remove(key);
            keyValueService.markToDelete(key);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public void deleteMarked() {
        keyValueService.deleteMarked();
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
