package com.infinity.commerce.auth_service.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedisCacheManager {

    @Autowired
    private RedisTemplate<String , Object> redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    //serialization of localdatetime
    /**
     * Generic method to get data from redis or fetch from DB if not present
     * @param key redis key
     * @param ttl TTL in seconds
     * @param supplier function to get data if not cached
     * @return cached or newly fetched data
     */
    public <T> T get(String key , Long ttl , Supplier<T> supplier){

        String value = (String) redisTemplate.opsForValue().get(key);

        if(value == null) {
            T fetchedValue= supplier.get();
            // fetch from DB
            if(fetchedValue != null ){
                redisTemplate.opsForValue().set(key , serialize(fetchedValue) , ttl , TimeUnit.SECONDS);
                //Cache the value
                System.out.println("Data was not cached for key "+ key);
                return fetchedValue;
            }
        }

        return deserialize(value);
    }
    private <T> String serialize(T data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize data", e);
        }
    }

    private <T> T deserialize(String value) {
        try {
            return (T) objectMapper.readValue(value, Object.class);
            //abstract class --> TypeReference
//            return (T) objectMapper.convertValue((T) objectMapper.readValue(value, Object.class), new TypeReference<T>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize data", e);
        }
    }

}
