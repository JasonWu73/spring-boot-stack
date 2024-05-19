package net.wuxianjie.myspringbootstarter.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import net.wuxianjie.myspringbootstarter.json.JsonConfig;

/**
 * Redis 配置，仅在类路径中存在 {@code RedisTemplate} 时生效。
 */
@AutoConfiguration
@AutoConfigureAfter(JsonConfig.class)
@ConditionalOnClass(RedisTemplate.class)
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
        RedisConnectionFactory redisConnectionFactory,
        @Qualifier("jsonMapper") ObjectMapper jsonMapper
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer(jsonMapper);
        RedisSerializer<String> stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        return template;
    }
}
