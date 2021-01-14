package com.CH.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author CH
 * @since 2021-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TmUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 角色Id
     */
    private Integer roleId;


}
