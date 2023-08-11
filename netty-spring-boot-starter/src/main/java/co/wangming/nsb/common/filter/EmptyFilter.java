package co.wangming.nsb.common.filter;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
public class EmptyFilter extends AbstractFilter{

    @Override
    public void onBefore(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters) {
    }

    @Override
    public void onAfter(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Object result) {
    }

    @Override
    public void onSystemException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
    }

    @Override
    public void onUserException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
    }
}
