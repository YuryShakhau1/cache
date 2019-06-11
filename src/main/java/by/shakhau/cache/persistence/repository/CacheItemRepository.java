package by.shakhau.cache.persistence.repository;

import by.shakhau.cache.persistence.entity.CacheItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheItemRepository extends JpaRepository<CacheItemEntity, Long> {

    CacheItemEntity findByKey(String key);
}
