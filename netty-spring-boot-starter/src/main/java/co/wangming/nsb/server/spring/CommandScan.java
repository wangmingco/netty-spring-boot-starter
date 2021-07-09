package co.wangming.nsb.server.spring;

/**
 * Created By WangMing On 2019-12-07
 **/

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({CommonScannerRegistrar.class, CommandProxyScannerRegistrar.class})
@Documented
public @interface CommandScan {

    String[] basePackage() default {};

}
