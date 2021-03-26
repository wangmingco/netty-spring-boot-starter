package co.wangming.nsb.context;

/**
 * Created By WangMing On 2019-12-22
 **/
public class ContextWrapper {

    private Class contextType;
    private Object context;

    public Class getContextType() {
        return contextType;
    }

    public void setContextType(Class contextType) {
        this.contextType = contextType;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }
}
