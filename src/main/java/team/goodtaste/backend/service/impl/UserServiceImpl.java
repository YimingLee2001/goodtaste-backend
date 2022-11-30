package team.goodtaste.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import team.goodtaste.backend.entity.User;
import team.goodtaste.backend.mapper.UserMapper;
import team.goodtaste.backend.service.UserService;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
