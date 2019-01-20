package com.tensquare.search.service;

import com.tensquare.search.dao.ArticleDao;
import com.tensquare.search.dao.ProblemDao;
import com.tensquare.search.pojo.Article;
import com.tensquare.search.pojo.Problem;
import com.tensquare.search.pojo.SearchRequest;
import com.tensquare.search.pojo.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    ArticleDao articleDao;
    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    ProblemDao problemDao;

    public Page<Article> serach(String key,Integer page,Integer size){

        Pageable pageable = PageRequest.of(page-1,size);
        return articleDao.findByTitleOrContentLike(key,key,pageable);
    }

    public SearchResult searchResult(SearchRequest request){
        Integer page = request.getPage()-1;//当前页
        Integer size = request.getSize();//条数
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();//构建基础查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();//bool查询,多条件查询
        boolQuery.must(QueryBuilders.multiMatchQuery(request.getKey(),"title","content"));//添加查询条件和查询字段
        Map<String, String> filter = request.getFilter();//遍历过滤条件
       for (Map.Entry<String, String> entry : filter.entrySet()) {
            String value = entry.getValue();
            String key = entry.getKey();
            boolQuery.filter(QueryBuilders.termQuery(key, value));//添加过滤条件
        }
        builder.withQuery(boolQuery);
        //builder.addAggregation(AggregationBuilders.terms("label").field("labelid"));
        builder.addAggregation(AggregationBuilders.terms("solves").field("solve"));//添加聚合条件
        builder.withPageable(PageRequest.of(page,size));//分页


        AggregatedPage<Problem> search1 = (AggregatedPage<Problem>) problemDao.search(builder.build());
        Aggregations aggregations = search1.getAggregations();
        //List<String> categoryList=getLabel(aggregations.get("label"));
        List<String> solveList=getSolves(aggregations.get("solves"));

        long totalPages = search1.getTotalPages();
        List<Problem> content = search1.getContent();

        return new SearchResult(totalPages,content,null,solveList);
        //return new SearchResult(totalPages,search.getContent(),null,solveList);
    }

    /**
     * 转换成中文
     * @param label
     * @return
     */
    private List<String> getSolves(StringTerms label) {
        List<String> list = new ArrayList<>();
        List<StringTerms.Bucket> buckets = label.getBuckets();
        for (StringTerms.Bucket bucket : buckets) {
            if (bucket.getKeyAsString().equals("0")){
            list.add("未解决");
            }else {
                list.add("已解决");
            }
        }
        return list;
    }


}
