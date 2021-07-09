package co.wangming.nsb.common.spring;

/**
 * Created By WangMing On 2019-12-07
 **/

import co.wangming.nsb.server.spring.CommandControllerRegistrar;
import co.wangming.nsb.server.spring.NSEventRegistrar;
import co.wangming.nsb.server.spring.NSProtocolProcessorRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({NSEventRegistrar.class, NSProtocolProcessorRegistrar.class, CommandControllerRegistrar.class})
@Documented
public @interface RegistrarScan {

    String[] basePackage() default {};

}
