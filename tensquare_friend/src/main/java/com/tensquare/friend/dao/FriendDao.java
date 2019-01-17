package com.tensquare.friend.dao;

import com.tensquare.friend.pojo.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FriendDao extends JpaRepository<Friend,String>,JpaSpecificationExecutor<Friend> {

    @Query(value ="SELECT COUNT(*) FROM tb_friend WHERE userid=? AND friendid=?",nativeQuery = true)
    int selectCount(String userid, String friendid);

    @Modifying
    @Query(value = "UPDATE tb_friend SET islike=?3 WHERE userid=?1 AND friendid=?2 ",nativeQuery = true)
    void updateLike(String userid, String friendid, String s);
    @Modifying
    @Query(value = "DELETE FROM tb_friend WHERE userid=? AND friendid=?",nativeQuery = true)
    void delete(String userid, String friendid);

}
