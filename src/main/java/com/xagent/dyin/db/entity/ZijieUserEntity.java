package com.xagent.dyin.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author jonty
 * @since 2020-05-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("zijie_user")
public class ZijieUserEntity implements Serializable {

private static final long serialVersionUID=1L;

    private Long id;

    private Long created;

    private Long updated;

    private Integer status;

    private String openid;

    private String nickname;

    private String avatar;


}
