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
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import team.goodtaste.backend.common.R;
import team.goodtaste.backend.entity.Seek;
import team.goodtaste.backend.service.SeekService;

@Slf4j
@RestController
@RequestMapping("/seek")
public class SeekController {

    @Autowired
    private SeekService seekService;

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

        // 4、修改一些seek信息
        oldSeek.setUpdateTime(LocalDateTime.now());
        oldSeek.setTopic(seek.getTopic());
        oldSeek.setMaxPrice(seek.getMaxPrice());
        oldSeek.setPictureUrl(seek.getPictureUrl());
        oldSeek.setAbout(seek.getAbout());

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

        // 3、删除这条数据
        seekService.removeById((Serializable) sid);

        // 4、返回成功消息
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

}
