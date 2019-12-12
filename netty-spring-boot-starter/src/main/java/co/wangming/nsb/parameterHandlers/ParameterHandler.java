package co.wangming.nsb.parameterHandlers;

/**
 * Created By WangMing On 2019-12-12
 **/
public interface ParameterHandler {

    Object handler(ParameterInfo parameterInfo, byte[] messageBytes);

}
