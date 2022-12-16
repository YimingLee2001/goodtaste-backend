package team.goodtaste.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import team.goodtaste.backend.entity.Statis;
import team.goodtaste.backend.mapper.StatisMapper;
import team.goodtaste.backend.service.StatisService;

@Service
public class StatisServiceImpl extends ServiceImpl<StatisMapper, Statis> implements StatisService {

}
