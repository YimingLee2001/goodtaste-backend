package team.goodtaste.backend.controller;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
     * 修改寻味道信息
     * 
     * @param request
     * @param seek
     * @return
     */
    @PostMapping("update")
    public R<String> update(HttpServletRequest request, @RequestBody Seek seek) {

        // 1、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 2、检查修改的是否是自己的发布
        Long seekUid = seek.getUid();
        if (!requestUid.equals(seekUid)) {
            return R.error("只能修改自己的发布");
        }

        // 3、拿到原来的seek
        Seek oldSeek = seekService.getById((Serializable) seekUid);

        // 4、修改一些seek信息
        oldSeek.setUpdateTime(LocalDateTime.now());
        oldSeek.setTopic(seek.getTopic());
        oldSeek.setMaxPrice(seek.getMaxPrice());

        // 5、对应则修改数据库
        seekService.updateById(oldSeek);

        // 6、返回成功消息
        return R.success("修改成功");
    }

    @GetMapping("/get")
    public R<Seek> get(Long sid) {
        log.info("sid = {}", sid);

        // 1、单独查询这一个
        Serializable seriSid = (Serializable) sid;

        Seek seek = seekService.getById(seriSid);

        return R.success(seek);
    }

    /**
     * 寻味道信息分页查询
     * 
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<Seek>> page(Integer page, Integer pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        Page<Seek> pageInfo = new Page<>(page, pageSize);

        LambdaQueryWrapper<Seek> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotEmpty(name), Seek::getTopic, name);

        queryWrapper.orderByDesc(Seek::getUpdateTime);

        seekService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

}
