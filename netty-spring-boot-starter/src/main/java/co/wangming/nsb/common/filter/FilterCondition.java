package co.wangming.nsb.common.filter;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
public class FilterCondition {

    private String[] packages;
    private Class[] classes;

    public FilterCondition(String[] packages, Class[] classes) {
        this.packages = packages;
        this.classes = classes;
    }

    public boolean isExec() {
        return true;
    }
}
