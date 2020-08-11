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
@TableName("zijie_admin")
public class ZijieAdminEntity implements Serializable {

private static final long serialVersionUID=1L;

    private Long id;

    private String admname;

    private String loginpw;

    private String role;

    private String nickname;


}
