package team.goodtaste.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data
public class Seek implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long sid;

    private Long uid;

    private Integer tasteType;

    private String topic;

    private String about;

    private Integer maxPrice;

    private LocalDateTime deadline;

    private String pictureUrl;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer state;
}
