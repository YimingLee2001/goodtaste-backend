package team.goodtaste.backend.controller;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
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
import team.goodtaste.backend.entity.User;
import team.goodtaste.backend.service.UserService;
// import team.goodtaste.backend.utils.JwtUtils;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * 
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody User user) {

        // 1、将页面提交的密码password进行md5加密处理
        String password = user.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的用户名nickname查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickname, user.getNickname());
        User userRet = userService.getOne(queryWrapper);

        // 3、如果没有查询到则返回登录失败结果
        if (userRet == null) {
            return R.error("用户不存在");
        }

        // 4、密码对比，如果不一致则返回登录失败结果
        if (!userRet.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        // 5、登录成功，将用户uid存入Session并返回登录成功结果
        request.getSession().setAttribute("uid", userRet.getUid());

        // 5、登录成功，生成token并返回用户
        // String token = JwtUtils.generateToken(userRet.getNickname());
        // userRet.setToken(token);

        log.info("登录成功");
        return R.success(userRet);
    }

    /**
     * 用户退出
     * 
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理Session中保存的当前登录用户的id
        request.getSession().removeAttribute("user");

        log.info("退出成功");
        return R.success("退出成功");
    }

    /**
     * 用户注册
     * 
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/signup")
    public R<String> signup(HttpServletRequest request, @RequestBody User user) {
        log.info("新增用户，用户信息：{}", user.toString());

        // 1、设置初始密码123456，需要md5加密处理
        user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 2、设置创建和修改时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3、将用户信息录入数据库
        userService.save(user);

        return R.success("注册成功");
    }

    /**
     * 按昵称分页查询用户信息
     * 
     * @param page
     * @param pageSize
     * @param nickname
     * @return
     */
    @GetMapping("/pagebynickname")
    public R<Page<User>> pageByNickname(Integer page, Integer pageSize, String nickname) {
        log.info("page = {}, pageSize = {}, nickname = {}", page, pageSize, nickname);

        // 1、构造分页构造器
        Page<User> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(nickname), User::getNickname, nickname);

        // 4、排序条件
        queryWrapper.orderByDesc(User::getUpdateTime);

        // 5、执行查询
        userService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 按id获取用户信息
     * 
     * @param uid
     * @return
     */
    @GetMapping("/getbyid")
    public R<User> getById(Long uid) {
        log.info("uid = {}", uid);

        // 1、将uid转换成Serializable类型
        Serializable seriUid = (Serializable) uid;

        // 2、去按主键查询
        User userRet = userService.getById(seriUid);

        // 3、查不到则报错
        if (userRet == null) {
            return R.error("用户不存在");
        }

        // 4、返回用户信息
        return R.success(userRet);
    }

    /**
     * 按id修改个人信息
     * 
     * @param request
     * @param user
     * @return
     */
    @PostMapping("/updatebyid")
    public R<String> updateById(HttpServletRequest request, @RequestBody User user) {

        // 1、能通过filter，就一定是登录过了
        Long requestUid = (Long) request.getSession().getAttribute("uid");

        // 2、检查修改的是否是自己的信息
        Long userUid = user.getUid();
        if (!requestUid.equals(userUid)) {
            return R.error("只能修改自己的信息");
        }

        // 3、拿到原来的user
        User oldUser = userService.getById((Serializable) userUid);

        // 4、修改一些user信息
        oldUser.setUpdateTime(LocalDateTime.now());
        oldUser.setPhone(user.getPhone());
        oldUser.setAbout(user.getAbout());

        // 5、特殊的，修改密码
        String password = user.getPassword();
        oldUser.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));

        // 6、修改成功
        return R.success("修改成功");
    }
}
