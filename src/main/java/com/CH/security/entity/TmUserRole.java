package com.CH.security.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author CH
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TmUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 角色Id
     */
    private Integer roleId;


}
