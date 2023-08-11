package co.wangming.nsb.common.filter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
public enum FilterChain {

    INSTANCE;

    private List<AbstractFilter> orderedFilters = new ArrayList<>();

    public void addFilter(Filter filter, Class<AbstractFilter> abstractFilterClass) {
        try {
            Constructor<AbstractFilter> constructor = abstractFilterClass.getConstructor();
            AbstractFilter abstractFilter = constructor.newInstance();

            int order = filter.order();
            Class[] classes = filter.classes();
            String[] packages = filter.packages();

            FilterCondition filterCondition = new FilterCondition(packages, classes);
            abstractFilter.setFilterCondition(filterCondition);
            abstractFilter.setOrder(order);

            orderedFilters.add(abstractFilter);
            orderedFilters.sort(Comparator.comparing(AbstractFilter::getOrder));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onBefore(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters) {
        for (AbstractFilter orderedFilter : orderedFilters) {
            orderedFilter.onBefore(filterContext, targetClass, targetMethod, parameters);
            if (!filterContext.getFilterContext().isFilterContinue()) {
                break;
            }
        }
    }

    public void onAfter(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Object result) {
        filterContext.copyContinue();
        for (AbstractFilter orderedFilter : orderedFilters) {
            orderedFilter.onAfter(filterContext, targetClass, targetMethod, parameters, result);
            if (!filterContext.getFilterContext().isFilterContinue()) {
                break;
            }
        }
    }

    public void onSystemException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
        filterContext.copyContinue();
        for (AbstractFilter orderedFilter : orderedFilters) {
            orderedFilter.onSystemException(filterContext, targetClass, targetMethod, parameters, throwable);
            if (!filterContext.getFilterContext().isFilterContinue()) {
                break;
            }
        }
    }

    public void onUserException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
        filterContext.copyContinue();
        for (AbstractFilter orderedFilter : orderedFilters) {
            orderedFilter.onUserException(filterContext, targetClass, targetMethod, parameters, throwable);
            if (!filterContext.getFilterContext().isFilterContinue()) {
                break;
            }
        }
    }
}
