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
@TableName("zijie_goods")
public class ZijieGoodsEntity implements Serializable {

private static final long serialVersionUID=1L;

    private Long id;

    private Integer status;

    private Long catagoryid;

    private Integer isort;

    private String title;

    private Integer price;

    private Integer buytimes;

    private String headimg;

    private String imgs;

    private String remark;


}
