package team.goodtaste.backend.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import lombok.extern.slf4j.Slf4j;
import team.goodtaste.backend.common.R;
import team.goodtaste.backend.entity.Detail;
import team.goodtaste.backend.entity.Statistics;
import team.goodtaste.backend.service.DetailService;
import team.goodtaste.backend.service.StatisticsService;

@Slf4j
@RestController
@RequestMapping("/stat")
public class StatisticsController {

    @Autowired
    private DetailService detailService;

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 按地区时段分页查询detail
     * 
     * @param page
     * @param pageSize
     * @param startTime
     * @param endTime
     * @param city
     * @return
     */
    @GetMapping("/pagebycitytime")
    public R<Page<Detail>> pageByCityTime(Integer page, Integer pageSize, String startTime, String endTime,
            String city) {
        log.info("startTime: {}", startTime);
        log.info("endTime: {}", endTime);

        // 1、构造分页构造器
        Page<Detail> pageInfo = new Page<>(page, pageSize);

        // 2、构造条件构造器
        LambdaQueryWrapper<Detail> queryWrapper = new LambdaQueryWrapper<>();

        // 3、添加过滤条件
        queryWrapper.eq(StringUtils.isNotEmpty(city), Detail::getCity, city);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime formatStartTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime formatEndTime = LocalDateTime.parse(endTime, formatter);
        queryWrapper.between(Detail::getDealTime, formatStartTime, formatEndTime);

        // 4、排序条件
        queryWrapper.orderByDesc(Detail::getDealTime);

        // 5、执行查询
        detailService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 按地区时段列表查询statistics
     * 
     * @param city
     * @param startTime
     * @param endTime
     * @return
     */
    @GetMapping("/listbycitytime")
    public R<List<Statistics>> listByCityTime(String city, String startTime, String endTime) {
        log.info("city={},startTime={},endTime={}", city, startTime, endTime);

        // 1、构造条件构造器
        LambdaQueryWrapper<Statistics> queryWrapper = new LambdaQueryWrapper<>();

        // 2、添加过滤条件
        queryWrapper.between(Statistics::getYearMonth, startTime, endTime);
        if (city == null) {
            return R.error("城市不能为空");
        }
        queryWrapper.eq(Statistics::getCity, city);

        // 3、排序条件
        queryWrapper.orderByAsc(Statistics::getYearMonth);

        // 4、执行查询
        List<Statistics> listInfo = statisticsService.list(queryWrapper);

        return R.success(listInfo);
    }
}
