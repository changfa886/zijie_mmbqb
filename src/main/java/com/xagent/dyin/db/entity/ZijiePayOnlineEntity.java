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
@TableName("zijie_pay_online")
public class ZijiePayOnlineEntity implements Serializable {

private static final long serialVersionUID=1L;

    private Long id;

    private Long created;

    private Long updated;

    private Long uid;

    private Integer status;

    private String outTradeNo;

    private Long goodsId;

    private Integer totalFee;

    private String remark;


}
