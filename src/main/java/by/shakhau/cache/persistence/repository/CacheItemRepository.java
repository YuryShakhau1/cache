package by.shakhau.cache.persistence.repository;

import by.shakhau.cache.persistence.entity.CacheItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CacheItemRepository extends JpaRepository<CacheItemEntity, Long> {

    CacheItemEntity findByKey(String key);

    @Query("update CacheItemEntity ci set ci.actual = false where ci.id = ?1")
    void markToDelete(String key);

    @Query("select min(ci.updateTimestamp) from CacheItemEntity ci")
    Long minUpdateTimestamp();

    @Query("select max(ci.updateTimestamp) from CacheItemEntity ci")
    Long maxUpdateTimestamp();

    @Query("delete from CacheItemEntity ci where ci.actual = false and ci.updateTimestamp < timestampTo")
    void deleteOldRecordsTo(Long timestampTo);
}
