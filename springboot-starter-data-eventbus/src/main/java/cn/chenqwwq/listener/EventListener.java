package cn.chenqwwq.listener;

import cn.chenqwwq.dto.EventObj;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
public interface EventListener {

    default void handleBefore(EventObj event, boolean async) {}

    default void handleAfter(EventObj event, boolean async) {}
}
