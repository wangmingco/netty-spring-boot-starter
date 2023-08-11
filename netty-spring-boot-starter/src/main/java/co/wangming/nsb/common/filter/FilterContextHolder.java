package co.wangming.nsb.common.filter;/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
public class FilterContextHolder {
    private FilterContext filterContext;

    public FilterContext getFilterContext() {
        return filterContext;
    }

    public void setFilterContext(FilterContext filterContext) {
        this.filterContext = filterContext;
    }

    public void copyContinue() {
        filterContext = FilterContext.continueFilter(filterContext.getData());
    }
}
