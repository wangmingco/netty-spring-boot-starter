package co.wangming.nsb.vo;

import co.wangming.nsb.parameterHandlers.ParameterInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created By WangMing On 2019-12-08
 **/
@Data
@Builder
public class MethodInfo {

    private List<ParameterInfo> parameterInfoList;

    private String beanName;
}
