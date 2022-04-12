package cn.chenqwwq;

import cn.chenqwwq.dto.BaseEventObj;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.seqno.RetentionLeaseActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
@SpringBootApplication
public class EsDemoBootstrap implements CommandLineRunner {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    public static void main(String[] args) {
        SpringApplication.run(EsDemoBootstrap.class, args);
        EventBusHolder.getEventBus().post(new BaseEventObj("1", new Object()));
    }

    @Override
    public void run(String... args) throws Exception {
        IndexRequest indexRequest = new IndexRequest("postilhub", "user", "3");
    }
}
