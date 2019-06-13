package by.shakhau.cache.service;

import by.shakhau.cache.service.dto.CacheKeyValue;

public interface KeyValueCacheService {

    CacheKeyValue find(String key);
    void add(CacheKeyValue cacheKeyValue);
    void delete(String key);
}
