package team.goodtaste.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data
public class Reply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long rid;

    private Long sid;

    private Long uid;

    private String about;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer state; // 0：待接受；1：同意；2：拒绝；3：取消
}
