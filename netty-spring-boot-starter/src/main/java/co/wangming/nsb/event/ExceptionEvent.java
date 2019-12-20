package co.wangming.nsb.event;

import lombok.Data;

/**
 * Created By WangMing On 2019-12-20
 **/
@Data
public class ExceptionEvent<T> extends AbstractEvent<T> {

    private Throwable cause;
}
