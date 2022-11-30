package team.goodtaste.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;

/**
 * 用户实体
 */
@Data
public class User implements Serializable {
    // 序列化版本号，防止版本出错
    private static final long serialVersionUID = 1L;

    private Long uid;

    private String nickname;

    private String password;

    private Integer accountType;

    private Integer idType;

    private String idNumber;

    private String phone;

    private Integer userLevel;

    private String about;

    private Integer province;

    private Integer city;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String avatarUrl;

    @TableField(exist = false)
    private String token;
}
