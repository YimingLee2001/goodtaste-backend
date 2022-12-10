package team.goodtaste.backend.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;

/**
 * 用户实体
 */
@Data
public class User implements Serializable {
    // 序列化版本号，防止版本出错
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long uid;

    private String nickname;

    private String password;

    private Integer accountType;

    private String username;

    private String idType;

    private String idNumber;

    private String phone;

    private Integer userLevel;

    private String about;

    private String province;

    private String city;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String avatarUrl;

    @TableField(exist = false)
    private String token;
}
