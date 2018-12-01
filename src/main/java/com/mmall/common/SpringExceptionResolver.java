package com.mmall.common;

import com.mmall.exception.ParamException;
import com.mmall.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String url = request.getRequestURL().toString();
        ModelAndView mv;

        String defaultMsg = "System error";

        //所有的json请求以.json结尾,所有的页面请求以.page结尾
        if (url.endsWith(".json")){
            if (ex instanceof PermissionException || ex instanceof ParamException){
                JsonData result = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("jsonView",result.toMap());
            }else {
                log.error("unknow json exception,url:"+url,ex);
                JsonData result = JsonData.fail(defaultMsg);
                mv = new ModelAndView("jsonView",result.toMap());
            }
        }else if (url.endsWith(".page")){
            log.error("unknow page exception,url:"+url,ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception",result.toMap());
        }else {
            log.error("unknow exception,url:"+url,ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView",result.toMap());
        }
        return mv;
    }
}
