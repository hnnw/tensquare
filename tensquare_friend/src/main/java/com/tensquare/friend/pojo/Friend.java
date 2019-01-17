package com.tensquare.friend.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tb_friend")
public class Friend implements Serializable {

    @Id
    private String userid;

    private String friendid;

    private String islike;  //是否互相喜欢
}
