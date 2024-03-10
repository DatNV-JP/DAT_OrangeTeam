package com.orange.redis;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface CacheService {

    boolean set(String key, Object value);

    boolean set(String key, Object value, Long expireTime);

    void remove(String... keys);

    /**
     * Delete key in bulk
     * @param pattern
     */
    void removePattern(String pattern);

    /**
           * Delete the corresponding value
     * @param key
     */
    void remove(String key);
    /**
           * Determine if there is a corresponding value in the cache
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
           * Read cache
     * @param key
     * @return
     */
    Object get(String key);
    /**
           * hash add
     * @param key
     * @param hashKey
     * @param value
     */
    void hmSet(String key, Object hashKey, Object value);

    void hmSetWithExpire(String hashKey, String key, Object value, long timeout, TimeUnit timeUnit);

    /**
           * Hash to get data
     * @param key
     * @param hashKey
     * @return
     */
    Object hmGet(String key, Object hashKey);

    List<Object> hmGetValues(Object hashKey);

    /**
           * List added
     * @param k
     * @param v
     */
    void lPush(String k, Object v);
    
   
	/**
     * List removed
	* @param k
	*/
	Object lPop(String k);

    /**
           * List acquisition
     * @param k
     * @param l
     * @param l1
     * @return
     */
    List<Object> lRange(String k, long l, long l1);

    /**
           * Collection added
     * @param key
     * @param value
     */
    void setArray(String key, Object value);

    /**
           * Collection acquisition
     * @param key
     * @return
     */
    Set<Object> getArray(String key);

    /**
           * Ordered collection added
     * @param key
     * @param value
     * @param score
     */
    void zAdd(String key, Object value, double score);

    /**
           * Ordered collection acquisition
     * @param key
     * @param score
     * @param score1
     * @return
     */
    Set<Object> rangeByScore(String key, double score, double score1);

    void hmDelete(String s, String... fieldsToDelete);
}
