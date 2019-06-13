package by.shakhau.cache.service.mapper;

import by.shakhau.cache.persistence.entity.KeyValueEntity;
import by.shakhau.cache.service.dto.KeyValue;
import org.springframework.stereotype.Component;

@Component
public class CacheItemMapper implements Mapper<KeyValueEntity, KeyValue> {


    @Override
    public KeyValueEntity toEntity(KeyValue dto) {
        if (dto == null) {
            return null;
        }

        KeyValueEntity entity = new KeyValueEntity();
        entity.setKey(dto.getKey());
        entity.setValue(dto.getValue());
        return entity;
    }

    @Override
    public KeyValue toDto(KeyValueEntity entity) {
        if (entity == null) {
            return null;
        }

        KeyValue dto = new KeyValue();
        dto.setKey(entity.getKey());
        dto.setValue(entity.getValue());
        return dto;
    }
}
