package cn.chenqwwq;

import cn.chenqwwq.dto.BaseEventObj;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.seqno.RetentionLeaseActions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> data = new HashMap<>();
        data.put("genres","Actions");
        data.put("movieId","102");
        data.put("title","消失的她");
        IndexRequest request = new IndexRequest();
        request.index("movies");
        request.id("3");
        request.source(data);

        restHighLevelClient.indexAsync(request, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                System.out.println(indexResponse.getResult());
            }

            @Override
            public void onFailure(Exception e) {
                System.out.println(e.toString());
            }
        });
    }
}
