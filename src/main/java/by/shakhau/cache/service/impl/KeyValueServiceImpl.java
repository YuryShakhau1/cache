package by.shakhau.cache.service.impl;

import by.shakhau.cache.persistence.entity.KeyValueEntity;
import by.shakhau.cache.persistence.repository.KeyValueRepository;
import by.shakhau.cache.service.KeyValueService;
import by.shakhau.cache.service.dto.KeyValue;
import by.shakhau.cache.service.exception.ResourceNotFoundException;
import by.shakhau.cache.service.mapper.CacheItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("KeyValueServiceImpl")
public class KeyValueServiceImpl implements KeyValueService {

    @Autowired
    private KeyValueRepository keyValueRepository;

    @Autowired
    private CacheItemMapper cacheItemMapper;

    @Override
    public KeyValue findByKey(String key) {
        KeyValueEntity keyValueEntity = keyValueRepository.findByKey(key);

        if (keyValueEntity == null || !keyValueEntity.getActive()) {
            throw new ResourceNotFoundException(key + " key not found in cache");
        }

        return cacheItemMapper.toDto(keyValueEntity);
    }

    @Override
    public void add(String key, String value) {
        KeyValueEntity keyValueEntity = keyValueRepository.findByKey(key);

        if (keyValueEntity == null) {
            keyValueEntity = new KeyValueEntity();
        }

        keyValueEntity.setKey(key);
        keyValueEntity.setValue(value);
        keyValueEntity.setActive(true);

        keyValueRepository.save(keyValueEntity);
    }

    @Override
    public void markToDelete(String key) {
        keyValueRepository.markToDelete(key);
    }

    @Override
    public void deleteMarked() {
        keyValueRepository.deleteMarked();
    }
}