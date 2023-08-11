package co.wangming.nsb.common.filter;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
public class FilterContext<T> {

    private boolean isInvokeContinue = true;
    private boolean isFilterContinue;

    private T data;

    public boolean isFilterContinue() {
        return isFilterContinue;
    }

    public T getData() {
        return data;
    }

    public static <T> FilterContext continueFilter() {
        return continueFilter(null);
    }

    public static <T> FilterContext continueFilter(T t) {
        FilterContext filterContext = new FilterContext();
        filterContext.isFilterContinue = true;
        filterContext.data = t;
        return filterContext;
    }

    public static <T> FilterContext stopFilter() {
        FilterContext filterContext = new FilterContext();
        filterContext.isFilterContinue = false;
        return filterContext;
    }

    public boolean isInvokeContinue() {
        return isInvokeContinue;
    }

    public void setInvokeContinue(boolean invokeContinue) {
        isInvokeContinue = invokeContinue;
    }
}
