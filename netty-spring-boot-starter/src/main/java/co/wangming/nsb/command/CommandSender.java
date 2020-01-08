package co.wangming.nsb.command;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

/**
 * Created By WangMing On 2020-01-02
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Autowired
public @interface CommandSender {

    String host() default "";

    int port() default -1;

}
