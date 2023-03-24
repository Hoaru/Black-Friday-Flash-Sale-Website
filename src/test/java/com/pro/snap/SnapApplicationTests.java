package com.pro.snap;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SnapApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript<Boolean> redisScript;

    @Test
    public void testLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 占位，key不存在才可以设置成功
        Boolean isLocked = valueOperations.setIfAbsent("k1", "v1");
        // 操作成功
        if(isLocked){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            Integer.parseInt("xxxx");
            // 结束操作，删除锁
            redisTemplate.delete("k1");
        }
        else{
            System.out.println("Please wait for other thread to release and try again later");
        }
    }
    @Test
    public void testLock02() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 给锁添加一个过期时间，防止应用在运行过程中抛出异常，导致锁无法正常释放
        Boolean isLocked = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);
        // 操作成功
        if(isLocked){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            Integer.parseInt("xxxx");
            // 结束操作，删除锁
            redisTemplate.delete("k1");
        }
        else{
            System.out.println("Please wait for other thread to release and try again later");
        }
    }
    @Test
    public void testLock03() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 随机生成value
        String value = UUID.randomUUID().toString();
        // 给锁添加一个过期时间，防止应用在运行过程中抛出异常，导致锁无法正常释放
        Boolean isLocked = valueOperations.setIfAbsent("k1", value, 120, TimeUnit.SECONDS);
        // 操作成功
        if(isLocked){
            valueOperations.set("name", "xxxx");
            String name = (String) valueOperations.get("name");
            System.out.println("name = " + name);
            System.out.println(valueOperations.get("k1"));
            Boolean result = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(result);
        }
        else{
            System.out.println("Please wait for other thread to release and try again later");
        }
    }

}
