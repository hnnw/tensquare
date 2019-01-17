package com.tensquare.spit.service;

import com.tensquare.spit.dao.SpitDao;
import com.tensquare.spit.pojo.Spit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class SpitService {

    @Autowired
    SpitDao spitDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    IdWorker idWorker;

    /**
     * 查询全部列表
     * @return
     */
    public List<Spit> findAll(){
        return  spitDao.findAll();
    }

    /**
     * 根据ID查询
     * @param id
     * @return
     */
    public Spit findById(String id){
        Spit spit = spitDao.findById(id).get();
        if (StringUtils.isBlank(spit.getParentid())){
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(id));
            Update update = new Update();
            update.inc("visits",1);
            mongoTemplate.updateFirst(query,update,"spit");
        }
        return spit ;
    }
    public Page<Spit> findByParentid(String id,Integer page,Integer size){

        Pageable pagea = PageRequest.of(page-1,size);
        return spitDao.findByParentid(id,pagea);
    }

    /**
     * 根据实体类
     * @param spit
     * @return
     */
    public List<Spit> findBySpit(Spit spit){
        Query query = new Query();
        if (!StringUtils.isBlank(spit.getNickname())){
            Pattern pattern = Pattern.compile("^.*" + spit.getNickname() + ".*$");
            query.addCriteria(Criteria.where("nickname").regex(pattern));
        }
        if (!StringUtils.isBlank(spit.getContent())){
            Pattern pattern = Pattern.compile("^.*" + spit.getContent() + ".*$");
            query.addCriteria(Criteria.where("content").regex(pattern));
        }
        return mongoTemplate.find(query, Spit.class);
    }

    /**
     * 添加吐槽
     * @param spit
     */
    public void save(Spit spit){
        spit.set_id(idWorker.nextId()+"");
        spit.setPublishtime(new Date());
        spit.setShare(0);//分享数
        spit.setThumbup(0);//点赞说
        spit.setComment(0);//回复数
        spit.setVisits(0);//浏览量
        spit.setState("1");//状态

        if (!StringUtils.isBlank(spit.getParentid())){
            //如果有父类ID则，父类吐糟回复数加1
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(spit.getParentid()));
            Update update = new Update();
            update.inc("comment",1);
            mongoTemplate.updateFirst(query,update,"spit");
        }
        spitDao.save(spit);
    }

    /**
     * 修改吐槽
     * @param spit
     */
    public void update(String id, Spit spit){
        spit.set_id(id);
        spitDao.save(spit);
    }

    /**
     * 根据ID删除
     * @param id
     */
    public void delete(String id){
        Spit spit = spitDao.findById(id).get();
        if (!StringUtils.isBlank(spit.getParentid())){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(spit.getParentid()));
        Update update = new Update();
        update.inc("comment",-1);
        mongoTemplate.updateFirst(query,update,"spit");
        }
        spitDao.deleteById(id);

    }

    /**
     * 用模板点赞
     * @param id
     */
    public void thumbup(String id,Integer i){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update=new Update();
        update.inc("thumbup",i);
        mongoTemplate.updateFirst(query,update,"spit");
    }
    /**
     * 用模板点赞
     * @param id
     */
    public void share(String id ){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update=new Update();
        update.inc("share",1);
        mongoTemplate.updateFirst(query,update,"spit");
    }
}
