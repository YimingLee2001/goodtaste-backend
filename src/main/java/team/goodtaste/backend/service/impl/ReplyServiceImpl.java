package team.goodtaste.backend.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import team.goodtaste.backend.entity.Reply;
import team.goodtaste.backend.mapper.ReplyMapper;
import team.goodtaste.backend.service.ReplyService;

@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements ReplyService {

}
