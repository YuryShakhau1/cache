package by.shakhau.cache.rest;

import by.shakhau.cache.service.KeyValueService;
import by.shakhau.cache.service.dto.KeyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class KeyValueResource {

    @Qualifier("KeyValueCachedServiceImpl")
    @Autowired
    private KeyValueService keyValueService;

    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public KeyValue findByKey(@PathVariable String key) {
        return keyValueService.findByKey(key);
    }

    @PostMapping(value = "/")
    public void add(@RequestParam String key, String value) {
        keyValueService.add(key, value);
    }
}
