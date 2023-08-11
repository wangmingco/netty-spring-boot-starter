package co.wangming.nsb.samples;

import co.wangming.nsb.common.filter.EmptyFilter;
import co.wangming.nsb.common.filter.Filter;
import co.wangming.nsb.common.filter.FilterContext;
import co.wangming.nsb.common.filter.FilterContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
@Filter
public class SimpleFilter extends EmptyFilter {

    private static final Logger log = LoggerFactory.getLogger(SimpleFilter.class);

    @Override
    public void onBefore(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters) {
        log.info("开始调用 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }

    @Override
    public void onAfter(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Object result) {
        log.info("调用完成 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }

    @Override
    public void onSystemException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
        log.error("调用异常 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }

    @Override
    public void onUserException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
        log.error("调用异常 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }
}
