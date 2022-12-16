package team.goodtaste.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

@Data
public class Detail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long did;

    private String topic;

    private Long sid;

    private Long rid;

    private Long suid;

    private Long ruid;

    private LocalDateTime dealTime;

    private Integer sfee;

    private Integer rfee;

    private String city;
}
