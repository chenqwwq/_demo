package cn.chenqwwq.dto;

import java.util.UUID;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
public class BaseEventObj implements EventObj {
    private final String id;
    private final Object original;

    public BaseEventObj(Object original) {
        this(UUID.randomUUID().toString(), original);
    }

    public BaseEventObj(String id, Object original) {
        this.id = id;
        this.original = original;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public Object original() {
        return original;
    }
}
