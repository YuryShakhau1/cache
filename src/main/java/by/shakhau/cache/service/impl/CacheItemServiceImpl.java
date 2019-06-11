package by.shakhau.cache.service.impl;

import by.shakhau.cache.persistence.entity.CacheItemEntity;
import by.shakhau.cache.persistence.repository.CacheItemRepository;
import by.shakhau.cache.service.CacheItemService;
import by.shakhau.cache.service.dto.CacheItem;
import by.shakhau.cache.service.exception.ResourceNotFoundException;
import by.shakhau.cache.service.mapper.CacheItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CacheItemServiceImpl implements CacheItemService {

    @Autowired
    private CacheItemRepository cacheItemRepository;

    @Autowired
    private CacheItemMapper cacheItemMapper;

    @Override
    public CacheItem findByKey(String key) {
        CacheItem cacheItem = cacheItemMapper.toDto(cacheItemRepository.findByKey(key));

        if (cacheItem == null) {
            throw new ResourceNotFoundException(key + " key not found in cache");
        }

        return cacheItem;
    }

    @Override
    public void add(CacheItem cacheItem) {
        CacheItemEntity cacheItemEntity = cacheItemRepository.findByKey(cacheItem.getKey());

        if (cacheItemEntity == null) {
            cacheItemEntity = new CacheItemEntity();
        }

        cacheItemEntity.setKey(cacheItem.getKey());
        cacheItemEntity.setValue(cacheItem.getValue());
        cacheItemRepository.save(cacheItemEntity);
    }
}