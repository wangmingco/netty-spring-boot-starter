package co.wangming.nsb.common.spring;

/**
 * Created By WangMing On 2019-12-07
 **/

import co.wangming.nsb.common.filter.FilterRegistrar;
import co.wangming.nsb.server.spring.CommandControllerRegistrar;
import co.wangming.nsb.server.spring.EventRegistrar;
import co.wangming.nsb.server.spring.ProtocolProcessorRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({EventRegistrar.class, ProtocolProcessorRegistrar.class, CommandControllerRegistrar.class, FilterRegistrar.class})
@Documented
public @interface RegistrarScan {

    String[] basePackage() default {};

}
