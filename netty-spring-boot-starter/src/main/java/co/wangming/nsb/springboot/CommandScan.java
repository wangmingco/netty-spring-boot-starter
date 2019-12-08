package co.wangming.nsb.springboot;

/**
 * Created By WangMing On 2019-12-07
 **/

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(CommandScannerRegistrar.class)
@Documented
public @interface CommandScan {

    String[] basePackage() default {};

}
