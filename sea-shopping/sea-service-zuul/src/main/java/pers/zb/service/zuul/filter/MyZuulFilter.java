package pers.zb.service.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

/**
 * zuul网关过滤器
 * zuul不仅只是路由，并且还能过滤，做一些安全验证。
 * 可以通过shouldFilter()方法返回值为false，来标明过滤器是否起作用
 */
@Component
public class MyZuulFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger(MyZuulFilter.class);

    /**
     * filter类型。
     * 分为以下几种：
     * pre:请求执行之前filter
     * route: 处理请求，进行路由
     * post: 请求处理完成后执行的filter
     * error:出现错误时执行的filter
     *
     * @return
     */
    @Override
    public String filterType() {
        // 在路由之前进行过滤
        System.out.println("==========filterType=============");
        return FilterConstants.PRE_TYPE;
    }

    /**
     * filter执行顺序，通过数字指定
     *  1.按照filterType决定顺序 按照filterType决定顺序 Pre 优先 Post执行，此时filterOrder没有作用。
     *  2.filterType相同  filterOrder有作用，数字越小，越先执行。（负数也是这个规则，0和-1的话，-1先执行）
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     *  shouldFilter 是否执行该过滤器，true代表需要过滤
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * filter具体逻辑
     *
     * @return
     */
    @Override
    public Object run() {
        System.out.println("==========run=============");

        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        log.info("send {} request to {}", request.getMethod(), request.getRequestURL().toString());

        //这里就简单的判断一下是否有token参数，只作为测试
        //获取传来的参数token
        Object accessToken = request.getParameter("token");
        if (accessToken == null) {
            log.warn("token is empty");
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(401);
            try {
                HttpServletResponse res = ctx.getResponse();
                res.getWriter().write("token is empty");
            } catch (Exception e) {
            }

            return null;
        }
        log.info("自定义zuul过滤器处理完成 ok！");
        //这里return的值没有意义，zuul框架没有使用该返回值
        return null;
    }
}