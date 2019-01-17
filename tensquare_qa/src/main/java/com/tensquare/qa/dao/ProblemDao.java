package com.tensquare.qa.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.qa.pojo.Problem;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ProblemDao extends JpaRepository<Problem,String>,JpaSpecificationExecutor<Problem>{


    @Query(value = "SELECT * FROM tb_problem,tb_pl WHERE id =problemid AND labelid=? ORDER BY replytime DESC",nativeQuery = true)
    Page<Problem> findNewProblem(String lablid,Pageable pageable);

    @Query(value = "SELECT * FROM tb_problem ORDER BY replytime DESC",nativeQuery = true)
    Page<Problem> findNewProblem(Pageable pageable);

    @Query(value = "SELECT * FROM tb_problem,tb_pl WHERE id =problemid AND labelid=? ORDER BY reply DESC",nativeQuery = true)
    Page<Problem> findHotProblem(String lablid,Pageable pageable);

    @Query(value = "SELECT * FROM tb_problem ORDER BY reply DESC",nativeQuery = true)
    Page<Problem> findHotProblem(Pageable pageable);


    @Query(value = "SELECT * FROM tb_problem,tb_pl WHERE id =problemid AND labelid=? AND reply=0 ORDER BY createtime DESC",nativeQuery = true)
    Page<Problem> findWaitProblem(String lablid,Pageable pageable);

    @Query(value = "SELECT * FROM tb_problem WHERE reply=0 ORDER BY createtime DESC",nativeQuery = true)
    Page<Problem> findWaitProblem(Pageable pageable);

}
