package co.wangming.nsb.server.command;

import java.lang.annotation.*;

/**
 * 用于spring扫描的注解类
 * <p>
 * Created By WangMing On 2020-01-02
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ScannedCommand {
}
