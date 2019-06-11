package by.shakhau.cache.service.mapper;

import by.shakhau.cache.persistence.entity.Entity;
import by.shakhau.cache.service.dto.Dto;

import java.util.List;
import java.util.stream.Collectors;

public interface Mapper<E extends Entity<Long>, D extends Dto> {

    E toEntity(D dto);
    D toDto(E entity);

    default List<E> toEntityList(List<D> dtos) {
        return dtos.stream().map(d -> toEntity(d)).collect(Collectors.toList());
    }

    default List<D> toDtoList(List<E> entities) {
        return entities.stream().map(e -> toDto(e)).collect(Collectors.toList());
    }
}
