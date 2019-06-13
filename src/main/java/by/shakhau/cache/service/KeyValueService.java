package by.shakhau.cache.service;

import by.shakhau.cache.service.dto.KeyValue;

public interface KeyValueService {

    KeyValue findByKey(String key);
    void add(String key, String value);
    void markToDelete(String key);
    void deleteMarked();
}
