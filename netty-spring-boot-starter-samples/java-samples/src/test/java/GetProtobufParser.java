import co.wangming.nsb.samples.protobuf.Search;
import com.google.protobuf.Parser;

import java.lang.reflect.Field;

/**
 * Created By WangMing On 2019-12-20
 **/
public class GetProtobufParser {

    public static void main(String[] args) throws Exception {

        Class clazz = Search.SearchRequest.class.getSuperclass();
        Field parserField = clazz.getDeclaredField("PARSER");
        parserField.setAccessible(true);
        Parser parser = (Parser) parserField.get(clazz);

    }
}
