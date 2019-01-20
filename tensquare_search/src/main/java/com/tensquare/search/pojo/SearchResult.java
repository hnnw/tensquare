package com.tensquare.search.pojo;

import entity.PageResult;
import lombok.Data;

import java.util.List;
@Data
public class SearchResult extends PageResult<Problem> {
    private List<String> labelid;

    private List<String> solve;

    public SearchResult() {
    }

    public SearchResult(Long total, List rows, List<String> labelid, List<String> solve) {
        super(total, rows);
        this.labelid = labelid;
        this.solve = solve;
    }

    public SearchResult(List<String> labelid, List<String> solve) {
        this.labelid = labelid;
        this.solve = solve;
    }
}
