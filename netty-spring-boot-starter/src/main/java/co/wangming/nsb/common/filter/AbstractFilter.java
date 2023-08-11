package co.wangming.nsb.common.filter;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
public abstract class AbstractFilter {

    private int order;
    private FilterCondition filterCondition;

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public void setFilterCondition(FilterCondition filterCondition) {
        this.filterCondition = filterCondition;
    }

    public boolean isExec() {
        return filterCondition.isExec();
    }

    public abstract void onBefore(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters);
    public abstract void onAfter(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Object result);
    public abstract void onSystemException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable);
    public abstract void onUserException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable);

}
