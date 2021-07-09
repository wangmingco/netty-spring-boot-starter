package co.wangming.nsb.server.command;

import java.lang.annotation.*;

/**
 * 命令注解, 用于注解在消息处理类以及方法上
 * <p>
 * Created By WangMing On 2019-12-06
 **/
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ScannedCommand
public @interface CommandController {


}
