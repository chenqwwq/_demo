package cn.chenqwwq.listener;

import cn.chenqwwq.dto.EventObj;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
public class EventListenerChain implements EventListener {

    private List<EventListener> listeners;

    public EventListenerChain(ApplicationContext context) {
        listeners = new ArrayList<>(context.getBeansOfType(EventListener.class).values());
    }

    @Override
    public void handleBefore(EventObj event,boolean async) {
        // DO NOTHING
    }

    @Override
    public void handleAfter(EventObj event,boolean async) {
        // DO NOTHING
    }
}
