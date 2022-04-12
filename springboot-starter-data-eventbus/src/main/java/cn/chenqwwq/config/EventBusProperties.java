package cn.chenqwwq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
@Configuration
@ConfigurationProperties(prefix = "eventbus")
public class EventBusProperties {
    private String threadPoolName = "event-bus-threadpool";
    private int coreSize = Runtime.getRuntime().availableProcessors();
    private int maxSize = 100;
    private long keepAliveTime = 60 * 1000;

    public String getThreadPoolName() {
        return threadPoolName;
    }

    public void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public void setCoreSize(int coreSize) {
        this.coreSize = coreSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
