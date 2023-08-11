package co.wangming.nsb.common.filter;

import co.wangming.nsb.server.command.ScannedCommand;

import java.lang.annotation.*;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ScannedCommand
public @interface Filter {

    int order() default 0;

    String[] packages() default {};

    Class[] classes() default {};
}
