package co.wangming.nsb.context;

import lombok.Data;

/**
 * Created By WangMing On 2019-12-22
 **/
@Data
public class ContextWrapper {

    private Class contextType;
    private Object context;
}
