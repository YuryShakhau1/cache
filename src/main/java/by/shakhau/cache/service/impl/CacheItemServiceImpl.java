package by.shakhau.cache.service.impl;

import by.shakhau.cache.persistence.entity.CacheItemEntity;
import by.shakhau.cache.persistence.repository.CacheItemRepository;
import by.shakhau.cache.service.CacheItemService;
import by.shakhau.cache.service.dto.CacheItem;
import by.shakhau.cache.service.exception.ResourceNotFoundException;
import by.shakhau.cache.service.mapper.CacheItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class CacheItemServiceImpl implements CacheItemService {

    @Value("${cache.max.size}")
    private Long maxSize;

    @Autowired
    private CacheItemRepository cacheItemRepository;

    @Autowired
    private CacheItemMapper cacheItemMapper;

    @Override
    public CacheItem findByKey(String key) {
        CacheItemEntity cacheItemEntity = cacheItemRepository.findByKey(key);

        if (cacheItemEntity == null) {
            throw new ResourceNotFoundException(key + " key not found in cache");
        } else if (!cacheItemEntity.getActual()) {
            cacheItemEntity.setActual(true);
            cacheItemRepository.save(cacheItemEntity);
        }

        return cacheItemMapper.toDto(cacheItemEntity);
    }

    @Override
    public void add(CacheItem cacheItem) {
        if (cacheItemRepository.count() > maxSize) {
            clearOldRecords();
        }

        CacheItemEntity cacheItemEntity = cacheItemRepository.findByKey(cacheItem.getKey());

        if (cacheItemEntity == null) {
            cacheItemEntity = new CacheItemEntity();
        }

        cacheItemEntity.setKey(cacheItem.getKey());
        cacheItemEntity.setValue(cacheItem.getValue());
        cacheItemEntity.setActual(true);
        cacheItemRepository.save(cacheItemEntity);
    }

    @Override
    public void markToDelete(String key) {
        cacheItemRepository.markToDelete(key);
    }

    private void clearOldRecords() {
        Long minTimestamp = cacheItemRepository.maxUpdateTimestamp();
        Long maxTimestamp = cacheItemRepository.maxUpdateTimestamp();
        Long timestampToDate = (minTimestamp + maxTimestamp) / 2;
        cacheItemRepository.deleteOldRecordsTo(timestampToDate);
    }
}