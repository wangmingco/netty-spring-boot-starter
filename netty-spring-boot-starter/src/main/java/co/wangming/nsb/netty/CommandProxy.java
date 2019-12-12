package co.wangming.nsb.netty;

import java.util.List;

/**
 * Created By WangMing On 2019-12-11
 **/
public interface CommandProxy {

    Object invoke(List paramters);
}
