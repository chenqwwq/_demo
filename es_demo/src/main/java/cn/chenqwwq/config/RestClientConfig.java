package cn.chenqwwq.config;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * @author chenqwwq
 * @date 2022/4/5
 **/
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration configuration = ClientConfiguration.create("121.43.140.94:9200");
        return RestClients.create(configuration).rest();
    }
}
