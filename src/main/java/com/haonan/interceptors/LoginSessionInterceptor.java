package com.haonan.interceptors;

import com.haonan.constant.UserConstant;
import com.haonan.context.BaseContext;
import com.haonan.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Session校验的拦截器
 */
@Component
@Slf4j
public class LoginSessionInterceptor implements HandlerInterceptor {


    /**
     * 校验Session
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        try {
            User loginUser = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY);
            if (loginUser == null) {
                // Todo:返回明确的json响应信息
                response.setStatus(401);
//                response.setContentType("application/json; charset=utf-8");
//                response.getOutputStream().print(new Gson().toJson(BaseResponse.error(ErrorCode.NO_LOGIN_ERROR)));
//                response.getOutputStream().flush();
                return false;
            }
            BaseContext.setCurrentUser(loginUser);
            log.info("当前用户：{}", loginUser);
            return true;
        } catch (Exception e) {
            log.info("未知错误：{}", e);
            return false;
        } finally {
//            response.getOutputStream().close();
        }
    }
}