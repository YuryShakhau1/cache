package by.shakhau.cache.service;

import by.shakhau.cache.service.dto.CacheItem;

public interface CacheItemService {

    CacheItem findByKey(String key);
    void add(CacheItem cacheItem);
    void markToDelete(String key);
}
