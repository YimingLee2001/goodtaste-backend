package team.goodtaste.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import team.goodtaste.backend.entity.Statistics;
import team.goodtaste.backend.mapper.StatisticsMapper;
import team.goodtaste.backend.service.StatisticsService;

@Service
public class StatisticsServiceImpl extends ServiceImpl<StatisticsMapper, Statistics> implements StatisticsService {

}
