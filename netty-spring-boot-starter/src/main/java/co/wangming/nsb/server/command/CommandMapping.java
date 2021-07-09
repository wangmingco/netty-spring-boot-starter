package co.wangming.nsb.server.command;

import java.lang.annotation.*;

/**
 * Created By WangMing On 2019-12-07
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@ScannedCommand
public @interface CommandMapping {

    long requestId();

    long responseId() default -1;

}
