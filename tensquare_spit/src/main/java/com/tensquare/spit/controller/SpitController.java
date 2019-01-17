package com.tensquare.spit.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.tensquare.spit.pojo.Spit;
import com.tensquare.spit.service.SpitService;
import entity.Result;
import entity.StatusCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spit")
@CrossOrigin
public class SpitController {

    @Autowired
    SpitService spitService;

    @Autowired
    StringRedisTemplate template;

    @GetMapping
    public Result findAll(){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findAll());
    }
    @GetMapping("{spitId}")
    public Result findByid(@PathVariable("spitId") String id){
        return new Result(true, StatusCode.OK,"查询成功",spitService.findById(id));
    }

    @PutMapping("{spitId}")
    public Result UpdateByid(@PathVariable("spitId") String id, @RequestBody Spit spit){
        spitService.update(id,spit);
        return new Result(true, StatusCode.OK,"修改成功");
    }
    @DeleteMapping("{spitId}")
    public Result updateByid(@PathVariable("spitId") String id){
        spitService.delete(id);
        return new Result(true, StatusCode.OK,"删除成功");
    }
    @PostMapping
    public Result save(@RequestBody Spit spit){
        spitService.save(spit);
        return new Result(true, StatusCode.OK,"添加成功");
    }
    @PutMapping("thumbup/{spitId}")
    public Result thumbup(@PathVariable("spitId") String id){
        if ("1".equals(template.opsForValue().get("111"))){
            spitService.thumbup(id,-1);
            template.delete("111");
            return new Result(true, StatusCode.OK,"取消点赞");
        }
        spitService.thumbup(id,1);
        template.opsForValue().set("111","1");
        return new Result(true, StatusCode.OK,"点赞成功");
    }



}
