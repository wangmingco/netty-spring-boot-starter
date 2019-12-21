package co.wangming.nsb.parsers;

import java.lang.annotation.*;

/**
 * Created By WangMing On 2019-12-20
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ParserRegister {

    Class messageType();
}
