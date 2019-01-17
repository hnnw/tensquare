package com.tensquare.search.controller;

import com.tensquare.search.pojo.Article;
import com.tensquare.search.service.SearchService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/search/{keywords}/{page}/{size}")
    public Result search(@PathVariable("keywords") String key,
                         @PathVariable("page") Integer page,
                         @PathVariable("size") Integer size){
        Page<Article> serach = searchService.serach(key, page, size);
        return new Result(true, StatusCode.OK, "查询成功",new PageResult<Article>(serach.getTotalElements(),serach.getContent()));
    }
}
