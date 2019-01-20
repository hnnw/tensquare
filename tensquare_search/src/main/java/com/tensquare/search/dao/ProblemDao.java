package com.tensquare.search.dao;

import com.tensquare.search.pojo.Problem;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProblemDao extends ElasticsearchRepository<Problem,String> {

}
