package com.pro.snap.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_users")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nickname;

    //MD5(MD5(pass明文+固定salt)+salt)
    private String password;

    private String salt;

    private String head;

    private Date dateRegister;

    private Date dateLastLogin;

    private Integer countLogin;

}
