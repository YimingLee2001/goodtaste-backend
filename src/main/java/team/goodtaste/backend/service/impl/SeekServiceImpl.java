package team.goodtaste.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import team.goodtaste.backend.entity.Seek;
import team.goodtaste.backend.mapper.SeekMapper;
import team.goodtaste.backend.service.SeekService;

@Service
public class SeekServiceImpl extends ServiceImpl<SeekMapper, Seek> implements SeekService {

}
