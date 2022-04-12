package cn.chenqwwq.annotation;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 标注在 Handler 类上用于注册 Bean
 *
 * @author chenqwwq
 * @date 2022/4/2
 **/
@Service
@Component
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
    boolean[] async() default false;
}
