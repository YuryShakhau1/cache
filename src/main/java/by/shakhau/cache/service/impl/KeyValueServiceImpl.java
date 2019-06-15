package by.shakhau.cache.service.impl;

import by.shakhau.cache.persistence.entity.KeyValueEntity;
import by.shakhau.cache.persistence.repository.KeyValueRepository;
import by.shakhau.cache.service.KeyValueCacheService;
import by.shakhau.cache.service.KeyValueService;
import by.shakhau.cache.service.dto.CacheKeyValue;
import by.shakhau.cache.service.dto.KeyValue;
import by.shakhau.cache.service.exception.ResourceNotFoundException;
import by.shakhau.cache.service.mapper.CacheItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Transactional
@Service
public class KeyValueServiceImpl implements KeyValueService {

    private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();

    @Autowired
    private KeyValueRepository keyValueRepository;

    @Autowired
    private KeyValueCacheService keyValueCacheService;

    @Autowired
    private CacheItemMapper cacheItemMapper;

    @Override
    public KeyValue findByKey(String key) {
        KeyValue keyValue;

        CacheKeyValue cacheKeyValue;
        try {
            LOCK.readLock().lock();
            cacheKeyValue = keyValueCacheService.find(key);
        } finally {
            LOCK.readLock().unlock();
        }

        if (cacheKeyValue != null) {
            keyValue = new KeyValue(cacheKeyValue.getKey(), cacheKeyValue.getValue());
            cacheKeyValue.setUpdateTimestamp(System.currentTimeMillis());
        } else {
            KeyValueEntity keyValueEntity;

            try {
                LOCK.readLock().lock();
                keyValueEntity = keyValueRepository.findByKey(key);
            } finally {
                LOCK.readLock().unlock();
            }

            if (keyValueEntity == null || !keyValueEntity.getActive()) {
                throw new ResourceNotFoundException(key + " key not found in cache");
            }

            keyValue = cacheItemMapper.toDto(keyValueEntity);
            keyValueCacheService.add(new CacheKeyValue(key, keyValue.getValue(), System.currentTimeMillis()));
        }

        return keyValue;
    }

    @Override
    public void add(String key, String value) {
        try {
            LOCK.writeLock().lock();
            KeyValueEntity keyValueEntity = keyValueRepository.findByKey(key);

            if (keyValueEntity == null) {
                keyValueEntity = new KeyValueEntity();
            }

            keyValueEntity.setKey(key);
            keyValueEntity.setValue(value);
            keyValueEntity.setActive(true);

            keyValueRepository.save(keyValueEntity);
            keyValueCacheService.add(new CacheKeyValue(key, value, System.currentTimeMillis()));
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public void markToDelete(String key) {
        try {
            LOCK.writeLock().lock();
            keyValueCacheService.delete(key);
            keyValueRepository.markToDelete(key);
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    @Override
    public void deleteMarked() {
        keyValueRepository.deleteMarked();
    }
}