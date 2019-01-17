package com.tensquare.base.controller;

import com.tensquare.base.pojo.Label;
import com.tensquare.base.service.LabelService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("label")
@CrossOrigin
public class LabelContRoller {

    @Autowired
    LabelService labelService;

    /**
     * 查询全部
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        return new Result(true,StatusCode.OK,"查询成功",
                labelService.findAll() );
    }
    /**
     * 根据ID查询
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result findById(@PathVariable(value = "id") String id){
        return new Result(true,StatusCode.OK,"查询成 功",labelService.findById(id));
    }

    /**
     * 添加标签
     * @param label
     * @return
     */
    @PostMapping
    public Result add( @RequestBody Label label){
        labelService.save(label);
        return new Result(true,StatusCode.OK,"增加成功");
    }

    /**
     * 修改标签
     * @param label
     * @param id
     * @return
     */
    @PutMapping("{id}")
    public Result update( @RequestBody Label label,@PathVariable("id") String
            id){
        label.setId(id);
        labelService.update(label);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    @DeleteMapping("{id}")
    public Result delete(@PathVariable("id") String id){
    labelService.delete(id);
    return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 查询推荐标签列表
     * @return
     */
    @GetMapping("toplist")
    public Result findByRecommend(){
        List<Label> byRecommend = labelService.findByRecommend();
        return new Result(true,StatusCode.OK,"查询成功",byRecommend);
    }


    /**
     * 按照条件查询
     *
     * @param label
     * @return
     */
    @PostMapping(value = "search")
    public Result search(@RequestBody Label label){
        List<Label> byRecommend = labelService.search(label);
        return new Result(true,StatusCode.OK,"搜索成功",byRecommend);
    }

    /**
     * 按照标签条件，分页查询
     * @param label
     * @return
     */
    @PostMapping(value = "search/{page}/{size}")
    public Result searchPage(@RequestBody Label label,
                             @PathVariable("page") Integer page,
                             @PathVariable("size") Integer size){
        Page<Label> page1= labelService.searchPage(label,page,size);
        return new Result(true,StatusCode.OK,"搜索成功",new PageResult<Label>(page1.getTotalElements(),page1.getContent()));
    }

}



