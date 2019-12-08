package co.wangming.nsb.netty;

import java.lang.annotation.*;

/**
 * Created By WangMing On 2019-12-07
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CommandMapping {

    long id();

}
