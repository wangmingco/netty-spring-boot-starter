package co.wangming.nsb.vo;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created By WangMing On 2019-12-08
 **/
@Data
@Builder
public class MethodInfo {

    private List<ParameterInfo> parameterInfoList;

    private Method targetMethod;

    private Class targetBeanClass;
}
