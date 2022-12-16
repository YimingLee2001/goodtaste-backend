package team.goodtaste.backend.controller;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import team.goodtaste.backend.common.R;
import team.goodtaste.backend.entity.Detail;
import team.goodtaste.backend.entity.Reply;
import team.goodtaste.backend.entity.Seek;
import team.goodtaste.backend.entity.Statis;
import team.goodtaste.backend.service.DetailService;
import team.goodtaste.backend.service.ReplyService;
import team.goodtaste.backend.service.SeekService;
import team.goodtaste.backend.service.StatisService;

@Slf4j
@RestController
@RequestMapping("/seek")
public class SeekController {

    @Autowired
    private SeekService seekService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private DetailService detailService;

    @Autowired
    private StatisService statisticsService;

    /**
     * 新增寻味道信息
     * 
     * @param request
     * @param seek
     * @return
     */
    @PostMapping("/add")
    public R<String> add(HttpServletRequest request, @RequestBody Seek seek) {

        // 1、能通过filter，就一定是登录过了
        Long uid = (Long) request.getSession().getAttribute("uid");

        // 2、将用户uid填入seek
        seek.setUid(uid);

        // 3、对seek初始化
        seek.setCreateTime(LocalDateTime.now());
        seek.setUpdateTime(LocalDateTime.now());
        seek.setDeadline(LocalDateTime.now().plusDays(3));
        seek.setState(1); // 待响应

        // 4、将seek则写入数据库
        seekService.save(seek);

        // 5、返回成功消息
        return R.success("新增成功");
    }

    /**
     * 按id修改寻味道信息
     * 
     * @param request
     * @param seek
     * @return
     */
    @PostMapping("updatebyid")
    public R<String> updateById(HttpServletRequest request, @RequestBody Seek seek) {

        // 1、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 2、检查修改的是否是自己的发布
        Long seekUid = seek.getUid();
        if (!requestUid.equals(seekUid)) {
            return R.error("只能修改自己的发布");
        }

        // 3、拿到原来的seek
        Seek oldSeek = seekService.getById((Serializable) seek.getSid());

        if (oldSeek.getState() != 1) {
            return R.error("该请求已有响应，不能修改");
        }

        // 4、修改一些seek信息
        oldSeek.setUpdateTime(LocalDateTime.now());
        oldSeek.setTopic(seek.getTopic());
        oldSeek.setMaxPrice(seek.getMaxPrice());
        oldSeek.setPictureUrl(seek.getPictureUrl());
        oldSeek.setAbout(seek.getAbout());
        oldSeek.setTasteType(seek.getTasteType());

        // 5、对应则修改数据库
        seekService.updateById(oldSeek);

        // 6、返回成功消息
        return R.success("修改成功");
    }

    /**
     * 按id获取寻味道信息
     * 
     * @param sid
     * @return
     */
    @GetMapping("/getbyid")
    public R<Seek> getByID(Long sid) {
        log.info("sid = {}", sid);

        // 1、单独查询这一个
        Serializable seriSid = (Serializable) sid;
        Seek seek = seekService.getById(seriSid);

        // 2、不存在，返回错误
        if (seek == null) {
            return R.error("信息不存在");
        }

        // 3、返回寻味道信息
        return R.success(seek);
    }

    /**
     * 按topic分页查询寻味道信息
     * 
     * @param page
     * @param pageSize
     * @param topic
     * @return
     */
    @GetMapping("/pagebytopic")
    public R<Page<Seek>> pageByTopic(Integer page, Integer pageSize, String topic) {
        log.info("page = {}, pageSize = {}, topic = {}", page, pageSize, topic);

        // 1、构造分页构造器
        Page<Seek> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Seek> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(topic), Seek::getTopic, topic);

        // 4、排序条件
        queryWrapper.orderByDesc(Seek::getUpdateTime);

