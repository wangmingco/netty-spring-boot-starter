package co.wangming.nsb.parameterHandlers;

import com.google.protobuf.Parser;
import lombok.Builder;
import lombok.Data;

/**
 * Created By WangMing On 2019-12-08
 **/
@Data
@Builder
public class ParameterInfo {

    private Parser parser;

    Class parameterType;
}
