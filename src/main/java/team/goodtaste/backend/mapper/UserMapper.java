package team.goodtaste.backend.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import team.goodtaste.backend.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
