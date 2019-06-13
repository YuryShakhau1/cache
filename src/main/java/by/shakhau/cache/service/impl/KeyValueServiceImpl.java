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

@Transactional
@Service
public class KeyValueServiceImpl implements KeyValueService {

    @Autowired
    private KeyValueRepository keyValueRepository;

    @Autowired
    private KeyValueCacheService keyValueCacheService;

    @Autowired
    private CacheItemMapper cacheItemMapper;

    @Override
    public KeyValue findByKey(String key) {
        CacheKeyValue cacheKeyValue = keyValueCacheService.find(key);

        KeyValue keyValue;
        if (cacheKeyValue != null) {
            keyValue = new KeyValue(cacheKeyValue.getKey(), cacheKeyValue.getValue());
            cacheKeyValue.setUpdateTimestamp(System.currentTimeMillis());
        } else {
            KeyValueEntity keyValueEntity = keyValueRepository.findByKey(key);

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
        KeyValue keyValue = new KeyValue(key, value);
        KeyValueEntity keyValueEntity = keyValueRepository.findByKey(keyValue.getKey());

        if (keyValueEntity == null) {
            keyValueEntity = new KeyValueEntity();
        }

        keyValueEntity.setKey(keyValue.getKey());
        keyValueEntity.setValue(keyValue.getValue());
        keyValueEntity.setActive(true);

        keyValueRepository.save(keyValueEntity);
        keyValueCacheService.add(new CacheKeyValue(key, value, System.currentTimeMillis()));
    }

    @Override
    public void markToDelete(String key) {
        keyValueCacheService.delete(key);
        keyValueRepository.markToDelete(key);
    }

    @Override
    public void deleteMarked() {
        keyValueRepository.deleteMarked();
    }
}