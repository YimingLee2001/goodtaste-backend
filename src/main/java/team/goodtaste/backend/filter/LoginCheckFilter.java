package team.goodtaste.backend.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;
import team.goodtaste.backend.common.R;
// import team.goodtaste.backend.utils.JwtUtils;

/*
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URL
        String requestURL = request.getRequestURI();

        log.info("拦截到请求：{}", requestURL);
        // ServletInputStream inputStream = request.getInputStream();
        // byte[] temByte = new byte[2000];
        // while (inputStream.readLine(temByte, 0, 5000) != -1) {
        // log.info("部分数据：{}", temByte);
        // }

        String[] urls = new String[] {
                "/user/login",
                "/user/signup",
                "/user/logout"
        };

        // 2、判断本次请求是否需要处理
        boolean check = check(urls, requestURL);

        // 3、如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURL);
            filterChain.doFilter(request, response);
            return;
        }

        // // ****暂时全部放行
        // filterChain.doFilter(request, response);
        // return;

        // 4、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("uid") != null) {
            // log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }

        // String token = getToken(request);
        // String nickname = JwtUtils.getClaimsByToken(token).getSubject();

        log.info("用户未登录");
        // 5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 获得head中的token
     * 
     * @param request
     * @return
     */
    // private String getToken(HttpServletRequest request) {
    // return request.getHeader("authorization");
    // }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * 
     * @param urls
     * @param requestURL
     * @return
     */
    public boolean check(String[] urls, String requestURL) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
