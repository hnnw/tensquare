package com.tensquare.search.dao;

import com.tensquare.search.pojo.Problem;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
@SpringBootTest
@RunWith(SpringRunner.class)
public class ProblemDaoTest{

    @Autowired
    ProblemDao problemDao;

    @Autowired
    ElasticsearchTemplate template;

    @Test
    public void add(){

        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();//构建查询条件
        builder.withQuery(QueryBuilders.multiMatchQuery("php","title","content"));//设置查询字段

        //SearchQuery query1 = (SearchQuery) QueryBuilders.multiMatchQuery("php","title","content");
        builder.addAggregation(AggregationBuilders.terms("label").field("labelid"));

        AggregatedPage<Problem> search = (AggregatedPage<Problem>) problemDao.search(builder.build());
        StringTerms agg = (StringTerms) search.getAggregation("label");
        List<StringTerms.Bucket> buckets = agg.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            System.out.println(bucket.getKeyAsString());
        }
        //System.out.println(search.getTotalPages());
        //System.out.println(search.getTotalElements());
    }
}
