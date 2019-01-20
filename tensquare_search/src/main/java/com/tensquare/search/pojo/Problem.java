package com.tensquare.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName="tensquare",type="problem")
public class Problem {
    @Id
    private String id;//ID
    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;//标题
    @Field(analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String content;//内容

    private String solve;//是否已解决 1 解决  0 未解决

    private String labelid;//标签

}