        // 5、执行查询
        seekService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 按用户id分页查询寻味道信息
     * 
     * @param page
     * @param pageSize
     * @param uid
     * @return
     */
    @GetMapping("/pagebyuser")
    public R<Page<Seek>> pageByUser(Integer page, Integer pageSize, Long uid) {
        log.info("page = {}, pageSize = {}, uid = {}", page, pageSize, uid);

        // 1、构造分页构造器
        Page<Seek> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Seek> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.eq(Seek::getUid, uid);

        // 4、排序条件
        queryWrapper.orderByDesc(Seek::getUpdateTime);

        // 5、执行查询
        seekService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 按id删除寻味道
     * 
     * @param request
     * @param seek
     * @return
     */
    @DeleteMapping("/removebyid")
    public R<String> removeById(HttpServletRequest request, @RequestBody Seek seek) {
        // 1、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 2、检查修改的是否是自己的发布
        Long seekUid = seek.getUid();
        if (!requestUid.equals(seekUid)) {
            return R.error("只能删除自己的发布");
        }

        // 3、拿到寻味道的sid
        Long sid = seek.getSid();
        Seek oldSeek = seekService.getById((Serializable) sid);

        if (oldSeek.getState() == 2) {
            return R.error("该请求已经被删除");
        } else if (oldSeek.getState() == 0) {
            return R.error("该请求已有响应，不可删除");
        } else if (oldSeek.getState() == 3) {
            return R.error("该请求已过期");
        }

        // 4、状态上删除这条数据即可
        // seekService.removeById((Serializable) sid);
        oldSeek.setState(2); // 已取消
        seekService.updateById(oldSeek);

        // 5、返回成功消息
        return R.success("删除成功");
    }

    /**
     * 按城市分页查询寻味道信息
     * 
     * @param page
     * @param pageSize
     * @param city
     * @return
     */
    @GetMapping("/pagebycity")
    public R<Page<Seek>> pageByCity(Integer page, Integer pageSize, String city) {
        // 1、构造分页构造器
        Page<Seek> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Seek> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.eq(Seek::getCity, city);

        // 4、排序条件
        queryWrapper.orderByDesc(Seek::getUpdateTime);

        // 5、执行查询
        seekService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 按id确认请品鉴信息
     * 
     * @param request
     * @param reply
     * @return
     */
    @PostMapping("confirmbyid")
    public R<String> confirmById(HttpServletRequest request, @RequestBody Reply reply) {

        // 1、先拿出两个实体
        Long sid = reply.getSid();
        Long rid = reply.getRid();
        Seek oldSeek = seekService.getById((Serializable) sid);
        Reply oldReply = replyService.getById((Serializable) rid);

        // 2、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 3、检查修改的是否是自己的发布
        Long seekUid = oldSeek.getUid();
        if (!requestUid.equals(seekUid)) {
            return R.error("只能确认自己的发布");
        }

        // 4、判断seek状态是否可以修改
        if (oldSeek.getState() != 1) {
            return R.error("该请求已结束");
        }

        // 5、修改reply状态
        if (oldReply.getState() != 0) {
            return R.error("该响应已结束");
        }
        // 这里接受或拒绝
        oldReply.setState(reply.getState());

        // 5、对应修改reply数据库
        replyService.updateById(oldReply);

        // 6、新增detail数据库以及在stat数据库记录
        if (reply.getState() == 1) {
            Detail detail = new Detail();

            // seek
            detail.setTopic(oldSeek.getTopic());
            detail.setSid(sid);
            detail.setSuid(oldSeek.getUid());
            detail.setSfee(3);
            detail.setCity(oldSeek.getCity());

            // reply
            detail.setRid(rid);
            detail.setRuid(oldReply.getUid());
            detail.setRfee(1);

            detail.setDealTime(LocalDateTime.now());
            detailService.save(detail);

            // stat
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            String yearMonth = detail.getDealTime().format(formatter);
            String city = oldSeek.getCity();
            LambdaQueryWrapper<Statis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Statis::getCity, city);
            queryWrapper.eq(Statis::getYearmonth, yearMonth);
            Statis oldStatistics = statisticsService.getOne(queryWrapper);
            if (oldStatistics == null) {
                Statis statistics = new Statis();
                statistics.setCity(city);
                statistics.setProvince(oldSeek.getProvince());
                statistics.setYearmonth(yearMonth);
                statistics.setTotalCount(1);
                statistics.setTotalFee(4);
                statisticsService.save(statistics);
            } else {
                oldStatistics.setTotalCount(oldStatistics.getTotalCount() + 1);
                oldStatistics.setTotalFee(oldStatistics.getTotalFee() + 4);
                statisticsService.updateById(oldStatistics);
            }

        }

        return R.success("确认成功");
    }
}
