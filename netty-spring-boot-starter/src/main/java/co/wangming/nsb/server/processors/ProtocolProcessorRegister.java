package co.wangming.nsb.server.processors;

import co.wangming.nsb.server.command.ScannedCommand;

import java.lang.annotation.*;

/**
 * Created By WangMing On 2019-12-20
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ScannedCommand
public @interface ProtocolProcessorRegister {

    Class messageType();
}
