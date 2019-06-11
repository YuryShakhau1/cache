package by.shakhau.cache.service.mapper;

import by.shakhau.cache.persistence.entity.CacheItemEntity;
import by.shakhau.cache.service.dto.CacheItem;
import org.springframework.stereotype.Component;

@Component
public class CacheItemMapper implements Mapper<CacheItemEntity, CacheItem> {


    @Override
    public CacheItemEntity toEntity(CacheItem dto) {
        if (dto == null) {
            return null;
        }

        CacheItemEntity entity = new CacheItemEntity();
        entity.setKey(dto.getKey());
        entity.setValue(dto.getValue());
        return entity;
    }

    @Override
    public CacheItem toDto(CacheItemEntity entity) {
        if (entity == null) {
            return null;
        }

        CacheItem dto = new CacheItem();
        dto.setKey(entity.getKey());
        dto.setValue(entity.getValue());
        return dto;
    }
}
