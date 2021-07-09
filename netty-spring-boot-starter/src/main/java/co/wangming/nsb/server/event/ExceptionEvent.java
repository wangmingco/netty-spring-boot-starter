package co.wangming.nsb.server.event;


/**
 * Created By WangMing On 2019-12-20
 **/
public class ExceptionEvent<T> extends AbstractEvent<T> {

    private Throwable cause;

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}
