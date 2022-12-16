package team.goodtaste.backend.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data
public class Statistics implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long sid;

    private String yearMonth;

    private String province;

    private String city;

    private Integer totalCount;

    private Integer totalFee;
}
