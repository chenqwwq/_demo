package cn.chenqwwq.config;

import cn.chenqwwq.EventBusHolder;
import cn.chenqwwq.annotation.EventHandler;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
@Configuration
public class EventBusConfiguration implements ApplicationContextAware {

    private final static Class<EventHandler> HANDLER_ANNOTATION = EventHandler.class;

    private ApplicationContext context;

    @Bean
    @ConditionalOnBean(EventBusProperties.class)
    public EventBusHolder eventBusHolder(EventBusProperties properties) {
        EventBusHolder holder = new EventBusHolder(context, properties);
        Map<String, Object> beans = context.getBeansWithAnnotation(HANDLER_ANNOTATION);
        holder.register(beans.values());
        return holder;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
