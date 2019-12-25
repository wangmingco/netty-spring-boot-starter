package co.wangming.nsb.processors;

import java.lang.annotation.*;

/**
 * Created By WangMing On 2019-12-20
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProtocolProcessorRegister {

    Class messageType();
}
