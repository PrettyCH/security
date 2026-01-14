package com.CH.security.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author CH
 * @since 2021-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TmRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * ID
     */
//    @TableId(value = "id", type = IdType.AUTO)
//    private Long id;
    /**
     * 角色编号
     */
    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 父角色ID
     */
    private Long parentId;

    /**
     * 状态（0-编辑中，1-正常，2-已删除）
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
