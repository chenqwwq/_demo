package cn.chenqwwq;

import cn.chenqwwq.config.EventBusProperties;
import cn.chenqwwq.dto.EventObj;
import cn.chenqwwq.listener.EventListenerChain;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
public class EventBusHolder {

    private static AtomicReference<EventBusHolder> INSTANCE = new AtomicReference<>();

    public static EventBusHolder getEventBus() {
        return Optional.ofNullable(INSTANCE.get()).orElseThrow(() -> new RuntimeException("EventBus initialization is not complete"));
    }

    private final EventBus eventBus;
    private final AsyncEventBus asyncEventBus;
    private final EventListenerChain listenerChain;

    public EventBusHolder(ApplicationContext context, EventBusProperties properties) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(properties.getCoreSize(), properties.getMaxSize(), properties.getKeepAliveTime(),
                TimeUnit.MICROSECONDS, new LinkedBlockingQueue<>(), new ThreadFactory() {
            final AtomicInteger cnt = new AtomicInteger();

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, properties.getThreadPoolName() + "-" + cnt.incrementAndGet());
            }
        });
        EventBusHolder.INSTANCE.compareAndSet(null, this);
        this.eventBus = new EventBus("spring-eventbus");
        this.asyncEventBus = new AsyncEventBus("spring-eventbus", executor);
        this.listenerChain = new EventListenerChain(context);
    }

    public void register(Collection<Object> handlers) {
        for (Object o : handlers) {
            eventBus.register(o);
            asyncEventBus.register(o);
        }
    }

    public void unRegister(Collection<Object> handlers) {
        for (Object o : handlers) {
            eventBus.unregister(o);
            asyncEventBus.unregister(o);
        }
    }

    public void post(EventObj event) {
        listenerChain.handleBefore(event, false);
        eventBus.post(event);
        listenerChain.handleAfter(event, false);
    }

    public void postAsync(EventObj event) {
        listenerChain.handleBefore(event, true);
        asyncEventBus.post(event);
        listenerChain.handleBefore(event, true);
    }
}
