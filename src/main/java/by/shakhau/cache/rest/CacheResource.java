package by.shakhau.cache.rest;

import by.shakhau.cache.service.CacheItemService;
import by.shakhau.cache.service.dto.CacheItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheResource {

    @Autowired
    private CacheItemService cacheItemService;

    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CacheItem findByKey(@PathVariable String key) {
        return cacheItemService.findByKey(key);
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void add(@RequestBody CacheItem cacheItem) {
        cacheItemService.add(cacheItem);
    }

    @DeleteMapping(value = "/{key}")
    public void delete(@PathVariable String key) {
        cacheItemService.markToDelete(key);
    }
}
