import co.wangming.nsb.samples.protobuf.Search;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

public class ProtobufTest {

    @Test
    public void test() {
        Assert.assertEquals(true, GeneratedMessageV3.class.isAssignableFrom(Search.SearchRequest.class));
    }

    @Test
    public void getProtobufParser() throws Exception {
        Class clazz = Search.SearchRequest.class;
        Field parserField = clazz.getDeclaredField("PARSER");
        parserField.setAccessible(true);
        Parser parser = (Parser) parserField.get(clazz);

        Assert.assertNotNull(parser);
    }
}
