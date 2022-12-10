package team.goodtaste.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import team.goodtaste.backend.entity.Detail;
import team.goodtaste.backend.mapper.DetailMaper;
import team.goodtaste.backend.service.DetailService;

@Service
public class DetailServiceImpl extends ServiceImpl<DetailMaper, Detail> implements DetailService {

}
