package com.tensquare.qa.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.tensquare.qa.dao.ReplyDao;
import com.tensquare.qa.pojo.Reply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import com.tensquare.qa.dao.ProblemDao;
import com.tensquare.qa.pojo.Problem;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
@Transactional
public class ProblemService {

	@Autowired
	private ProblemDao problemDao;
	
	@Autowired
	private IdWorker idWorker;

	@Autowired
	private ReplyDao replyDao;
	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	private final String S="tensquare_problem";

	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Problem> findAll() {
		return problemDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Problem> findSearch(Map whereMap, int page, int size) {
		Specification<Problem> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return problemDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Problem> findSearch(Map whereMap) {
		Specification<Problem> specification = createSpecification(whereMap);
		return problemDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Problem findById(String id) {

		Problem problem = problemDao.findById(id).get();
		List<Reply> byProblemid = replyDao.findByProblemid(id);
		//把回答集合传入问题
		problem.setList(byProblemid);
		return problem;

	}

	/**
	 * 增加
	 * @param problem
	 */
	public void add(Problem problem) {
		problem.setId( idWorker.nextId()+"" );
		problem.setSolve("0");
		problemDao.save(problem);
	}

	/**
	 * 修改
	 * @param problem
	 */
	public void update(Problem problem) {
		problemDao.save(problem);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		problemDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Problem> createSpecification(Map searchMap) {

		return new Specification<Problem>() {

			@Override
			public Predicate toPredicate(Root<Problem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 标题
                if (searchMap.get("title")!=null && !"".equals(searchMap.get("title"))) {
                	predicateList.add(cb.like(root.get("title").as(String.class), "%"+(String)searchMap.get("title")+"%"));
                }
                // 内容
                if (searchMap.get("content")!=null && !"".equals(searchMap.get("content"))) {
                	predicateList.add(cb.like(root.get("content").as(String.class), "%"+(String)searchMap.get("content")+"%"));
                }
                // 用户ID
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 昵称
                if (searchMap.get("nickname")!=null && !"".equals(searchMap.get("nickname"))) {
                	predicateList.add(cb.like(root.get("nickname").as(String.class), "%"+(String)searchMap.get("nickname")+"%"));
                }
                // 是否解决
                if (searchMap.get("solve")!=null && !"".equals(searchMap.get("solve"))) {
                	predicateList.add(cb.like(root.get("solve").as(String.class), "%"+(String)searchMap.get("solve")+"%"));
                }
                // 回复人昵称
                if (searchMap.get("replyname")!=null && !"".equals(searchMap.get("replyname"))) {
                	predicateList.add(cb.like(root.get("replyname").as(String.class), "%"+(String)searchMap.get("replyname")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

	/**
	 * 查询最新回答列表
	 * @param id
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Problem> findNewProblem(String id,Integer page,Integer size){
		PageRequest of = PageRequest.of(page-1, size);
		if ("0".equals(id)){
			return problemDao.findNewProblem(of);
		}
		return problemDao.findNewProblem(id,of);
	}

    /**
     * 查询热门回答列表
     * @param id
     * @param page
     * @param size
     * @return
     */
	public Page<Problem> findHotProblem(String id,Integer page,Integer size){
		PageRequest of = PageRequest.of(page-1, size);
		if ("0".equals(id)){

			return problemDao.findHotProblem(of);
		}
		return problemDao.findHotProblem(id,of);
	}

    /**
     * 查询等待回答列表
     * @param id
     * @param page
     * @param size
     * @return
     */
    public Page<Problem> findWaitProblem(String id,Integer page,Integer size){
        PageRequest of = PageRequest.of(page-1, size);
        Map<String,Object> map = new HashMap<>();
            if ("0".equals(id)) {
                Page<Problem> waitProblem = problemDao.findWaitProblem(of);
                map.put("1",waitProblem);
                redisTemplate.opsForValue().set(S + "WaitProblem"+id, map, 5, TimeUnit.MINUTES);
                return waitProblem;
            }
            Page<Problem> waitProblem = problemDao.findWaitProblem(id, of);
            map.put("1",waitProblem);
            redisTemplate.opsForValue().set(S + "WaitProblem"+id, map, 5, TimeUnit.MINUTES);
            return problemDao.findWaitProblem(id, of);
    }

	/**
	 * 查询用户提问的问题
	 * @param id
	 * @return
	 */
	public List<Problem> findByUserid(String id){
		return problemDao.findByUserid(id);
	}


}
