package cn.chenqwwq.dto;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
public interface EventObj {
    /**
     * 获取事件 Id
     */
    String id();

    /**
     * 获取事件源
     */
    Object original();
}
