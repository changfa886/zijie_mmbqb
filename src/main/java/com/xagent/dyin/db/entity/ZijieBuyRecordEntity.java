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
 * @since 2020-05-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("zijie_buy_record")
public class ZijieBuyRecordEntity implements Serializable {

private static final long serialVersionUID=1L;

    private Long id;

    private Long created;

    private Long uid;

    private Integer amount;

    private Integer type;

    private Long goodsid;

    private Long catagoryid;

    private String remark;


}
