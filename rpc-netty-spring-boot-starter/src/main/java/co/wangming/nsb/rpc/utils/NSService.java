package co.wangming.nsb.rpc.utils;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Created by wangming on 2017/6/3.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NSService {

    String serviceName() default "";

    NSType rpcCallType() default NSType.LOCAL;
}
