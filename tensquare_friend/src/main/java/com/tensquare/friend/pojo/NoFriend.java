package com.tensquare.friend.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tb_nofriend")
public class NoFriend implements Serializable {

    @Id
    private String userid;

    private String friendid;

}
