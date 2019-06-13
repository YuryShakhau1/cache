package by.shakhau.cache.service.dto;

public class CacheKeyValue implements Dto {

    private String key;
    private String value;
    private long updateTimestamp;

    public CacheKeyValue(String key, String value, Long updateTimestamp) {
        this.key = key;
        this.value = value;
        this.updateTimestamp = updateTimestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }
}
