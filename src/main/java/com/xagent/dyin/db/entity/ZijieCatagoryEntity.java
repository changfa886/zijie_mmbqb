package com.xagent.dyin.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;

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
@TableName("zijie_catagory")
public class ZijieCatagoryEntity implements Serializable {

private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer status;

    private Integer isort;


}
