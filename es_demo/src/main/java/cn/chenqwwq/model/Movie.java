package cn.chenqwwq.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author chenqwwq
 * @date 2022/4/2
 **/
@Document(indexName = "movies")
public class Movie {
}
