package com.CH.security.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author CH
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TmUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户账号
     */
    private String userId;

    /**
     * 用户密码
     */
    private String userPwd;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户邮箱
     */
    private String userEmail;

    /**
     * 用户手机号码
     */
    private String userMobile;

    /**
     * 帐号描述
     */
    private String userDesc;

    /**
     * 状态（0-正常，1-已注销）
     */
    private Integer status;

    /**
     * 创建人编号
     */
    private Long creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后修改人编号
     */
    private Long updator;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;


}
