package team.goodtaste.backend.controller;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import team.goodtaste.backend.common.R;
import team.goodtaste.backend.entity.Reply;
import team.goodtaste.backend.entity.Seek;
import team.goodtaste.backend.service.ReplyService;
import team.goodtaste.backend.service.SeekService;

@Slf4j
@RestController
@RequestMapping("/reply")
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private SeekService seekService;

    /**
     * 按用户id分页查询请品鉴信息
     * 
     * @param page
     * @param pageSize
     * @param uid
     * @return
     */
    @GetMapping("/pagebyuser")
    public R<Page<Reply>> pageByUser(Integer page, Integer pageSize, Long uid) {
        log.info("page = {}, pageSize = {}, uid = {}", page, pageSize, uid);

        // 1、构造分页构造器
        Page<Reply> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.eq(Reply::getUid, uid);

        // 4、排序条件
        queryWrapper.orderByDesc(Reply::getUpdateTime);

        // 5、执行查询
        replyService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 新增请品鉴信息
     * 
     * @param request
     * @param reply
     * @return
     */
    @PostMapping("/add")
    public R<String> add(HttpServletRequest request, @RequestBody Reply reply) {

        // 1、能通过filter，就一定是登录过了
        Long uid = (Long) request.getSession().getAttribute("uid");

        // 2、将用户uid填入reply
        reply.setUid(uid);

        // 3、对reply初始化
        reply.setCreateTime(LocalDateTime.now());
        reply.setUpdateTime(LocalDateTime.now());
        reply.setState(0); // 待接受

        // 4、检验seek信息
        Long sid = reply.getSid();
        Seek seek = seekService.getById((Serializable) sid);
        if (seek.getState() != 1) {
            return R.error("该请求不可响应");
        }

        // 5、将reply写入数据库
        replyService.save(reply);

        return R.success("新增成功");
    }

    /**
     * 按id修改请品鉴信息
     * 
     * @param request
     * @param reply
     * @return
     */
    @PostMapping("/updatebyid")
    public R<String> updateById(HttpServletRequest request, @RequestBody Reply reply) {

        // 1、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 2、检查修改的是否是自己的发布
        Long replyUid = reply.getUid();
        if (!requestUid.equals(replyUid)) {
            return R.error("只能修改自己的发布");
        }

        // 3、拿到原来的reply
        Reply oldReply = replyService.getById((Serializable) reply.getRid());

        // 4、检查是否可以修改
        if (oldReply.getState() > 0) {
            return R.error("该响应已不可修改");
        }

        // 5、修改一些reply信息
        oldReply.setUpdateTime(LocalDateTime.now());
        oldReply.setAbout(reply.getAbout());

        // 6、对应则修改数据库
        replyService.updateById(oldReply);

        return R.success("修改成功");
    }

    /**
     * 按id删除请品鉴
     * 
     * @param request
     * @param reply
     * @return
     */
    @DeleteMapping("/removebyid")
    public R<String> removeById(HttpServletRequest request, @RequestBody Reply reply) {
        // 1、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 2、检查修改的是否是自己的发布
        Long replyUid = reply.getUid();
        if (!requestUid.equals(replyUid)) {
            return R.error("只能删除自己的响应");
        }

        // 3、拿到寻味道的sid
        Long rid = reply.getRid();
        Reply oldReply = replyService.getById((Serializable) rid);

        // 4、判断状态
        if (oldReply.getState() > 0) {
            return R.error("该响应已被不可删除");
        }

        // 5、逻辑上删除reply数据
        // replyService.removeById((Serializable) rid);
        oldReply.setState(3); // 已取消
        replyService.updateById(oldReply);

        // 5、返回成功消息
        return R.success("删除成功");
    }

    /**
     * 按寻味道id分页查询请品鉴信息
     * 
     * @param page
     * @param pageSize
     * @param sid
     * @return
     */
    @GetMapping("/pagebyseek")
    public R<Page<Reply>> pageBySeek(Integer page, Integer pageSize, Long sid) {
        log.info("page = {}, pageSize = {}, sid = {}", page, pageSize, sid);

        // 1、构造分页构造器
        Page<Reply> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Reply> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.eq(Reply::getSid, sid);

        // 4、排序条件
        queryWrapper.orderByDesc(Reply::getUpdateTime);

        // 5、执行查询
        replyService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }
}
