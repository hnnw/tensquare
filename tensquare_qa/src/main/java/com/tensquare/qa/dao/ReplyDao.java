package com.tensquare.qa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.tensquare.qa.pojo.Reply;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ReplyDao extends JpaRepository<Reply,String>,JpaSpecificationExecutor<Reply>{

    @Modifying
    @Query(value = "UPDATE tb_reply SET VERSION=VERSION+1 WHERE id = ?",nativeQuery = true)
    public void updateReplyVersion(String id);

    public List<Reply> findByProblemid(String id);


}
