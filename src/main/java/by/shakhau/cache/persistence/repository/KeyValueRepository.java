package by.shakhau.cache.persistence.repository;

import by.shakhau.cache.persistence.entity.KeyValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface KeyValueRepository extends JpaRepository<KeyValueEntity, Long> {

    KeyValueEntity findByKey(String key);

    @Query("update KeyValueEntity ci set ci.active = false where ci.key = ?1")
    void markToDelete(String key);

    @Query("delete from KeyValueEntity ci where ci.active = false")
    void deleteMarked();
}
